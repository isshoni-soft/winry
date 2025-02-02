package institute.isshoni.winry.api.bootstrap;

import institute.isshoni.araragi.concurrent.collection.ConcurrentLinkedList;
import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.logging.model.ILoggerFactory;
import institute.isshoni.araragi.reflect.ReflectionUtil;
import institute.isshoni.araragi.stream.Streams;
import institute.isshoni.winry.api.annotation.Bootstrap;
import institute.isshoni.winry.api.async.IWinryAsyncManager;
import institute.isshoni.winry.api.bootstrap.executable.BackloadExecutable;
import institute.isshoni.winry.api.bootstrap.executable.IExecutable;
import institute.isshoni.winry.api.context.IBootstrapContext;
import institute.isshoni.winry.api.context.IExceptionManager;
import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.api.meta.IAnnotatedClass;
import institute.isshoni.winry.api.meta.ISingletonAnnotatedClass;
import institute.isshoni.winry.internal.WinryContext;
import institute.isshoni.winry.internal.annotation.manage.WinryAnnotationManager;
import institute.isshoni.winry.internal.event.WinryEventBus;
import institute.isshoni.winry.internal.meta.InstanceManager;
import institute.isshoni.winry.internal.meta.MetaManager;
import institute.isshoni.winry.internal.model.bootstrap.IBootstrapper;
import institute.isshoni.winry.internal.model.meta.IAnnotatedMeta;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class WinryBootstrapper implements IBootstrapper {

    private static AraragiLogger LOGGER;

    private final IWinryContext context;

    private final List<IExecutable> executed;

    private Map<Class<?>, Object> provided;

    private Class<?> bootstrapped;

    private IExecutable currentExecutable;

    public WinryBootstrapper(Bootstrap bootstrap, IBootstrapContext bootstrapContext) {
        this.executed = new ConcurrentLinkedList<>();

        IWinryAsyncManager asyncManager = bootstrapContext.getAsyncManager();
        ILoggerFactory loggerFactory = bootstrapContext.getLoggerFactory();
        WinryAnnotationManager annotationManager = new WinryAnnotationManager(bootstrap, loggerFactory, this);
        IExceptionManager exceptionManager = annotationManager.getExceptionManager();
        MetaManager metaManager = new MetaManager(loggerFactory);
        InstanceManager instanceManager = new InstanceManager(loggerFactory, metaManager);
        WinryEventBus eventBus = new WinryEventBus(asyncManager, loggerFactory, metaManager, annotationManager,
                exceptionManager, instanceManager);

        this.context = WinryContext.builder(bootstrap, this)
                .bootstrapContext(bootstrapContext)
                .exceptionManager(exceptionManager)
                .metaManager(metaManager)
                .instanceManager(instanceManager)
                .annotationManager(annotationManager)
                .loggerFactory(loggerFactory)
                .asyncManager(asyncManager)
                .eventBus(eventBus)
                .build();

        exceptionManager.getContext().set(this.context);

        LOGGER = this.context.getLoggerFactory().createLogger("WinryBootstrapper");
    }

    @Override
    public IWinryContext getContext() {
        return this.context;
    }

    @Override
    public Map<Class<?>, Object> getProvided() {
        return Collections.unmodifiableMap(this.provided);
    }

    @Override
    public void bootstrap(Bootstrap bootstrap, Class<?> clazz, Map<Class<?>, Object> provided) {
        this.bootstrapped = clazz;
        this.provided = Collections.unmodifiableMap(provided);

        LOGGER.debug("Registering provided instances...");
        provided.forEach((c, o) -> {
            IAnnotatedClass classMeta;
            try {
                classMeta = this.context.getMetaManager().generateSingletonMeta(c, o);
            } catch (Throwable e) {
                this.context.getExceptionManager().toss(e);
                return;
            }

            this.context.registerToContext(o);
            this.context.getInstanceManager().registerSingletonInstance(classMeta, o);
        });

        LOGGER.debug("${a:dashes%50} Bootstrapping ${a:dashes%50}");
        this.context.getAnnotationManager().initialize(this.context);
        LOGGER.debug("Finished class discovery and instantiation...");

        LOGGER.debug("Recompiling annotations of provided bootstrapped classes...");
        provided.forEach((c, o) -> this.context.getMetaManager().getSingletonMeta(c).regenerate());

        List<IExecutable> run = compileRunList();
        Streams.to(this.context.getAnnotationManager().getAllProviders(bootstrap))
                .map(ReflectionUtil::construct)
                .peek(this.context::registerToContext)
                .flatMap(p -> Optional.ofNullable(p.provideExecutables(this.context))
                        .orElse(new LinkedList<>()).stream())
                .peek(this.context::registerToContext)
                .peek(p -> LOGGER.debug("Injecting Executable: ${0}", p.getDisplay()))
                .addTo(run);

        run = fuseExecutables(run);

        LOGGER.debug("${a:dashes%50} Initial Run Order ${a:dashes%50}");
        run.forEach(r -> LOGGER.debug(r.getDisplay()));
        LOGGER.debug("${a:dashes%130}");

        LOGGER.info("${a:dashes%50} Execution ${a:dashes%50}");
        execute(run, this.executed);
    }

    @Override
    public void backload() {
        LOGGER.debug("Triggering backload ...");

        BackloadExecutable executable = new BackloadExecutable(this.currentExecutable);

        this.context.registerExecutable(executable);

        this.executed.add(this.currentExecutable);
        forkExecution(this.executed, executable);
    }

    @SafeVarargs
    @Override
    public final void reprocess(Class<? extends Annotation>... annotations) {
        Streams.to(this.executed)
                .filter(c -> IAnnotatedMeta.class.isAssignableFrom(c.getClass()))
                .map(c -> (IAnnotatedMeta) c)
                .filter(m -> m.hasAnnotations(annotations))
                .forEach(IExecutable::execute);
    }

    public List<IExecutable> fuseExecutables(List<IExecutable> run) {
        return Streams.to(run)
                .add(this.context.getExecutables())
                .distinct()
                .sorted()
                .toList();
    }

    public void execute(List<IExecutable> executables, List<IExecutable> executed) {
        execute(executables, executed, null);
    }

    public void execute(List<IExecutable> executables, List<IExecutable> executed, BackloadExecutable backloaded) {
        boolean broken = false;
        for (IExecutable executable : executables) {
            this.currentExecutable = executable;
            List<IExecutable> prevExecs = new LinkedList<>(this.context.getExecutables());
            LOGGER.info("Executing: " + executable.getDisplay());

            if (executable instanceof BackloadExecutable && Objects.nonNull(backloaded)) {
                executed.add(executable);
                LOGGER.info("-> Terminating backload fork.");
                return;
            } else {
                executable.execute();
                executed.add(executable);
            }

            if (prevExecs.size() != this.context.getExecutables().size() ||
                    !Streams.to(this.context.getExecutables()).matches(prevExecs, Object::equals)) {
                LOGGER.info("-> Detected registered executable changed, forking executable.");
                broken = true;
                break;
            }
        }

        if (broken) {
            forkExecution(executed, backloaded);
        }
    }

    private void forkExecution(List<IExecutable> executed, BackloadExecutable backloaded) {
        List<IExecutable> executables = fuseExecutables(compileRunList());
        List<IExecutable> newList = Streams.to(executables)
                .filterInverted(executed::contains)
                .sorted()
                .toList();
        LOGGER.info("-> New executable list size: " + newList.size() + "; pruned: " + executed.size() + " (total: " + executables.size() + ")...");
        execute(newList, executed, backloaded);
    }

    @Override
    public List<IExecutable> compileRunList() {
        LOGGER.debug("Compiling run order...");
        return Streams.to(this.context.getMetaManager().getAllSingletonClasses())
                .peek(ISingletonAnnotatedClass::regenerate)
                .expand(IExecutable.class, IAnnotatedClass::getMethods, IAnnotatedClass::getFields)
                .collect(Collectors.toList());
    }

    @Override
    public Class<?> getBootstrapped() {
        return this.bootstrapped;
    }
}

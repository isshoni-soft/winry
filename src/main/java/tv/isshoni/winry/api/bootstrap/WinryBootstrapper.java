package tv.isshoni.winry.api.bootstrap;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.annotation.Bootstrap;
import tv.isshoni.winry.api.async.IWinryAsyncManager;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.context.WinryContext;
import tv.isshoni.winry.internal.annotation.manage.WinryAnnotationManager;
import tv.isshoni.winry.internal.event.WinryEventBus;
import tv.isshoni.winry.internal.logging.LoggerFactory;
import tv.isshoni.winry.internal.meta.InstanceManager;
import tv.isshoni.winry.internal.meta.MetaManager;
import tv.isshoni.winry.internal.model.bootstrap.IBootstrapper;
import tv.isshoni.winry.internal.model.exception.IExceptionManager;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WinryBootstrapper implements IBootstrapper {

    private static AraragiLogger LOGGER;

    private final IWinryContext context;

    private Map<Class<?>, Object> provided;

    private Class<?> bootstrapped;

    public WinryBootstrapper(Bootstrap bootstrap, IWinryAsyncManager asyncManager) {
        LoggerFactory loggerFactory = new LoggerFactory();
        loggerFactory.setDefaultLoggerLevel(bootstrap.defaultLevel());
        WinryAnnotationManager annotationManager = new WinryAnnotationManager(bootstrap, loggerFactory, this);
        IExceptionManager exceptionManager = annotationManager.getExceptionManager();
        MetaManager metaManager = new MetaManager(loggerFactory);

        this.context = WinryContext.builder(bootstrap, this)
                .exceptionManager(exceptionManager)
                .metaManager(metaManager)
                .instanceManager(new InstanceManager(loggerFactory, metaManager))
                .annotationManager(annotationManager)
                .loggerFactory(loggerFactory)
                .asyncManager(asyncManager)
                .eventBus(new WinryEventBus(asyncManager, loggerFactory, annotationManager, annotationManager.getExceptionManager()))
                .build();

        LOGGER = this.context.getLoggerFactory().createLogger("SimpleBootstrapper");
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
            IAnnotatedClass classMeta = this.context.getMetaManager().generateMeta(c, o);

            this.context.registerToContext(o);
            this.context.getInstanceManager().registerSingletonInstance(classMeta, o);
        });

        LOGGER.debug("${dashes%50} Bootstrapping ${dashes%50}");
        this.context.getAnnotationManager().initialize();
        LOGGER.debug("Finished class discovery and instantiation...");

        LOGGER.debug("Recompiling annotations of provided bootstrapped classes...");
        provided.forEach((c, o) -> this.context.getMetaManager().getMeta(c).regenerate());

        List<IExecutable> run = compileRunList();
        Streams.to(this.context.getAnnotationManager().getAllProviders(bootstrap))
                .map(ReflectionUtil::construct)
                .peek(this.context::registerToContext)
                .flatMap(p -> Optional.ofNullable(p.provideExecutables(this.context))
                        .orElse(new LinkedList<>()).stream())
                .peek(this.context::registerToContext)
                .peek(p -> LOGGER.debug("Injecting Executable: " + p.getDisplay()))
                .addTo(run);

        run = fuseExecutables(run);

        LOGGER.debug("${dashes%50} Initial Run Order ${dashes%50}");
        run.forEach(r -> LOGGER.debug(r.getDisplay()));
        LOGGER.debug("${dashes%50}${dashes%19}${dashes%50}");

        execute(run, new LinkedList<>());

        this.context.getAsyncManager().awaitAsyncTermination();
    }

    public List<IExecutable> fuseExecutables(List<IExecutable> run) {
        return Streams.to(run)
                .add(this.context.getExecutables())
                .distinct()
                .sorted()
                .toList();
    }

    public void execute(List<IExecutable> executables, List<IExecutable> executed) {
        LOGGER.info("${dashes%50} Execution ${dashes%50}");

        boolean broken = false;
        for (IExecutable executable : executables) {
            List<IExecutable> prevExecs = new LinkedList<>(this.context.getExecutables());
            LOGGER.info("Executing: " + executable.getDisplay());
            executable.execute();
            executed.add(executable);

            // TODO: Check for change in executable in list.
            if (prevExecs.size() != this.context.getExecutables().size() ||
                    !new HashSet<>(this.context.getExecutables()).containsAll(prevExecs)) {
                LOGGER.info("----------> Detected new executable registration, preforming hotswap...");
                broken = true;
                break;
            }
        }

        if (broken) {
            executables = fuseExecutables(compileRunList());
            List<IExecutable> newList = Streams.to(executables)
                    .filterInverted(executed::contains)
                    .toList();
            LOGGER.info("----------> New executable list size: " + newList.size() + "; pruned: " + executed.size() + " (total: " + executables.size() + ")...");
            execute(newList, executed);
        }
    }

    @Override
    public List<IExecutable> compileRunList() {
        LOGGER.debug("Compiling run order...");
        return Streams.to(this.context.getMetaManager().getAllClasses())
                .peek(IAnnotatedClass::regenerate)
                .expand(IExecutable.class, IAnnotatedClass::getMethods, IAnnotatedClass::getFields)
                .collect(Collectors.toList());
    }

    @Override
    public Class<?> getBootstrapped() {
        return this.bootstrapped;
    }
}

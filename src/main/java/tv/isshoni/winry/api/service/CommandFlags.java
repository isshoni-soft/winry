package tv.isshoni.winry.api.service;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.api.context.IWinryContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// possible expansion here:
// - Bind flags & parameters to argument level i.e. cmd -foo help -advanced pull
//                                  (foo binds to help and advanced binds to pull)
@Injected
public class CommandFlags implements IContextual {

    private final Constant<IWinryContext> context;

    private final Set<String> flags;

    private final Map<String, String> parameters;

    private final List<String> arguments;

    public CommandFlags(@Context IWinryContext context) {
        this.context = new Constant<>(context);
        this.flags = new HashSet<>();
        this.parameters = new HashMap<>();
        this.arguments = new LinkedList<>();

        String[] programArguments = context.getBootstrapContext().getArguments();

        for (String argument : programArguments) {
            if (argument.startsWith("-")) {
                if (!argument.contains("=")) {
                    this.flags.add(argument.substring(1));
                } else {
                    String[] split = argument.split("=");
                    this.parameters.put(split[0], split[1]);
                }
            } else {
                this.arguments.add(argument);
            }
        }
    }

    public boolean hasFlag(String flag) {
        return this.flags.contains(flag);
    }

    public boolean hasParameter(String key) {
        return this.parameters.containsKey(key);
    }

    public String getParameter(String key) {
        return this.parameters.get(key);
    }

    public Set<String> getFlags() {
        return Collections.unmodifiableSet(this.flags);
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(this.parameters);
    }

    public List<String> getArguments() {
        return Collections.unmodifiableList(this.arguments);
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}

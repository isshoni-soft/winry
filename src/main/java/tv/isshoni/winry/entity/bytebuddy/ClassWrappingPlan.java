package tv.isshoni.winry.entity.bytebuddy;

import tv.isshoni.winry.entity.element.BootstrappedClass;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClassWrappingPlan {

    private final BootstrappedClass bootstrappedClass;

    private final List<WrappedMethod> methods;

    public ClassWrappingPlan(BootstrappedClass bootstrappedClass) {
        this.bootstrappedClass = bootstrappedClass;
        this.methods = new LinkedList<>();
    }

    public BootstrappedClass getBootstrappedClass() {
        return this.bootstrappedClass;
    }

    public List<WrappedMethod> getMethods() {
        return Collections.unmodifiableList(this.methods);
    }
}

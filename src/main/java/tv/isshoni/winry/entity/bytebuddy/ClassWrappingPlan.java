package tv.isshoni.winry.entity.bytebuddy;

import tv.isshoni.winry.entity.element.BootstrappedClass;

public class ClassWrappingPlan {

    private final BootstrappedClass bootstrappedClass;

//    private final List<>

    public ClassWrappingPlan(BootstrappedClass bootstrappedClass) {
        this.bootstrappedClass = bootstrappedClass;
    }

    public BootstrappedClass getBootstrappedClass() {
        return this.bootstrappedClass;
    }

}

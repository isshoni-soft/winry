package tv.isshoni.winry.api.entity.executable;

import tv.isshoni.winry.entity.bootstrap.element.IBootstrappedElement;

public interface IExecutable extends Comparable<IExecutable> {

    int getWeight();

    void execute();

    String getDisplay();

    default int compareTo(IExecutable value) {
        return Integer.compare(value.getWeight(), this.getWeight());
    }
}

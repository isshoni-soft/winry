package institute.isshoni.winry.internal.model.meta;

import institute.isshoni.winry.api.bootstrap.executable.IExecutable;

public interface IWeighted extends Comparable<IExecutable> {

    int getWeight();

    default int compareTo(IExecutable value) {
        return Integer.compare(value.getWeight(), this.getWeight());
    }
}

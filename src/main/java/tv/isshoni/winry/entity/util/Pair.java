package tv.isshoni.winry.entity.util;

import com.google.common.base.Preconditions;

import java.util.Objects;

public class Pair<F, S> {

    private final F first;

    private final S second;

    public Pair(F first, S second) {
        Preconditions.checkNotNull(first, second);

        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return this.first;
    }

    public S getSecond() {
        return this.second;
    }

    @Override
    public String toString() {
        return "Pair[first=" + this.first.toString() + ",second=" + this.second.toString() + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Pair)) {
            return false;
        }

        Pair otherPair = (Pair) object;

        return this.first.equals(otherPair.first) && this.second.equals(otherPair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }
}

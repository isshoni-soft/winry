package tv.isshoni.winry.api.bootstrap.executable;

public class BackloadExecutable implements IExecutable {

    private static int ID = 0;

    private final int id;

    private final IExecutable wrapped;

    public BackloadExecutable(IExecutable wrapped) {
        this.id = ID++;
        this.wrapped = wrapped;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void execute() { /* nothing */ }

    @Override
    public String getDisplay() {
        return this.wrapped.getDisplay();
    }

    @Override
    public int getWeight() {
        return this.wrapped.getWeight();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BackloadExecutable other)) {
            return false;
        }

        return other.id == this.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}

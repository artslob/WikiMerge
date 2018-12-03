package ifmo.jackalope.ruthes.entries;

abstract class RuthesEntry implements Entry {
    private final String id;
    private final String name;

    RuthesEntry(String id, String name) {
        if (id == null || name == null)
            throw new IllegalStateException("Id or name of entry is null.");
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.id, this.name);
    }
}

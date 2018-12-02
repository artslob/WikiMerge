package ifmo.jackalope.ruthes.entries;

abstract class Entry {
    final String id;
    final String name;

    Entry(String id, String name) {
        if (id == null || name == null)
            throw new IllegalStateException("Id or name of entry is null.");
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.id, this.name);
    }
}

package ifmo.jackalope.ruthes.entries;

abstract class Entry {
    String id;
    String name;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}

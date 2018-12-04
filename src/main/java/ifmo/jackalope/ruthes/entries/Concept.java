package ifmo.jackalope.ruthes.entries;

public class Concept extends RuthesEntry {
    private final String gloss;
    private final String domain;

    private Concept(Builder builder) {
        super(builder.id, builder.name);
        this.gloss = builder.gloss;
        this.domain = builder.domain;
    }

    public static class Builder {
        private String id;
        private String name;
        private String gloss;
        private String domain;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setGloss(String gloss) {
            this.gloss = gloss;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public Concept build() {
            return new Concept(this);
        }
    }

    public String getGloss() {
        return gloss;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return String.format("Concept - %s", super.toString());
    }
}

package ifmo.jackalope.ruthes.entries;

import java.util.ArrayList;
import java.util.List;

public class Concept extends Entry {
    private final String gloss;
    private final String domain;
    /* this -> from; rel.concept -> to; rel.type -> type */
    private final List<Relation> relations = new ArrayList<>();
    /* hold entries */
    private final List<TextEntry> synonyms = new ArrayList<>();

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

    public List<Relation> getRelations() {
        return relations;
    }

    public List<TextEntry> getSynonyms() {
        return synonyms;
    }

    @Override
    public String toString() {
        return String.format("Concept - %s", super.toString());
    }
}

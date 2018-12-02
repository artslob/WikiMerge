package ifmo.jackalope.ruthes.entries;

import java.util.ArrayList;
import java.util.List;

public class Concept extends Entry {
    private String gloss;
    private String domain;
    /* this -> from; rel.concept -> to; rel.type -> type */
    private List<Relation> relations = new ArrayList<>();
    /* hold entries */
    private List<TextEntry> synonyms = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGloss() {
        return gloss;
    }

    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    public List<TextEntry> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<TextEntry> synonyms) {
        this.synonyms = synonyms;
    }

    public String toString() {
        return String.format("%s %s", this.id, this.name);
    }
}

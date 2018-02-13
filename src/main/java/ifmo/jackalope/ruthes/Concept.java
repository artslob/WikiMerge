package ifmo.jackalope.ruthes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Concept {
    private String id;
    private String name;
    private String gloss;
    private String domain;
    /* this.id -> from; map.key -> to.id; map.value -> name */
    private Map<String, RelationType> relations = new HashMap<>();
    /* hold entries id */
    private List<String> synonyms = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
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

    public Map<String, RelationType> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, RelationType> relations) {
        this.relations = relations;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public String toString() {
        return String.format("%s %s", this.id, this.name);
    }
}

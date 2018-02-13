package ifmo.jackalope.ruthes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Concept {
    private int id = 0;
    private String name;
    private String gloss;
    private String domain;
    /* this.id -> from; map.key -> to; map.value -> name */
    private Map<Integer, RelationType> relations = new HashMap<>();
    /* hold entries id */
    private List<Integer> synonyms = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Map<Integer, RelationType> getRelations() {
        return relations;
    }

    public void setRelations(Map<Integer, RelationType> relations) {
        this.relations = relations;
    }

    public List<Integer> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<Integer> synonyms) {
        this.synonyms = synonyms;
    }
}

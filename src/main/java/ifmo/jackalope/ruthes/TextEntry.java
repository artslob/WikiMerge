package ifmo.jackalope.ruthes;

import java.util.ArrayList;
import java.util.List;

public class TextEntry {
    private String id;
    private String name;
    private String lemma;
    private String main_word;
    private String synt_type;
    private String pos_string;
    /* hold concepts id */
    private List<Concept> synonyms = new ArrayList<>();

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

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getMain_word() {
        return main_word;
    }

    public void setMain_word(String main_word) {
        this.main_word = main_word;
    }

    public String getSynt_type() {
        return synt_type;
    }

    public void setSynt_type(String synt_type) {
        this.synt_type = synt_type;
    }

    public String getPos_string() {
        return pos_string;
    }

    public void setPos_string(String pos_string) {
        this.pos_string = pos_string;
    }

    public List<Concept> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<Concept> synonyms) {
        this.synonyms = synonyms;
    }

    public String toString() {
        return String.format("%s %s", this.id, this.name);
    }
}

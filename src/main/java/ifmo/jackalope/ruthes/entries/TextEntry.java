package ifmo.jackalope.ruthes.entries;

import java.util.ArrayList;
import java.util.List;

public class TextEntry extends Entry {
    private final String lemma;
    private final String main_word;
    private final String synt_type;
    private final String pos_string;
    /* hold concepts id */
    private final List<Concept> synonyms = new ArrayList<>();

    private TextEntry(Builder builder) {
        super(builder.id, builder.name);
        this.lemma = builder.lemma;
        this.main_word = builder.main_word;
        this.synt_type = builder.synt_type;
        this.pos_string = builder.pos_string;
    }

    public static class Builder {
        private String id;
        private String name;
        private String lemma;
        private String main_word;
        private String synt_type;
        private String pos_string;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setLemma(String lemma) {
            this.lemma = lemma;
        }

        public void setMain_word(String main_word) {
            this.main_word = main_word;
        }

        public void setSynt_type(String synt_type) {
            this.synt_type = synt_type;
        }

        public void setPos_string(String pos_string) {
            this.pos_string = pos_string;
        }

        public TextEntry build() {
            return new TextEntry(this);
        }
    }

    public String getLemma() {
        return lemma;
    }

    public String getMain_word() {
        return main_word;
    }

    public String getSynt_type() {
        return synt_type;
    }

    public String getPos_string() {
        return pos_string;
    }

    public List<Concept> getSynonyms() {
        return synonyms;
    }

    public String toString() {
        return String.format("%s %s", this.id, this.name);
    }
}

package ifmo.jackalope.ruthes.entries;

public class TextEntry extends RuthesEntry {
    private final String lemma;
    private final String main_word;
    private final String synt_type;
    private final String pos_string;

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

        public void setMainWord(String main_word) {
            this.main_word = main_word;
        }

        public void setSyntType(String synt_type) {
            this.synt_type = synt_type;
        }

        public void setPosString(String pos_string) {
            this.pos_string = pos_string;
        }

        public TextEntry build() {
            return new TextEntry(this);
        }
    }

    public String getLemma() {
        return lemma;
    }

    public String getMainWord() {
        return main_word;
    }

    public String getSyntType() {
        return synt_type;
    }

    public String getPosString() {
        return pos_string;
    }

    @Override
    public String toString() {
        return String.format("Text Entry - %s", super.toString());
    }
}

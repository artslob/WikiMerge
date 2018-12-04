package ifmo.jackalope.ruthes.entries;

public enum RelationType {
    HYPONYM("НИЖЕ") {
        public RelationType getConverseRelation() {
            return HYPERNYM;
        }
    },
    HYPERNYM("ВЫШЕ") {
        public RelationType getConverseRelation() {
            return HYPONYM;
        }
    },
    HOLONYM("ЦЕЛОЕ") {
        public RelationType getConverseRelation() {
            return MERONYM;
        }
    },
    MERONYM("ЧАСТЬ") {
        public RelationType getConverseRelation() {
            return HOLONYM;
        }
    },
    /*  несимметричная ассоциация
     *  понятие имеет зависимые от него понятия
     */
    HAS_DEPEND("АСЦ2") {
        public RelationType getConverseRelation() {
            return DEPEND_ON;
        }
    },
    /*  несимметричная ассоциация
     *  существование понятия зависит от другого понятия
     */
    DEPEND_ON("АСЦ1") {
        public RelationType getConverseRelation() {
            return HAS_DEPEND;
        }
    },
    /* симметричная ассоциация */
    SYM_ASSOC("АСЦ"),
    SYNONYM("SYNONYM");

    public String typeName;

    RelationType(String typeName) {
        this.typeName = typeName;
    }

    public RelationType getConverseRelation() {
        return null;
    }

    public static RelationType fromString(String name) {
        for (RelationType rt : RelationType.values()) {
            if (rt.typeName.equalsIgnoreCase(name)) {
                return rt;
            }
        }
        throw new IllegalArgumentException("No constant with text " + name + " found");
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.name(), this.typeName);
    }
}

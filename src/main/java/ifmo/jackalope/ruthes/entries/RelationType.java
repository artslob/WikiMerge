package ifmo.jackalope.ruthes.entries;

/**
 * Тип отношений между понятиями RuThes. В основном представлен отношениями из файла relations.xml, поэтому
 * изначально связывал между собой только понятия вида Concept.
 * Однако расширен следующими типами отношений для единообразия интерфейса между концептом и текстовым вхождением.
 * 1. SYNONYM.
 * <p>
 * Например, эквивалентные записи:
 * from = C1, to = C2, name = ВЫШЕ
 * С1 -> ВЫШЕ -> С2
 * С2 ВЫШЕ С1
 * С1 НИЖЕ С2
 * C2 - более общее, а C1 - частное
 * все экземпляры понятия С1 являются экземплярами понятия С2
 * C1="ЭНЕРГЕТИКА (СФЕРА ДЕЯТЕЛЬНОСТИ)" -> ВЫШЕ -> C2="ОТРАСЛЬ, ПРОИЗВОДЯЩАЯ ТОВАРЫ"
 */
public enum RelationType {
    /**
     * Родовидовое отношение. C2 является более частным понятием.
     * ЭЛЕКТРОСТАНЦИЯ -> НИЖЕ -> ГИДРОЭЛЕКТРОСТАНЦИЯ
     */
    HYPONYM("НИЖЕ") {
        public RelationType getConverseRelation() {
            return HYPERNYM;
        }
    },
    /**
     * Родовидовое отношение. C2 является более общем понятием.
     * ЭНЕРГЕТИКА (СФЕРА ДЕЯТЕЛЬНОСТИ) -> ВЫШЕ -> ОТРАСЛЬ, ПРОИЗВОДЯЩАЯ ТОВАРЫ
     */
    HYPERNYM("ВЫШЕ") {
        public RelationType getConverseRelation() {
            return HYPONYM;
        }
    },
    /**
     * Под частями понимаются разного рода внутренние отношения сущностей, такие как физические части, части-процессы,
     * роли, участники ситуации, свойства и атрибуты и т.д.
     * C2 включает в себя C1.
     * ДОМАШЕЕ ХОЗЯЙСТВО -> ЦЕЛОЕ -> БЫТОВАЯ СФЕРА
     */
    HOLONYM("ЦЕЛОЕ") {
        public RelationType getConverseRelation() {
            return MERONYM;
        }
    },
    /**
     * Под частями понимаются разного рода внутренние отношения сущностей, такие как физические части, части-процессы,
     * роли, участники ситуации, свойства и атрибуты и т.д.
     * C2 часть C1.
     * ДОМАШЕЕ ХОЗЯЙСТВО -> ЧАСТЬ -> РАБОТА НА ДОМУ
     */
    MERONYM("ЧАСТЬ") {
        public RelationType getConverseRelation() {
            return HOLONYM;
        }
    },
    /**
     * Несимметричная ассоциация. Понятие имеет зависимые от него понятия.
     * С1 имеет зависимое С2. Существование понятия C2 зависит от существования понятия С1.
     * МЕДИЦИНА -> АСЦ2 -> МЕДИЦИНСКОЕ ОБРАЗОВАНИЕ
     */
    HAS_DEPEND("АСЦ2") {
        public RelationType getConverseRelation() {
            return DEPEND_ON;
        }
    },
    /**
     * Несимметричная ассоциация.
     * С1 зависит от C2. Существование понятия C1 зависит от существования понятия С2.
     * МЕДИЦИНА -> АСЦ1 -> ПОВРЕЖДЕНИЕ ЗДОРОВЬЯ
     */
    DEPEND_ON("АСЦ1") {
        public RelationType getConverseRelation() {
            return HAS_DEPEND;
        }
    },
    /**
     * Симметричная ассоциация. Устанавливается в ограниченном числе случаев для некоторых видов антонимов,
     * для близких понятий-подвидов одного и того же понятия (босоножки-сандалии), конверсивы (покупка-продажа) и др.
     * ЗАКОННОСТЬ <-> АСЦ <-> ЗАКОНОДАТЕЛЬСТВО
     */
    SYM_ASSOC("АСЦ"),
    /**
     * Синоним. Связывает между собой концепты и текстовые вхождения. К одному концепту могут относиться несколько
     * текстовых входов (синонимы), текстовый вход может относиться к нескольким понятиям (лексическая неоднозначность).
     * ИЗНОС ОБОРУДОВАНИЯ <-> SYNONYM <-> ИЗНОС ТЕХНИКИ
     */
    SYNONYM("SYNONYM");

    private final String typeName;

    RelationType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
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

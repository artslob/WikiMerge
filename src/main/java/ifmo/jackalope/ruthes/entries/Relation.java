package ifmo.jackalope.ruthes.entries;

/**
 * Класс, обозначающий связь между 2 понятиями словаря RuThes. Отношение имеет направление и тип.
 * <p>
 * Направление:
 * Объект вида Entry, имеющий набор отношений, является исходым понятием, от которого направлено отношение.
 * Объект же данного класса содержит ссылку на объект, к которому направлено данное отношение, и тип этого отношения.
 * Отношение однонаправленное. Для двунаправленной связи используется обратное отношение.
 * <p>
 * Типы отношений - RelationType
 *
 * @see RelationType
 */
public class Relation {
    /**
     * Понятие, к которому направлено отношение.
     */
    private final Concept concept;
    /**
     * Тип отношения.
     */
    private final RelationType relationType;

    public Relation(Concept concept, RelationType relationType) {
        this.concept = concept;
        this.relationType = relationType;
    }

    public Concept getConcept() {
        return concept;
    }

    public RelationType getType() {
        return relationType;
    }

    @Override
    public String toString() {
        return concept.toString() + " — " + relationType.toString();
    }
}

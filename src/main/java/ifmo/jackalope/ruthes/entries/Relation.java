package ifmo.jackalope.ruthes.entries;

public class Relation {
    private final Concept concept;
    private final RelationType relationType;

    public Relation(Concept concept, RelationType relationType) {
        this.concept = concept;
        this.relationType = relationType;
    }

    public Concept getConcept() {
        return concept;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    @Override
    public String toString() {
        return concept.toString() + " — " + relationType.toString();
    }
}

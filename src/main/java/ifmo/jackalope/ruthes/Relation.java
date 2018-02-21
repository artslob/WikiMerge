package ifmo.jackalope.ruthes;

public class Relation {
    private Concept concept;
    private RelationType relationType;

    public Concept getConcept() {
        return concept;
    }

    Relation(Concept concept, RelationType relationType) {
        this.concept = concept;
        this.relationType = relationType;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    @Override
    public String toString() {
        return concept.toString() + " — " + relationType.toString();
    }
}

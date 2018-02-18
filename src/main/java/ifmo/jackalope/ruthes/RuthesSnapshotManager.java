package ifmo.jackalope.ruthes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuthesSnapshotManager {
    private RuthesSnapshot snapshot;

    public RuthesSnapshotManager(String dir_contain_xml) {
        snapshot = new RuthesSnapshot(dir_contain_xml);
    }

    public RuthesSnapshot getSnapshot() {
        return snapshot;
    }

    public Concept getConceptByName(String name) {
        for (Concept concept : snapshot.getConcepts().values()) {
            if (concept.getName().equalsIgnoreCase(name))
                return concept;
        }
        return null;
    }

    public TextEntry getTextEntryByName(String name) {
        for (TextEntry text_entry : snapshot.getEntries().values()) {
            if (text_entry.getName().equalsIgnoreCase(name))
                return text_entry;
        }
        return null;
    }

    public List<ConceptToRelation> getRelationsForConcept(String concept_id) {
        Concept concept = snapshot.getConcepts().get(concept_id);
        if (concept == null)
            return null;
        List<ConceptToRelation> relations = new ArrayList<>();
        for (Map.Entry<String, RelationType> entry : concept.getRelations().entrySet()) {
            Concept to = snapshot.getConcepts().get(entry.getKey());
            relations.add(new ConceptToRelation(to, entry.getValue()));
        }
        return relations;
    }

    public List<TextEntry> getSynonymsForConcept(String concept_id) {
        Concept concept = snapshot.getConcepts().get(concept_id);
        if (concept == null)
            return null;
        List<TextEntry> synonyms = new ArrayList<>();
        for (String entry_id : concept.getSynonyms()) {
            synonyms.add(snapshot.getEntries().get(entry_id));
        }
        return synonyms;
    }

    public List<Concept> getSynonymsForTextEntry(String text_entry_id) {
        TextEntry entry = snapshot.getEntries().get(text_entry_id);
        if (entry == null)
            return null;
        List<Concept> synonyms = new ArrayList<>();
        for (String concept_id : entry.getSynonyms()) {
            synonyms.add(snapshot.getConcepts().get(concept_id));
        }
        return synonyms;
    }

    public class ConceptToRelation {
        Concept concept;
        RelationType relationType;

        ConceptToRelation(Concept concept, RelationType relationType) {
            this.concept = concept;
            this.relationType = relationType;
        }

        @Override
        public String toString() {
            return concept.toString() + " — " + relationType.toString();
        }
    }
}

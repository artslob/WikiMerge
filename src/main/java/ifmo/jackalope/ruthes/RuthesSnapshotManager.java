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

    public List<Relation> getRelationsForConcept(String concept_id) {
        Concept concept = snapshot.getConcepts().get(concept_id);
        if (concept == null)
            return null;
        return concept.getRelations();
    }

    public List<TextEntry> getSynonymsForConcept(String concept_id) {
        Concept concept = snapshot.getConcepts().get(concept_id);
        if (concept == null)
            return null;
        return concept.getSynonyms();
    }

    public List<Concept> getSynonymsForTextEntry(String text_entry_id) {
        TextEntry entry = snapshot.getEntries().get(text_entry_id);
        if (entry == null)
            return null;
        return entry.getSynonyms();
    }
}

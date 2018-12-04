package ifmo.jackalope.ruthes;

import ifmo.jackalope.ruthes.entries.Concept;
import ifmo.jackalope.ruthes.entries.Entry;
import ifmo.jackalope.ruthes.entries.Relation;
import ifmo.jackalope.ruthes.entries.TextEntry;

import java.util.List;

public class RuthesSnapshotManager {
    private RuthesSnapshot snapshot;

    public RuthesSnapshotManager(String dir_contain_xml) {
        snapshot = new RuthesSnapshot(dir_contain_xml);
    }

    public RuthesSnapshot getSnapshot() {
        return snapshot;
    }

    public Entry getEntryByName(String name) {
        for (Entry entry : snapshot.getEntries().values()) {
            if (entry.getName().equalsIgnoreCase(name))
                return entry;
        }
        return null;
    }

    public Concept getConceptByName(String name) {
        for (Entry entry : snapshot.getEntries().values()) {
            if (entry instanceof Concept && entry.getName().equalsIgnoreCase(name))
                return (Concept) entry;
        }
        return null;
    }

    public TextEntry getTextEntryByName(String name) {
        for (Entry entry : snapshot.getEntries().values()) {
            if (entry instanceof TextEntry && entry.getName().equalsIgnoreCase(name))
                return (TextEntry) entry;
        }
        return null;
    }

    public List<Relation> getRelationsForConcept(String concept_id) {
        Concept concept = (Concept) snapshot.getEntries().get(concept_id);
        if (concept == null)
            return null;
        return concept.getRelations();
    }

//        public List<TextEntry> getSynonymsForConcept(String concept_id) {
//        Concept concept = (Concept) snapshot.getEntries().get(concept_id);
//        if (concept == null)
//            return null;
//        return concept.getSynonyms();
//    }
//
//    public List<Concept> getSynonymsForTextEntry(String text_entry_id) {
//        TextEntry entry = snapshot.getEntries().get(text_entry_id);
//        if (entry == null)
//            return null;
//        return entry.getSynonyms();
//    }
}

package ifmo.jackalope.ruthes;

import ifmo.jackalope.ruthes.entries.Concept;
import ifmo.jackalope.ruthes.entries.Relation;
import ifmo.jackalope.ruthes.entries.RelationType;
import ifmo.jackalope.ruthes.entries.TextEntry;
import ifmo.jackalope.ruthes.utils.StaxEventProcessor;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuthesSnapshot {
    private Map<String, Concept> concepts = new HashMap<>();
    private Map<String, TextEntry> entries = new HashMap<>();
    /* map.key -> concept.id; map.value -> [entries.ids] */
    private Map<String, List<String>> synonyms = new HashMap<>();

    RuthesSnapshot(String dir_contain_xml) {
        try {
            conceptsLoad(dir_contain_xml);
            entriesLoad(dir_contain_xml);
            relationsLoad(dir_contain_xml);
            synonymsLoad(dir_contain_xml);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Concept> getConcepts() {
        return concepts;
    }

    public Map<String, TextEntry> getEntries() {
        return entries;
    }

    public Map<String, List<String>> getSynonyms() {
        return synonyms;
    }

    private void synonymsLoad(String dir_contain_xml) throws IOException, XMLStreamException {
        Path path = Paths.get(dir_contain_xml, "synonyms.xml");

        try (StaxEventProcessor processor = new StaxEventProcessor(Files.newInputStream(path))) {
            XMLEventReader reader = processor.getReader();

            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("entry_rel")) {
                        Attribute concept_id_attr = startElement.getAttributeByName(new QName("concept_id"));
                        Attribute entry_id_attr = startElement.getAttributeByName(new QName("entry_id"));

                        String concept_id = concept_id_attr.getValue();
                        String entry_id = entry_id_attr.getValue();

                        List<String> entries = synonyms.get(concept_id);
                        if (entries == null) {
                            entries = new ArrayList<>();
                            entries.add(entry_id);
                            synonyms.put(concept_id, entries);
                        }
                        else {
                            entries.add(entry_id);
                        }

                        this.concepts.get(concept_id).getSynonyms().add(this.entries.get(entry_id));
                        this.entries.get(entry_id).getSynonyms().add(this.concepts.get(concept_id));
                    }
                    else if (!startElement.getName().getLocalPart().equals("synonyms")) {
                        throw new IllegalStateException("Unknown start element " +
                                startElement.getName().getLocalPart());
                    }
                }
            }
        }
    }

    private void entriesLoad(String dir_contain_xml) throws IOException, XMLStreamException {
        Path path = Paths.get(dir_contain_xml, "text_entry.xml");

        try (StaxEventProcessor processor = new StaxEventProcessor(Files.newInputStream(path))) {
            XMLEventReader reader = processor.getReader();
            TextEntry.Builder text_entry_builder = null;

            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "entry":
                            text_entry_builder = new TextEntry.Builder();
                            Attribute id_attr = startElement.getAttributeByName(new QName("id"));
                            if (id_attr != null) {
                                text_entry_builder.setId(id_attr.getValue());
                            }
                            break;
                        case "name":
                            xmlEvent = reader.nextEvent();
                            if (text_entry_builder != null) {
                                text_entry_builder.setName(xmlEvent.asCharacters().getData().toLowerCase());
                            }
                            break;
                        case "lemma":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElement) && text_entry_builder != null) {
                                text_entry_builder.setLemma(xmlEvent.asCharacters().getData().toLowerCase());
                            }
                            break;
                        case "main_word":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElement) && text_entry_builder != null) {
                                text_entry_builder.setMainWord(xmlEvent.asCharacters().getData().toLowerCase());
                            }
                            break;
                        case "synt_type":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElement) && text_entry_builder != null) {
                                text_entry_builder.setSyntType(xmlEvent.asCharacters().getData());
                            }
                            break;
                        case "pos_string":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElement) && text_entry_builder != null) {
                                text_entry_builder.setPosString(xmlEvent.asCharacters().getData());
                            }
                            break;
                        default:
                            if (!startElement.getName().getLocalPart().equals("entries"))
                                throw new IllegalStateException("Unknown start element " +
                                        startElement.getName().getLocalPart());
                            break;
                    }
                }
                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("entry")) {
                        if (text_entry_builder != null) {
                            TextEntry text_entry_result = text_entry_builder.build();
                            entries.put(text_entry_result.getId(), text_entry_result);
                            text_entry_builder = null;
                        }
                    }
                }
            }
        }
    }

    private void relationsLoad(String dir_contain_xml) throws IOException, XMLStreamException {
        Path path = Paths.get(dir_contain_xml, "relations.xml");

        try (StaxEventProcessor processor = new StaxEventProcessor(Files.newInputStream(path))) {
            XMLEventReader reader = processor.getReader();

            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("rel")) {
                        Attribute from = startElement.getAttributeByName(new QName("from"));
                        Attribute to = startElement.getAttributeByName(new QName("to"));
                        Attribute name = startElement.getAttributeByName(new QName("name"));
//                        Attribute asp = startElement.getAttributeByName(new QName("asp"));
                        Concept from_concept = concepts.get(from.getValue());
                        Concept to_concept = concepts.get(to.getValue());
                        Relation relation = new Relation(to_concept, RelationType.fromString(name.getValue()));
                        from_concept.getRelations().add(relation);
                    }
                    else if (!startElement.getName().getLocalPart().equals("relations")) {
                        throw new IllegalStateException("Unknown start element " +
                                startElement.getName().getLocalPart());
                    }
                }
            }
        }
    }

    private void conceptsLoad(String dir_contain_xml) throws IOException, XMLStreamException {
        Path path = Paths.get(dir_contain_xml, "concepts.xml");

        try (StaxEventProcessor processor = new StaxEventProcessor(Files.newInputStream(path))) {
            XMLEventReader reader = processor.getReader();
            Concept.Builder concept_builder = null;

            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "concept":
                            concept_builder = new Concept.Builder();
                            Attribute id_attr = startElement.getAttributeByName(new QName("id"));
                            if (id_attr != null) {
                                concept_builder.setId(id_attr.getValue());
                            }
                            break;
                        case "name":
                            xmlEvent = reader.nextEvent();
                            if (concept_builder != null) {
                                concept_builder.setName(xmlEvent.asCharacters().getData().toLowerCase());
                            }
                            break;
                        case "gloss":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElement) && concept_builder != null) {
                                concept_builder.setGloss(xmlEvent.asCharacters().getData().toLowerCase());
                            }
                            break;
                        case "domain":
                            xmlEvent = reader.nextEvent();
                            if (concept_builder != null) {
                                concept_builder.setDomain(xmlEvent.asCharacters().getData());
                            }
                            break;
                        default:
                            if (!startElement.getName().getLocalPart().equals("concepts"))
                                throw new IllegalStateException("Unknown start element " +
                                        startElement.getName().getLocalPart());
                            break;
                    }
                }
                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("concept")) {
                        if (concept_builder != null) {
                            Concept result_concept = concept_builder.build();
                            concepts.put(result_concept.getId(), result_concept);
                            concept_builder = null;
                        }
                    }
                }
            }
        }
    }

}

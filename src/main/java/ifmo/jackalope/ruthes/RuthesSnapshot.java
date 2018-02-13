package ifmo.jackalope.ruthes;

import com.sun.xml.internal.stream.events.EndElementEvent;

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
    private Map<Integer, Concept> concepts = new HashMap<>();
    private Map<Integer, TextEntry> entries = new HashMap<>();
    /* map.key -> concept_id; map.value -> [entry_id] */
    private Map<Integer, List<Integer>> synonyms = new HashMap<>();

    public RuthesSnapshot(String dir_contain_xml) {
        try {
            conceptsLoad(dir_contain_xml);
            relationsLoad(dir_contain_xml);
            entriesLoad(dir_contain_xml);
            synonymsLoad(dir_contain_xml);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Concept> getConcepts() {
        return concepts;
    }

    public Map<Integer, TextEntry> getEntries() {
        return entries;
    }

    public Map<Integer, List<Integer>> getSynonyms() {
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

                        Integer concept_id = Integer.parseInt(concept_id_attr.getValue());
                        Integer entry_id = Integer.parseInt(entry_id_attr.getValue());

                        List<Integer> entries = synonyms.get(concept_id);
                        if (entries == null) {
                            List<Integer> concepts = new ArrayList<>();
                            concepts.add(entry_id);
                            synonyms.put(concept_id, concepts);
                        }
                        else {
                            entries.add(entry_id);
                        }

                        this.concepts.get(concept_id).getSynonyms().add(entry_id);
                        this.entries.get(entry_id).getSynonyms().add(concept_id);
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
            TextEntry current_entry = null;

            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "entry":
                            current_entry = new TextEntry();
                            Attribute id_attr = startElement.getAttributeByName(new QName("id"));
                            if (id_attr != null) {
                                current_entry.setId(Integer.parseInt(id_attr.getValue()));
                            }
                            break;
                        case "name":
                            xmlEvent = reader.nextEvent();
                            if (current_entry != null) {
                                current_entry.setName(xmlEvent.asCharacters().getData());
                            }
                            break;
                        case "lemma":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElementEvent) && current_entry != null) {
                                current_entry.setLemma(xmlEvent.asCharacters().getData());
                            }
                            break;
                        case "main_word":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElementEvent) && current_entry != null) {
                                current_entry.setMain_word(xmlEvent.asCharacters().getData());
                            }
                            break;
                        case "synt_type":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElementEvent) && current_entry != null) {
                                current_entry.setSynt_type(xmlEvent.asCharacters().getData());
                            }
                            break;
                        case "pos_string":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElementEvent) && current_entry != null) {
                                current_entry.setPos_string(xmlEvent.asCharacters().getData());
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
                        if (current_entry != null) {
                            entries.put(current_entry.getId(), current_entry);
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
                        Concept from_concept = concepts.get(Integer.parseInt(from.getValue()));
                        from_concept.getRelations().put(Integer.parseInt(to.getValue()),
                                RelationType.fromString(name.getValue()));
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
            Concept current_concept = null;

            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "concept":
                            current_concept = new Concept();
                            Attribute id_attr = startElement.getAttributeByName(new QName("id"));
                            if (id_attr != null) {
                                current_concept.setId(Integer.parseInt(id_attr.getValue()));
                            }
                            break;
                        case "name":
                            xmlEvent = reader.nextEvent();
                            if (current_concept != null) {
                                current_concept.setName(xmlEvent.asCharacters().getData());
                            }
                            break;
                        case "gloss":
                            xmlEvent = reader.nextEvent();
                            if (!(xmlEvent instanceof EndElementEvent) && current_concept != null) {
                                current_concept.setGloss(xmlEvent.asCharacters().getData());
                            }
                            break;
                        case "domain":
                            xmlEvent = reader.nextEvent();
                            if (current_concept != null) {
                                current_concept.setDomain(xmlEvent.asCharacters().getData());
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
                        if (current_concept != null) {
                            concepts.put(current_concept.getId(), current_concept);
                        }
                    }
                }
            }
        }
    }

}

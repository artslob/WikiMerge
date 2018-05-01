package ifmo.jackalope.ruthes.rules;

import com.tuneit.jackalope.dict.wiki.engine.core.SenseOption;
import com.tuneit.jackalope.dict.wiki.engine.core.SenseOptionType;
import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRule implements Rule {
    private final Map<String, WikiSense> wiki_senses;
    final Map<String, List<WikiSense>> lemma_to_sense;
    final RuthesSnapshot ruthes;

    AbstractRule(final Map<String, WikiSense> wiki_senses, final RuthesSnapshot ruthes) {
        this.wiki_senses = wiki_senses;
        this.lemma_to_sense = lemma_to_senses(wiki_senses);
        this.ruthes = ruthes;
    }

    WikiSense find_most_similar_sense_for_option(List<WikiSense> senses, TextEntry text_entry, SenseOptionType option_type) {
        if (!is_synonym_relation(option_type))
            return null;
        if (senses == null || senses.size() == 0)
            return null;
        if (senses.size() == 1)
            return senses.get(0);

        WikiSense result = null;
        int similarity = 0;

        for (Concept synonym : text_entry.getSynonyms()) {
            for (WikiSense adj_sense : senses) {
                int current_similarity = compare_links(adj_sense, synonym);
                if (current_similarity > similarity) {
                    similarity = current_similarity;
                    result = adj_sense;
                }
            }
        }

        return result;
    }

    WikiSense find_most_similar_sense_for_option(List<WikiSense> senses, Concept concept, SenseOptionType option_type) {
        if (senses == null || senses.size() == 0)
            return null;
        if (senses.size() == 1)
            return senses.get(0);

        WikiSense result = null;
        int similarity = 0;

        if (is_synonym_relation(option_type)) {
            for (TextEntry synonym : concept.getSynonyms()) {
                for (WikiSense adj_sense : senses) {
                    int current_similarity = compare_links(adj_sense, synonym);
                    if (current_similarity > similarity) {
                        similarity = current_similarity;
                        result = adj_sense;
                    }
                }
            }
        }
        else {
            for (Relation relation : concept.getRelations()) {
                Concept adj_concept = relation.getConcept();
                RelationType adj_relation_type = relation.getRelationType();

                if (!compare_link_types(adj_relation_type, option_type))
                    continue;

                for (WikiSense adj_sense : senses) {
                    int current_similarity = compare_links(adj_sense, adj_concept);
                    if (current_similarity > similarity) {
                        similarity = current_similarity;
                        result = adj_sense;
                    }
                }
            }
        }

        return result;
    }

    WikiSense find_most_similar_sense(List<WikiSense> source_senses, TextEntry text_entry) {
        if (source_senses.size() <= 0)
            return null;
        if (source_senses.size() == 1)
            return source_senses.get(0);

        WikiSense current_sense = null;
        int similarity = 0;
        for (WikiSense sense : source_senses) {
            int current_similarity = compare_links(sense, text_entry);
            if (current_similarity > similarity) {
                current_sense = sense;
                similarity = current_similarity;
            }
        }
        return current_sense;
    }

    WikiSense find_most_similar_sense(List<WikiSense> source_senses, Concept concept) {
        if (source_senses.size() <= 0)
            return null;
        if (source_senses.size() == 1)
            return source_senses.get(0);

        WikiSense current_sense = null;
        int similarity = 0;
        for (WikiSense sense : source_senses) {
            int current_similarity = compare_links(sense, concept);
            if (current_similarity > similarity) {
                current_sense = sense;
                similarity = current_similarity;
            }
        }
        return current_sense;
    }

    private int compare_links(WikiSense sense, TextEntry text_entry) {
        int similarity = 0;

        for (Concept target_concept : text_entry.getSynonyms()) {
            for (SenseOption option : sense.getAllOptions()) {
                String option_target_lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType();

                if (option_target_lemma.equalsIgnoreCase(target_concept.getName()) && is_synonym_relation(option_type))
                    similarity++;
            }

            for (Map.Entry<String, SenseOptionType> entry : sense.getLinks().entrySet()) {
                WikiSense link_target_sense = wiki_senses.get(entry.getKey());
                SenseOptionType sense_option = entry.getValue();

                if (link_target_sense.getLemma().equalsIgnoreCase(target_concept.getName()) && is_synonym_relation(sense_option))
                    similarity++;
            }
        }

        return similarity;
    }

    private int compare_links(WikiSense sense, Concept concept) {
        int similarity = 0;

        for (Relation relation : concept.getRelations()) {
            Concept target_concept = relation.getConcept();
            RelationType relation_type = relation.getRelationType();

            for (SenseOption option : sense.getAllOptions()) {
                String option_target_lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType();

                if (option_target_lemma.equalsIgnoreCase(target_concept.getName()) && compare_link_types(relation_type, option_type))
                    similarity++;
            }

            for (Map.Entry<String, SenseOptionType> entry : sense.getLinks().entrySet()) {
                WikiSense link_target_sense = wiki_senses.get(entry.getKey());
                SenseOptionType sense_option = entry.getValue();

                if (link_target_sense.getLemma().equalsIgnoreCase(target_concept.getName()) && compare_link_types(relation_type, sense_option))
                    similarity++;
            }
        }

        for (TextEntry synonym : concept.getSynonyms()) {
            for (SenseOption option : sense.getAllOptions()) {
                String option_target_lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType();

                if (option_target_lemma.equalsIgnoreCase(synonym.getName()) && is_synonym_relation(option_type))
                    similarity++;
            }

            for (Map.Entry<String, SenseOptionType> entry : sense.getLinks().entrySet()) {
                WikiSense link_target_sense = wiki_senses.get(entry.getKey());
                SenseOptionType sense_option = entry.getValue();

                if (link_target_sense.getLemma().equalsIgnoreCase(synonym.getName()) && is_synonym_relation(sense_option))
                    similarity++;
            }
        }

        return similarity;
    }

    private boolean compare_link_types(RelationType ruthes_link, SenseOptionType wiki_link) {
        if (ruthes_link == RelationType.HYPONYM && wiki_link == SenseOptionType.HYPONYM) // НИЖЕ
            return true;

        if (ruthes_link == RelationType.HYPERNYM && wiki_link == SenseOptionType.HYPERNYM) // ВЫШЕ
            return true;

        if (ruthes_link == RelationType.HOLONYM && wiki_link == SenseOptionType.HOLONYM) // ЦЕЛОЕ
            return true;

        if (ruthes_link == RelationType.MERONYM && wiki_link == SenseOptionType.MERONYM) // ЧАСТЬ
            return true;

        if (ruthes_link == RelationType.DEPEND_ON && wiki_link == SenseOptionType.RELATED_TERM) // АСЦ1
            return true;

        if (ruthes_link == RelationType.HAS_DEPEND && // АСЦ2
                (
                        wiki_link == SenseOptionType.DERIVED_TERM ||
                                wiki_link == SenseOptionType.TRANSLATION ||
                                wiki_link == SenseOptionType.PRONUNCIATION ||
                                wiki_link == SenseOptionType.ALTERNATIVE_FORM ||
                                wiki_link == SenseOptionType.DESCENDANT
                )
                )
        {
            return true;
        }

        return ruthes_link == RelationType.SYM_ASSOC && // АСЦ
                (
                        wiki_link == SenseOptionType.RELATED_TERM ||
                                wiki_link == SenseOptionType.COORDINATE_TERM ||
                                wiki_link == SenseOptionType.INFLECTION ||
                                wiki_link == SenseOptionType.QUOTATION ||
                                wiki_link == SenseOptionType.COLLOCATION ||
                                wiki_link == SenseOptionType.ALTERNATIVE_FORM
                );
    }

    private boolean is_synonym_relation(SenseOptionType wiki_link) {
        return wiki_link == SenseOptionType.SYNONYM;
    }

    private Map<String, List<WikiSense>> lemma_to_senses(Map<String, WikiSense> wiki_senses) {
        Map<String, List<WikiSense>> lemma_to_senses = new HashMap<>();

        for (WikiSense sense : wiki_senses.values()) {
            String lemma = sense.getLemma();
            if (lemma_to_senses.containsKey(lemma)) {
                lemma_to_senses.get(lemma).add(sense);
            }
            else {
                List<WikiSense> senses = new ArrayList<>();
                senses.add(sense);
                lemma_to_senses.put(lemma, senses);
            }
        }

        return lemma_to_senses;
    }

    private Map<String, WikiSense> lemmas_with_one_sense(Map<String, WikiSense> wiki_senses) {
        Map<String, List<WikiSense>> lemma_to_senses = lemma_to_senses(wiki_senses);

        Map<String, WikiSense> lemma_to_sense = new HashMap<>();

        for (Map.Entry<String, List<WikiSense>> entry : lemma_to_senses.entrySet()) {
            String lemma = entry.getKey();
            List<WikiSense> senses = entry.getValue();

            if (senses.size() == 1) {
                lemma_to_sense.put(lemma, senses.get(0));
                lemma_to_senses.remove(lemma);
            }
        }

        return lemma_to_sense;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}

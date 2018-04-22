package ifmo.jackalope.ruthes.rules;

import com.tuneit.jackalope.dict.wiki.engine.core.SenseOption;
import com.tuneit.jackalope.dict.wiki.engine.core.SenseOptionType;
import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Делается предположение о сходстве сенсов в рутезе и вики на основе
 * сходства связей сенса вики и рутеза
 */
public class FirstRule implements Rule {

    FirstRule(final Map<String, WikiSense> wiki_senses, final RuthesSnapshot ruthes) {
        this.wiki_senses = wiki_senses;
        this.lemma_to_sense = lemma_to_senses(wiki_senses);
        this.ruthes = ruthes;
    }

    private final Map<String, WikiSense> wiki_senses;
    private final Map<String, List<WikiSense>> lemma_to_sense;
    private final RuthesSnapshot ruthes;

    @Override
    public int apply() {
        int links_restored = 0;

        for (Concept concept : ruthes.getConcepts().values()) {
            List<WikiSense> source_senses = lemma_to_sense.get(concept.getName().toLowerCase());
            if (source_senses == null || source_senses.size() < 1)
                continue;

            WikiSense source_sense = find_most_similar_sense(source_senses, concept);
            if (source_sense == null)
                continue;

            // TODO: для связей в concept - попробовать найти аналогичные связи в вики (построение новых)


            // TODO: для sense_option - попробовать определить к какому сенсу идёт ссылка (корректировка текущих связей)
            for (SenseOption option : source_sense.getAllOptions()) {
                String lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType(); // TODO: compare

                List<WikiSense> possible_target_senses = lemma_to_sense.get(lemma);
                if (possible_target_senses == null || possible_target_senses.size() < 1)
                    continue;

                // TODO: тут можно восстанавливать больше, чем одну связь

                // TODO: тут надо сравнить концепты/синонимы имеющие связь с текущим concept и possible_target_senses (то есть будут сравниваться их связи, аналогично выше)
                WikiSense most_similar_target_sense = find_most_similar_sense_for_option(possible_target_senses, concept);
            }

            // TODO: для links - попробовать найти аналогичную связь в рутезе и экспортировать связи этого узла в вики (новые для сенса, на который указывает link)


            System.out.println();
        }

        return links_restored;
    }


    private WikiSense find_most_similar_sense_for_option(List<WikiSense> senses, Concept concept) {
        if (senses == null || senses.size() == 0)
            return null;
        if (senses.size() == 1)
            return senses.get(0);

        WikiSense result = null;
        int similarity = 0;

        // TODO: check link type

        for (Relation relation : concept.getRelations()) {
            Concept adj_concept = relation.getConcept();
            RelationType adj_type = relation.getRelationType();

            for (WikiSense adj_sense : senses) {
                int current_similarity = compare_links(adj_sense, adj_concept);
                if (current_similarity > similarity) {
                    similarity = current_similarity;
                    result = adj_sense;
                }
            }
        }

        for (TextEntry synonym : concept.getSynonyms()) {
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

    private WikiSense find_most_similar_sense(List<WikiSense> source_senses, Concept concept) {
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
            // TODO: compare link type (synonym)

            for (SenseOption option : sense.getAllOptions()) {
                String option_target_lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType(); // TODO: compare link type

                if (option_target_lemma.equalsIgnoreCase(target_concept.getName()))
                    similarity++;
            }

            for (Map.Entry<String, SenseOptionType> entry : sense.getLinks().entrySet()) {
                WikiSense link_target_sense = wiki_senses.get(entry.getKey());
                SenseOptionType sense_option = entry.getValue(); // TODO: compare link type

                if (link_target_sense.getLemma().equalsIgnoreCase(target_concept.getName())) // TODO: check link type similarity
                    similarity++;
            }
        }

        return similarity;
    }

    private int compare_links(WikiSense sense, Concept concept) {
        int similarity = 0;

        for (Relation relation : concept.getRelations()) {
            Concept target_concept = relation.getConcept();
            RelationType relation_type = relation.getRelationType(); // TODO: compare link type

            for (SenseOption option : sense.getAllOptions()) {
                String option_target_lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType(); // TODO: compare link type

                if (option_target_lemma.equalsIgnoreCase(target_concept.getName()))
                    similarity++;
            }

            for (Map.Entry<String, SenseOptionType> entry : sense.getLinks().entrySet()) {
                WikiSense link_target_sense = wiki_senses.get(entry.getKey());
                SenseOptionType sense_option = entry.getValue(); // TODO: compare link type

                if (link_target_sense.getLemma().equalsIgnoreCase(target_concept.getName())) // TODO: check link type similarity
                    similarity++;
            }
        }

        for (TextEntry synonym : concept.getSynonyms()) {
            for (SenseOption option : sense.getAllOptions()) {
                String option_target_lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType(); // TODO: compare link type

                if (option_target_lemma.equalsIgnoreCase(synonym.getName()))
                    similarity++;
            }

            for (Map.Entry<String, SenseOptionType> entry : sense.getLinks().entrySet()) {
                WikiSense link_target_sense = wiki_senses.get(entry.getKey());
                SenseOptionType sense_option = entry.getValue(); // TODO: compare link type

                if (link_target_sense.getLemma().equalsIgnoreCase(synonym.getName())) // TODO: check link type similarity
                    similarity++;
            }
        }

        return similarity;
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

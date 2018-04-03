package ifmo.jackalope.ruthes.rules;

import com.tuneit.jackalope.dict.wiki.engine.core.SenseOption;
import com.tuneit.jackalope.dict.wiki.engine.core.SenseOptionType;
import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.Concept;
import ifmo.jackalope.ruthes.RuthesSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Делается предположение о сходстве сенсов в рутезе и вики на основе
 * сходства связей сенса вики и рутеза
 */
public class FirstRule implements Rule {
    @Override
    public int apply(Map<String, WikiSense> wiki_senses, RuthesSnapshot ruthes) {
        int links_restored = 0;
        // TODO: optionally cast to lower case wiki and ruthes lemmas
        Map<String, List<WikiSense>> lemma_to_sense = lemma_to_senses(wiki_senses);

        for (Concept concept : ruthes.getConcepts().values()) {
            List<WikiSense> source_senses = lemma_to_sense.get(concept.getName().toLowerCase());
            if (source_senses == null || source_senses.size() < 1)
                continue;

            WikiSense source_sense = find_most_similar_sense(source_senses, wiki_senses, lemma_to_sense, concept);
            if (source_sense == null)
                continue;

            // TODO: для sense_option - попробовать определить к какому сенсу идёт ссылка (корректировка текущих связей)
            // TODO: для связей в concept - попробовать найти аналогичные связи в вики (построение новых)
            // TODO: для links - попробовать найти аналогичную связь в рутезе и экспортировать связи этого узла в вики (новые для сенса, на который указывает link)

            // это корректировка существующих связей
            for (SenseOption option : source_sense.getAllOptions()){
                String lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType();

                List<WikiSense> possible_target_senses = lemma_to_sense.get(lemma);
                if (possible_target_senses == null || possible_target_senses.size() < 1)
                    continue;

                // TODO: тут надо сравнить концепты/синонимы имеющие связь с текущим concept и possible_target_senses (то есть будут сравниваться их связи, аналогично выше)
                WikiSense most_similar_target_sense = find_most_similar_sense(possible_target_senses, wiki_senses, lemma_to_sense, );
            }

            // TODO: for links in source_sense


            System.out.println();
        }

        return links_restored;
    }

    private WikiSense find_most_similar_sense(List<WikiSense> source_senses, Map<String, WikiSense> wiki_senses,
                                              Map<String, List<WikiSense>> lemma_to_sense, Concept concept)
    {
        if (source_senses.size() <= 0)
            return null;
        if (source_senses.size() == 1)
            return source_senses.get(0);

        WikiSense current_sense = null;
        int similarity = 0;
        for (WikiSense sense : source_senses) {
            int current_similarity = compare_links(wiki_senses, lemma_to_sense, sense, concept);
            if (current_similarity > similarity) {
                current_sense = sense;
                similarity = current_similarity;
            }
        }
        return current_sense;
    }

    private int compare_links(Map<String, WikiSense> wiki_senses, Map<String, List<WikiSense>> lemma_to_sense,
                              WikiSense sense, Concept concept)
    {
        int similarity = 0;
        for (SenseOption option : sense.getAllOptions()) {
            String lemma = option.getOption().toString();
            SenseOptionType option_type= option.getType();

            if (lemma.equalsIgnoreCase(concept.getName())) // TODO: check link type similarity
                similarity++;
        }

        for (Map.Entry<String, SenseOptionType> entry : sense.getLinks().entrySet()) {
            WikiSense link_target_sense = wiki_senses.get(entry.getKey());

            if (link_target_sense.getLemma().equalsIgnoreCase(concept.getName())) // TODO: check link type similarity
                similarity++;
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

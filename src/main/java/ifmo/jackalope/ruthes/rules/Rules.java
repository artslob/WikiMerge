package ifmo.jackalope.ruthes.rules;

import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.RuthesSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rules {

    private static final List<Rule> rules = new ArrayList<>();

    public static int apply(Map<String, WikiSense> wiki_senses, RuthesSnapshot ruthes) {
        final Map<String, List<WikiSense>> lemma_to_sense = lemma_to_senses(wiki_senses);

        if (rules.isEmpty()) {
            rules.add(new FirstRule(wiki_senses, ruthes, lemma_to_sense));
            rules.add(new SecondRule(wiki_senses, ruthes, lemma_to_sense));
            // TODO: для links - попробовать найти аналогичную связь в рутезе и экспортировать связи этого узла в вики (новые для сенса, на который указывает link)
        }

        int total = 0;

        for (Rule rule : rules) {
            int apply_result = rule.apply();
            total += apply_result;
            System.out.println("Rule " + rule + " created " + apply_result + " links");
        }

        return total;
    }

    private static Map<String, List<WikiSense>> lemma_to_senses(Map<String, WikiSense> wiki_senses) {
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

    private static Map<String, WikiSense> lemmas_with_one_sense(Map<String, WikiSense> wiki_senses) {
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
}

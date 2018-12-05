package ifmo.jackalope.ruthes.rules;

import com.tuneit.jackalope.dict.wiki.engine.core.SenseOption;
import com.tuneit.jackalope.dict.wiki.engine.core.SenseOptionType;
import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.RuthesSnapshot;
import ifmo.jackalope.ruthes.entries.Entry;
import ifmo.jackalope.ruthes.entries.Relation;
import ifmo.jackalope.ruthes.entries.RelationType;

import java.util.List;
import java.util.Map;

/**
 * для связей рутеза - попробовать найти аналогичные связи в вики (построение новых)
 */
public class SecondRule extends AbstractRule {

    SecondRule(final Map<String, WikiSense> wiki_senses,
               final RuthesSnapshot ruthes,
               final Map<String, List<WikiSense>> lemma_to_sense) {
        super(wiki_senses, ruthes, lemma_to_sense);
    }

    @Override
    public int apply() {
        int links_restored = 0;

        for (Entry entry : ruthes.getEntries().values()) {
            List<WikiSense> source_senses = lemma_to_sense.get(entry.getName().toLowerCase());
            if (source_senses == null || source_senses.size() < 1)
                continue;

            WikiSense source_sense = find_most_similar_sense(source_senses, entry);
            if (source_sense == null)
                continue;

            for (Relation relation : entry.getRelations()) {
                Entry adj_entry = relation.getEntry();
                RelationType adj_relation_type = relation.getType();

                List<WikiSense> target_senses = lemma_to_sense.get(adj_entry.getName().toLowerCase());
                if (target_senses == null || target_senses.size() < 1)
                    continue;

                WikiSense target_sense = find_most_similar_sense(target_senses, adj_entry);
                if (target_sense == null)
                    continue;

                if (link_exists_between_senses(source_sense, target_sense, adj_relation_type))
                    continue;

                links_restored++;
                log(source_sense, target_sense, entry);
            }
        }

        return links_restored;
    }

    private boolean link_exists_between_senses(WikiSense source_sense, WikiSense target_sense, RelationType relation_type) {
        for (SenseOption option : source_sense.getAllOptions()) {
            String lemma = option.getOption().toString();
            SenseOptionType option_type = option.getType();

            List<WikiSense> possible_senses = lemma_to_sense.get(lemma);
            if (possible_senses == null || possible_senses.size() < 1)
                continue;

            if (possible_senses.contains(target_sense) && compare_link_types(relation_type, option_type))
                return true;
        }

        for (Map.Entry<String, SenseOptionType> link : source_sense.getLinks().entrySet()) {
            WikiSense possible_target_sense = wiki_senses.get(link.getKey());
            SenseOptionType option_type = link.getValue();

            if (possible_target_sense.equals(target_sense) && compare_link_types(relation_type, option_type))
                return true;
        }

        return false;
    }

    private void log(WikiSense source, WikiSense target, Entry entry) {
        String log = String.format("Can create link from sense: %s\n\twith gloss: %s\n" +
                        "\tto sense: %s\n\twith gloss: %s\n" +
                        "\tby concept: %s\n",
                source.getLemma(), source.getGloss(),
                target.getLemma(), target.getGloss(),
                entry.getName());
        System.out.println(log);
    }
}

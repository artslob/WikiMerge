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
 * Построение новых связей в вики - импорт связей из рутеза.
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
            /*
             * Получаем все сенсы леммы, имеющей такое же имя, как и узел entry из ruthes.
             * Таким образом мы знаем, что один из сенсов соответствует данному узлу в ruthes.
             * */
            List<WikiSense> source_senses = lemma_to_sense.get(entry.getName().toLowerCase());
            if (source_senses == null || source_senses.size() < 1)
                continue;

            /*
             * Так как мы знаем, что один из сенсов соответствует данному узлу в ruthes, то данный метод
             * находит точное соответствие между entry и одним из сенсов.
             * В результате имеем, что source_sense и entry - эквивалентные понятия в wiki и ruthes.
             * */
            WikiSense source_sense = find_most_similar_sense(source_senses, entry);
            if (source_sense == null)
                continue;

            /*
             * Проходим по всем связям entry, так ранее мы нашли соответствие между понятиями в wiki и ruthes, поэтому
             * можно попробовать импортировать данные связи.
             * */
            for (Relation relation : entry.getRelations()) {
                /*
                 * Соседний узел к entry.
                 * */
                Entry adj_entry = relation.getEntry();
                RelationType adj_relation_type = relation.getType();

                /*
                 * Получаем список сенсов от леммы, имеющий такое же имя, что и соседний к entry узел в ruthes.
                 * */
                List<WikiSense> target_senses = lemma_to_sense.get(adj_entry.getName().toLowerCase());
                if (target_senses == null || target_senses.size() < 1)
                    continue;

                /*
                 * На данном этапе мы имеем:
                 * 1. Соответствие между сенсом в вики (source_sense) и понятием в рутезе (entry).
                 * 2. Соответствие между соседним к entry понятием (adj_entry) и списком сенсов (target_senses).
                 *
                 * Поэтому можно предположить, что эквивалетное adj_entry понятие находится в этом списке, поэтому мы
                 * можем воспроизвести связь от entry к adj_entry в виде связи от source_sense к одному из сенсов из
                 * этого списка.
                 * Для этого нам надо найти соответствие понятия adj_entry к одному из сенсов из target_senses.
                 * */
                WikiSense target_sense = find_most_similar_sense(target_senses, adj_entry);
                if (target_sense == null)
                    continue;

                /*
                 * Проверяем, что связь не существует.
                 * */
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

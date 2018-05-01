package ifmo.jackalope.ruthes.rules;

import com.tuneit.jackalope.dict.wiki.engine.core.SenseOption;
import com.tuneit.jackalope.dict.wiki.engine.core.SenseOptionType;
import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.*;

import java.util.List;
import java.util.Map;

/**
 * Делается предположение о сходстве сенсов в рутезе и вики на основе сходства соседних узлов и связей к ним.
 * Производится корректировка связей SenseOption - связь от сенса к лемме преобразуется в связь от сенса к сенсу
 */
public class FirstRule extends AbstractRule {

    FirstRule(final Map<String, WikiSense> wiki_senses, final RuthesSnapshot ruthes, final Map<String, List<WikiSense>> lemma_to_sense) {
        super(wiki_senses, ruthes, lemma_to_sense);
    }

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

            // корректировка связей: для option связей source_sense - попытка определить к какому сенсу идёт ссылка
            for (SenseOption option : source_sense.getAllOptions()) {
                String lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType();

                List<WikiSense> possible_target_senses = lemma_to_sense.get(lemma);
                if (possible_target_senses == null || possible_target_senses.size() < 1)
                    continue;

                // TODO: тут можно восстанавливать больше, чем одну связь
                // сравнение концептов/текстовых вхождений, имеющих связь с текущим concept и сенсов,
                // имеющих связь с текущим source_sense (possible_target_senses)
                WikiSense most_similar_target_sense = find_most_similar_sense_for_option(possible_target_senses, concept, option_type);
                if (most_similar_target_sense == null)
                    continue;

                String log = String.format("Can restore link from sense: %s\n\twith gloss: %s\n" +
                        "\tto sense: %s\n\twith gloss: %s\n" +
                        "\tby concept: %s\n",
                        source_sense.getLemma(), source_sense.getGloss(),
                        most_similar_target_sense.getLemma(), most_similar_target_sense.getGloss(),
                        concept.getName());
                links_restored++;
                System.out.println(log);
            }
        }

        for (TextEntry text_entry : ruthes.getEntries().values()) {
            List<WikiSense> source_senses = lemma_to_sense.get(text_entry.getName().toLowerCase());
            if (source_senses == null || source_senses.size() < 1)
                continue;

            WikiSense source_sense = find_most_similar_sense(source_senses, text_entry);
            if (source_sense == null)
                continue;

            // корректировка связей: для option связей source_sense - попытка определить к какому сенсу идёт ссылка
            for (SenseOption option : source_sense.getAllOptions()) {
                String lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType();

                List<WikiSense> possible_target_senses = lemma_to_sense.get(lemma);
                if (possible_target_senses == null || possible_target_senses.size() < 1)
                    continue;

                // сравнение концептов/текстовых вхождений, имеющих связь с текущим concept и сенсов,
                // имеющих связь с текущим source_sense (possible_target_senses)
                WikiSense most_similar_target_sense = find_most_similar_sense_for_option(possible_target_senses, text_entry, option_type);
                if (most_similar_target_sense == null)
                    continue;

                String log = String.format("Can restore link from sense: %s\n\twith gloss: %s\n" +
                        "\tto sense: %s\n\twith gloss: %s\n" +
                        "\tby text_entry: %s\n",
                        source_sense.getLemma(), source_sense.getGloss(),
                        most_similar_target_sense.getLemma(), most_similar_target_sense.getGloss(),
                        text_entry.getName());
                links_restored++;
                System.out.println(log);
            }
        }

        return links_restored;
    }
}

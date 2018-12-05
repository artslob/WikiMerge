package ifmo.jackalope.ruthes.rules;

import com.tuneit.jackalope.dict.wiki.engine.core.SenseOption;
import com.tuneit.jackalope.dict.wiki.engine.core.SenseOptionType;
import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.RuthesSnapshot;
import ifmo.jackalope.ruthes.entries.Entry;

import java.util.List;
import java.util.Map;

/**
 * Делается предположение о сходстве сенсов в рутезе и вики на основе сходства соседних узлов и связей к ним.
 * Производится корректировка связей SenseOption - связь от сенса к лемме преобразуется в связь от сенса к сенсу
 */
public class FirstRule extends AbstractRule {

    FirstRule(final Map<String, WikiSense> wiki_senses,
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
            List<WikiSense> source_senses = lemma_to_senses.get(entry.getName().toLowerCase());
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
             * Далее проводим корректировку связей.
             * Каждый сенс в wiki имеет неразрешённые связи (связи не к другим сенсам, а к леммам). Каждая лемма имеет
             * список сенсов, логично, что данная связь направлена к одному из них, но мы не знаем к какому (так как в
             * данный момент она направлена к лексеме). Поэтому мы проходим по всем таким связям и пытаемся определить,
             * к какому именно сенсу леммы направлена данная связь.
             * */
            for (SenseOption option : source_sense.getAllOptions()) {
                String lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType();

                /*
                 * Получаем список сенсов леммы, к которой направлена неразрешённая связь.
                 * */
                List<WikiSense> possible_target_senses = lemma_to_senses.get(lemma);
                if (possible_target_senses == null || possible_target_senses.size() < 1)
                    continue;

                // TODO: тут можно восстанавливать больше, чем одну связь
                /*
                 * На данном этапе мы имеем:
                 * 1. Сенс (source_sense), от которого идёт связь к лемме.
                 * 2. Тип данной связи
                 * 3. Список сенсов данной леммы (possible_target_senses).
                 * 4. Соответствие между source_sense в вики и понятием entry в ruthes.
                 *
                 * Можно предположить, что данная связь также есть и в ruthes. Поэтому мы должны пройти по всем связям
                 * понятия entry, имеющим такой же тип как и у связи в вики, и проверить на соответствие каждый узел на
                 * конце этой связи в ruthes с каждым сенсом леммы, к которой идёт неразрешённая связь. То есть мы
                 * сравниваем сенсы данной леммы (possible_target_senses) и все соседние узлы найденного понятия в
                 * ruthes (соседи понятия entry) - так они соседние узлы к source_sense и entry, между которыми было
                 * найдено соответствие.
                 * Соответствие между понятием из рутеза и сенсом в вики определяется посредством нахождения
                 * максимального количества совпадающих связей (одинаковый тип связи и похожий узел на конце связи.
                 * */
                WikiSense most_similar_target_sense = find_most_similar_sense_for_option(possible_target_senses, entry, option_type);
                if (most_similar_target_sense == null)
                    continue;

                links_restored++;
                log(source_sense, most_similar_target_sense, entry);
            }
        }

        return links_restored;
    }

    private void log(WikiSense source, WikiSense target, Entry entry) {
        String log = String.format(
                "Can restore link from sense: %s\n\twith gloss: %s\n" +
                "\tto sense: %s\n\twith gloss: %s\n" +
                "\tby concept: %s\n",
                source.getLemma(), source.getGloss(),
                target.getLemma(), target.getGloss(),
                entry.getName());
        System.out.println(log);
    }
}

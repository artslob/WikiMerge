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

public abstract class AbstractRule implements Rule {
    final Map<String, WikiSense> wiki_senses;
    final Map<String, List<WikiSense>> lemma_to_senses;
    final RuthesSnapshot ruthes;

    /**
     * @param wiki_senses     соответствие ключа сенса к сенсу
     * @param ruthes          снапшот рутеза
     * @param lemma_to_senses соответствие между именем леммы и списком её сенсов
     */
    AbstractRule(final Map<String, WikiSense> wiki_senses,
                 final RuthesSnapshot ruthes,
                 final Map<String, List<WikiSense>> lemma_to_senses) {
        this.wiki_senses = wiki_senses;
        this.ruthes = ruthes;
        this.lemma_to_senses = lemma_to_senses;
    }

    /**
     * Данный метод находит сенс из списка сенсов, к которому с наибольшей вероятностью направлена связь типа
     * option_type. Связь идёт от некоторого сенса в вики, с которым мы нашли соответсвие в виде понятия entry из
     * ruthes. Мы можем предполагать, что соседний к entry узел имеет эквивалент в списке сенсов, поэтому можем найти
     * его, сравнив связи соседа entry и сенса из списка соответственно.
     *
     * @param senses      список сенсов в вики, к которым идёт связь
     * @param option_type вид данной связи
     * @param entry       понятие из рутеза, эквивалент сенса в вики, от которого идёт связь
     */
    WikiSense find_most_similar_sense_for_option(List<WikiSense> senses, Entry entry, SenseOptionType option_type) {
        if (senses == null || senses.size() == 0)
            return null;
        if (senses.size() == 1)
            return senses.get(0);

        WikiSense result = null;
        int similarity = 0;

        for (Relation relation : entry.getRelations()) {
            Entry adj_entry = relation.getEntry();
            RelationType adj_relation_type = relation.getType();

            if (!compare_link_types(adj_relation_type, option_type))
                continue;

            for (WikiSense adj_sense : senses) {
                int current_similarity = compare_links(adj_sense, adj_entry);
                if (current_similarity > similarity) {
                    similarity = current_similarity;
                    result = adj_sense;
                }
            }
        }

        return result;
    }

    /**
     * Находит соответствие между понятием из ruthes и одним из сенсов вики из списка.
     * Соответствие находится путём сравнения соседних узлов понятий и связей к ним. То есть чем больше у сенса
     * эквивалентных узлов с понятием из рутеза, тем более вероятно, что это искомый сенс.
     *
     * @param source_senses одно из этих понятий соответствует понятию entry в ruthes
     * @param entry         понятие, к которому находится соответствие
     */
    WikiSense find_most_similar_sense(List<WikiSense> source_senses, Entry entry) {
        if (source_senses.size() <= 0)
            return null;
        if (source_senses.size() == 1)
            return source_senses.get(0);

        WikiSense current_sense = null;
        int similarity = 0;

        for (WikiSense sense : source_senses) {
            int current_similarity = compare_links(sense, entry);
            if (current_similarity > similarity) {
                current_sense = sense;
                similarity = current_similarity;
            }
        }

        return current_sense;
    }

    /**
     * Сравнивает связи к соседним узлам вики сенса и понятия рутеза.
     * Для этого сравнивает тип связей и соответсвие имён соседних узлов.
     */
    private int compare_links(WikiSense sense, Entry entry) {
        int similarity = 0;

        for (Relation relation : entry.getRelations()) {
            Entry target_entry = relation.getEntry();
            RelationType relation_type = relation.getType();

            /* проходим по неразрешённым связям сенса (связи к леммам, а не сенсам) */
            for (SenseOption option : sense.getAllOptions()) {
                String option_target_lemma = option.getOption().toString();
                SenseOptionType option_type = option.getType();

                if (option_target_lemma.equalsIgnoreCase(target_entry.getName())
                        && compare_link_types(relation_type, option_type))
                    similarity++;
            }

            /* проходим по всем связям к сенсам */
            for (Map.Entry<String, SenseOptionType> map_entry : sense.getLinks().entrySet()) {
                WikiSense link_target_sense = wiki_senses.get(map_entry.getKey());
                SenseOptionType sense_option = map_entry.getValue();

                if (link_target_sense.getLemma().equalsIgnoreCase(target_entry.getName())
                        && compare_link_types(relation_type, sense_option))
                    similarity++;
            }
        }

        return similarity;
    }

    boolean compare_link_types(RelationType ruthes_link, SenseOptionType wiki_link) {
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

        if (ruthes_link == RelationType.SYM_ASSOC && // АСЦ
                (
                        wiki_link == SenseOptionType.RELATED_TERM ||
                        wiki_link == SenseOptionType.COORDINATE_TERM ||
                        wiki_link == SenseOptionType.INFLECTION ||
                        wiki_link == SenseOptionType.QUOTATION ||
                        wiki_link == SenseOptionType.COLLOCATION ||
                        wiki_link == SenseOptionType.ALTERNATIVE_FORM
                )
           )
        {
            return true;
        }

        return ruthes_link == RelationType.SYNONYM && wiki_link == SenseOptionType.SYNONYM;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}

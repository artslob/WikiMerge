package ifmo.jackalope;

import com.tuneit.jackalope.dict.wiki.engine.core.SenseOption;
import com.tuneit.jackalope.dict.wiki.engine.core.SenseOptionType;
import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.RuthesSnapshot;
import ifmo.jackalope.ruthes.RuthesSnapshotManager;
import ifmo.jackalope.ruthes.rules.Rules;
import org.apache.commons.lang3.time.StopWatch;

import java.util.*;


public class App {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Provide first argument - path to snapshot!");
            System.exit(1);
        }
        SnapshotLoader wiki = new SnapshotLoader(args[0]);
        Map<String, WikiSense> wiki_senses = wiki.get_map_senses();

        RuthesSnapshotManager ruthesManager = new RuthesSnapshotManager("E:\\yad\\UNIVERSITY\\DIPLOM\\ruthes");
        RuthesSnapshot ruthes_snapshot = ruthesManager.getSnapshot();

//        for (WikiSense s : wiki_senses.values()){
//            if (s.getLinks().size() > 0) {
//                System.out.println(s);
//            }
//        }

        int result = Rules.apply(wiki_senses, ruthes_snapshot);
        System.out.println(result + " links were restored.");
        System.out.println("Program done.");
    }

    public static void fuzzySynonymSearch(SnapshotLoader wiki) {
        Collection<WikiSense> senses = wiki.get_senses();

        Map<String, List<WikiSense>> lemma_to_senses = new HashMap<>();

        for (WikiSense sense : senses) {
            String lexeme = sense.getLemma();
            if (lemma_to_senses.containsKey(lexeme)) {
                lemma_to_senses.get(lexeme).add(sense);
            }
            else {
                List<WikiSense> lexeme_senses = new ArrayList<>();
                lexeme_senses.add(sense);
                lemma_to_senses.put(lexeme, lexeme_senses);
            }
        }

        FuzzyTest fuzzyTest = new FuzzyTest();
        int counter = 0;
        StopWatch watch = StopWatch.createStarted();

        for (WikiSense sense : senses) {
            List<SenseOption> options = sense.getOptionsByType(SenseOptionType.SYNONYM);
            if (options == null || options.isEmpty())
                continue;
            for (SenseOption option : options) {
                String option_lemma = option.getOption().toString();
                List<WikiSense> possible_synonym_senses = lemma_to_senses.get(option_lemma);
                if (possible_synonym_senses == null)
                    continue;
                String first_gloss = sense.getObjectGloss().getGlossText();
                if (first_gloss == null || first_gloss.trim().isEmpty())
                    continue;
                for (WikiSense possible_synonym : possible_synonym_senses) {
                    String second_gloss = possible_synonym.getObjectGloss().getGlossText();
                    if (second_gloss == null || second_gloss.trim().isEmpty())
                        continue;
                    float factor = fuzzyTest.test_fuzzy_equality(first_gloss, second_gloss);
                    if (factor > 0.45f) {
                        System.out.println(String.format("%d. Can create sense link from %s to %s.\n" +
                                "    1st gloss: %s\n" +
                                "    2nd gloss: %s\n" +
                                "    ration %.02f\n" +
                                "    target lemma %s has more than 1 sense %b\n" +
                                "    sense has link to this sense %b\n",
                                ++counter, sense.getNamedId(), possible_synonym.getNamedId(),
                                sense.getObjectGloss().getGlossText(),
                                possible_synonym.getObjectGloss().getGlossText(),
                                factor,
                                option_lemma, possible_synonym_senses.size() > 1,
                                sense.getLinks().containsKey(possible_synonym.getId())
                        ));
                    }
                }
            }
        }

        watch.stop();
        System.out.println("Search for synonym within " + senses.size() + " senses got " + watch);
    }

}

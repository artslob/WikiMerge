package ifmo.jackalope.ruthes.rules;

import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.RuthesSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rules {

    private static final List<Rule> rules = new ArrayList<>();

    public static int apply(Map<String, WikiSense> wiki_senses, RuthesSnapshot ruthes) {

        if (rules.isEmpty()) {
            rules.add(new FirstRule(wiki_senses, ruthes));
        }

        int total = 0;

        for (Rule rule : rules) {
            int apply_result = rule.apply();
            total += apply_result;
            System.out.println("Rule " + rule + " created " + apply_result + " links");
        }

        return total;
    }
}

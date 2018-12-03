package ifmo.jackalope;

import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;
import ifmo.jackalope.ruthes.RuthesSnapshot;
import ifmo.jackalope.ruthes.RuthesSnapshotManager;
import ifmo.jackalope.ruthes.rules.Rules;

import java.util.Map;


public class App {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage:\narg 0: path to wiki snapshot\narg 1: path to ruthes directory");
            System.exit(1);
        }
        SnapshotLoader wiki = new SnapshotLoader(args[0]);
        Map<String, WikiSense> wiki_senses = wiki.get_map_senses();

        RuthesSnapshotManager ruthesManager = new RuthesSnapshotManager(args[1]);
        RuthesSnapshot ruthes_snapshot = ruthesManager.getSnapshot();

        int result = Rules.apply(wiki_senses, ruthes_snapshot);
        System.out.println(result + " links were restored.");
        System.out.println("Program done.");
    }
}

package ifmo.jackalope;

import com.tuneit.jackalope.dict.wiki.engine.core.WikiSense;

import java.util.Collection;


public class App {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Provide first argument - path to snapshot!");
            System.exit(1);
        }
        SnapshotLoader wiki = new SnapshotLoader(args[0]);
        Collection<WikiSense> senses = wiki.get_senses();
    }
}

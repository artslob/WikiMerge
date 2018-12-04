package ifmo.jackalope.ruthes.entries;

import java.util.List;

public interface Entry {
    String getId();

    String getName();

    List<Relation> getRelations();

}

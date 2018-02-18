package ifmo.jackalope.ruthes;

public class RuthesSnapshotManager {
    private RuthesSnapshot snapshot;

    public RuthesSnapshotManager(String dir_contain_xml) {
        snapshot = new RuthesSnapshot(dir_contain_xml);
    }

    public RuthesSnapshot getSnapshot() {
        return snapshot;
    }
}

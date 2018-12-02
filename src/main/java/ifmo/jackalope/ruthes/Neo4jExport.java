package ifmo.jackalope.ruthes;

import ifmo.jackalope.ruthes.entries.Concept;
import ifmo.jackalope.ruthes.entries.Relation;
import ifmo.jackalope.ruthes.entries.TextEntry;
import org.apache.commons.lang3.time.StopWatch;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.util.Objects;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jExport implements AutoCloseable {
    private final Driver driver;

    public Neo4jExport(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() {
        driver.close();
    }

    public void export(RuthesSnapshot ruthesSnapshot) {
        if (Objects.isNull(ruthesSnapshot)) {
            throw new IllegalStateException("Ruthes snapshot should be not null.");
        }
        try (Session session = driver.session()) {
            StopWatch watch = StopWatch.createStarted();
            clear_database(session);
            for (Concept concept : ruthesSnapshot.getConcepts().values()) {
                export_concept(session, concept);
            }
            for (TextEntry entry : ruthesSnapshot.getEntries().values()) {
                export_entry(session, entry);
            }
            for (Concept concept : ruthesSnapshot.getConcepts().values()) {
                export_relations(session, concept);
                export_synonyms(session, concept);
            }
            watch.stop();
            System.out.println(String.format("Export done for %s.", watch));
        }
    }

    private void export_concept(Session session, Concept concept) {
        session.writeTransaction(tx -> tx.run(
                "CREATE (n: Concept {id: $id, name: $name, gloss: $gloss})",
                parameters("id", concept.getId(), "name", concept.getName(), "gloss", concept.getGloss())
        ));
    }

    private void export_entry(Session session, TextEntry entry) {
        session.writeTransaction(tx -> tx.run(
                "CREATE (n: TextEntry {id: $id, name: $name, main_word: $main_word})",
                parameters("id", entry.getId(), "name", entry.getName(), "main_word", entry.getMain_word())
        ));
    }

    private void export_relations(Session session, Concept concept) {
        for (Relation relation : concept.getRelations()) {
            session.writeTransaction(tx -> tx.run(
                    "MATCH (f: Concept {id: $from_id})\n" +
                    "MATCH (t: Concept {id: $to_id})" +
                    "CREATE (f)-[:" + relation.getRelationType().name +"]->(t)",
                    parameters("from_id", concept.getId(), "to_id", relation.getConcept().getId())
            ));
        }
    }

    private void export_synonyms(Session session, Concept concept) {
        for (TextEntry synonym_entry : concept.getSynonyms()) {
            session.writeTransaction(tx -> tx.run(
                    "MATCH (c: Concept {id: $concept_id})\n" +
                    "MATCH (e: TextEntry {id: $entry_id})\n" +
                    "CREATE (c)-[:SYNONYM]->(e)\n" +
                    "CREATE (e)-[:SYNONYM]->(c)",
                    parameters("concept_id", concept.getId(), "entry_id", synonym_entry.getId())
            ));
        }
    }

    private void clear_database(Session session) {
        session.writeTransaction(tx -> tx.run(
                "MATCH (n) DETACH DELETE n"
        ));
    }

}

package eu.europa.ec.cc.drools;

import org.drools.reliability.core.ReliableGlobalResolverFactory;
import org.drools.reliability.core.SimpleReliableObjectStoreFactory;
import org.drools.reliability.core.TestableStorageManager;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import static org.drools.reliability.h2mvstore.H2MVStoreStorageManager.cleanUpDatabase;

@Component
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private final SessionFactory sessionFactory;
    private final TestableStorageManager storageManager;

    public App(SessionFactory sessionFactory, TestableStorageManager storageManager) {
        this.sessionFactory = sessionFactory;
        this.storageManager = storageManager;
    }

    public static void main(String... args) {
        SimpleReliableObjectStoreFactory.get("h2mvstore");
        ReliableGlobalResolverFactory.get("h2mvstore");

        // Clean the database before starting the application
        cleanUpDatabase();

        // Create the application context and run the application
        new AnnotationConfigApplicationContext(App.class.getPackageName())
                .getBean(App.class)
                .run();
    }

    public void run() {
        var sessionId = step1_insertPerson();

        storageManager.restart();
        step2_updateAge(sessionId);

        storageManager.restart();
        step3_loadSession(sessionId);

        storageManager.restart();
        step4_loadSession(sessionId);
    }

    public long step1_insertPerson() {
        LOG.info("----- step1 - Insert person -----");

        // Create a new Kie session
        var session = sessionFactory.createSession();

        // Insert person fact and fire all rules
        insertAndFire(session, new Person("666", "John Doe", 32));

        checkBeforeClose(session);

        // Return session ID to use when restoring
        return session.getIdentifier();
    }

    public void step2_updateAge(long sessionId) {
        LOG.info("----- step2 - Update age -----");

        // Restore previously stored Kie session
        var session = sessionFactory.restoreSession(sessionId);

        // Insert fact that will update person age and fire all rules
        insertAndFire(session, 32);

        checkBeforeClose(session);
    }

    public void step3_loadSession(long sessionId) {
        LOG.info("----- step3 - Just load Session -----");

        // Restore previously stored Kie session
        var session = sessionFactory.restoreSession(sessionId);

        checkBeforeClose(session);
    }

    public void step4_loadSession(long sessionId) {
        LOG.info("----- step4 - Just load Session -----");

        // Restore previously stored Kie session
        var session = sessionFactory.restoreSession(sessionId);

        checkBeforeClose(session);
    }

    public static void insertAndFire(KieSession session, Object object) {
        LOG.info("Inserting {}", object);

        var handle = session.insert(object);
        LOG.info("{} handle: {}", object.getClass().getSimpleName(), handle);

        var count = session.fireAllRules();
        LOG.info("Fired {} rule(s) after insert {}", count, object);
    }

    // Check that no more rules trigger before closing
    public static void checkBeforeClose(KieSession session) {
        var count = session.fireAllRules();
        LOG.info("Fired {} rule(s) before closing", count);
    }

}
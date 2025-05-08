package eu.europa.ec.cc.drools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drools.reliability.core.TestableStorageManager;
import org.kie.api.runtime.KieSession;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import static org.drools.reliability.h2mvstore.H2MVStoreStorageManager.cleanUpDatabase;

@Slf4j
@Component
@RequiredArgsConstructor
public class App {

    private final SessionFactory sessionFactory;
    private final TestableStorageManager storageManager;

    public static void main(String... args) {
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
        log.info("----- step1 - Insert person -----");

        // Create a new Kie session
        var session = sessionFactory.createSession();

        // Insert person fact and fire all rules
        insertAndFire(session, new Person("666", "John Doe", 32));

        checkBeforeClose(session);

        // Return session ID to use when restoring
        return session.getIdentifier();
    }

    public void step2_updateAge(long sessionId) {
        log.info("----- step2 - Update age -----");

        // Restore previously stored Kie session
        var session = sessionFactory.restoreSession(sessionId);

        // Insert fact that will update person age and fire all rules
        insertAndFire(session, 32);

        checkBeforeClose(session);
    }

    public void step3_loadSession(long sessionId) {
        log.info("----- step3 - Just load Session -----");

        // Restore previously stored Kie session
        var session = sessionFactory.restoreSession(sessionId);

        checkBeforeClose(session);
    }

    public void step4_loadSession(long sessionId) {
        log.info("----- step4 - Just load Session -----");

        // Restore previously stored Kie session
        var session = sessionFactory.restoreSession(sessionId);

        checkBeforeClose(session);
    }

    public static void insertAndFire(KieSession session, Object object) {
        log.info("Inserting {}", object);

        var handle = session.insert(object);
        log.info("{} handle: {}", object.getClass().getSimpleName(), handle);

        var count = session.fireAllRules();
        log.info("Fired {} rule(s) after insert {}", count, object);
    }

    // Check that no more rules trigger before closing
    public static void checkBeforeClose(KieSession session) {
        var count = session.fireAllRules();
        log.info("Fired {} rule(s) before closing", count);
    }

}
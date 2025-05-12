package eu.europa.ec.cc.drools;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.kie.api.runtime.conf.PersistedSessionOption.PersistenceStrategy.STORES_ONLY;
import static org.kie.api.runtime.conf.PersistedSessionOption.SafepointStrategy.ALWAYS;
import static org.kie.api.runtime.conf.PersistedSessionOption.fromSession;
import static org.kie.api.runtime.conf.PersistedSessionOption.newSession;

@Component
public class SessionFactory {

    private final Logger log = LoggerFactory.getLogger(SessionFactory.class);

    private final KieServices kieServices;
    private final KieBase kieBase;

    public SessionFactory(KieServices kieServices, KieBase kieBase) {
        this.kieServices = kieServices;
        this.kieBase = kieBase;
    }

    public KieSession createSession() {
        return getKieSession(newSession());
    }

    public KieSession restoreSession(long sessionId) {
        return getKieSession(fromSession(sessionId));
    }

    public KieSession getKieSession(PersistedSessionOption option) {
        // Create configuration with persistence options
        var conf = kieServices.newKieSessionConfiguration();
        conf.setOption(option
                .withPersistenceStrategy(STORES_ONLY)
                .withSafepointStrategy(ALWAYS));

        // Create kie session
        var kieSession = kieBase.newKieSession(conf, null);

        log.info("Session id: {}, fact count: {}",
                kieSession.getIdentifier(),
                kieSession.getFactCount());

        // Fire all rules (should be 0)
        var count = kieSession.fireAllRules();

        if (count > 0) {
            log.error("No rules should have been fired after init, but {} rule(s) did fire", count);
        }

        return kieSession;
    }

}

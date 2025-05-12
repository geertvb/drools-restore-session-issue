package eu.europa.ec.cc.drools;

import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.core.TestableStorageManager;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public StorageManagerFactory storageManagerFactory() {
        return StorageManagerFactory.get("h2mvstore");
    }

    @Bean
    public TestableStorageManager storageManager(StorageManagerFactory storageManagerFactory) {
        return (TestableStorageManager) storageManagerFactory.getStorageManager();
    }

    @Bean
    public KieServices kieServices() {
        return KieServices.Factory.get();
    }

    @Bean
    public KieContainer kieContainer(KieServices kieServices) {
        return kieServices.getKieClasspathContainer();
    }

    @Bean
    public KieBase kieBase(KieContainer kieContainer) {
        return kieContainer.getKieBase();
    }

}

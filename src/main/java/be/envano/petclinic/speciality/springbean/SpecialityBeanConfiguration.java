package be.envano.petclinic.speciality.springbean;

import be.envano.petclinic.core.transaction.Transaction;
import be.envano.petclinic.core.transaction.springbean.TransactionFactory;
import be.envano.petclinic.speciality.SpecialtyCatalog;
import be.envano.petclinic.speciality.persistence.SpecialityJpaRepository;
import be.envano.petclinic.speciality.persistence.SpecialityJpaRepositoryAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class SpecialityBeanConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpecialityBeanConfiguration.class);

    @Bean
    public SpecialtyCatalog specialtyCatalog(
        SpecialityJpaRepository repository,
        TransactionTemplate transactionTemplate,
        ApplicationEventPublisher eventPublisher
    ) {
        logger.info("Initializing SpecialtyCatalog");
        final var storage = new SpecialityJpaRepositoryAdapter(repository);
        final Transaction transaction = TransactionFactory.configure(transactionTemplate);
        return new SpecialtyCatalog(transaction, storage, eventPublisher::publishEvent);
    }

}

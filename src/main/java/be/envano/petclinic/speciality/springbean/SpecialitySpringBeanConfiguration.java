package be.envano.petclinic.speciality.springbean;

import be.envano.petclinic.platform.journal.Journal;
import be.envano.petclinic.platform.transaction.Transaction;
import be.envano.petclinic.platform.transaction.springbean.TransactionFactory;
import be.envano.petclinic.speciality.SpecialtyCatalog;
import be.envano.petclinic.speciality.jdbc.JdbcSpecialtyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class SpecialitySpringBeanConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpecialitySpringBeanConfiguration.class);

    @Bean
    public SpecialtyCatalog specialtyCatalog(
        JdbcClient jdbcClient,
        TransactionTemplate transactionTemplate,
        Journal journal
    ) {
        logger.info("Initializing SpecialtyCatalog");
		final var repository = new JdbcSpecialtyRepository(jdbcClient);
		final Transaction transaction = TransactionFactory.configure(transactionTemplate);
        return new SpecialtyCatalog(journal, transaction, repository);
    }

}

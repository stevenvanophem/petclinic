package be.envano.petclinic.speciality.bean;

import be.envano.petclinic.speciality.SpecialtyCatalog;
import be.envano.petclinic.speciality.persistence.SpecialityJpaRepository;
import be.envano.petclinic.speciality.persistence.SpecialityJpaRepositoryAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpecialityBeanConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpecialityBeanConfiguration.class);

    @Bean
    public SpecialtyCatalog specialtyCatalog(
        SpecialityJpaRepository repository,
        ApplicationEventPublisher eventPublisher
    ) {
        logger.info("Initializing SpecialtyCatalog");
        final var storage = new SpecialityJpaRepositoryAdapter(repository);
        return new SpecialtyCatalog(storage, eventPublisher::publishEvent);
    }

}

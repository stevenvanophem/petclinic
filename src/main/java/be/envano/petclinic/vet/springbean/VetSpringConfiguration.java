package be.envano.petclinic.vet.springbean;

import be.envano.petclinic.core.transaction.Transaction;
import be.envano.petclinic.core.transaction.springbean.TransactionFactory;
import be.envano.petclinic.vet.VetRepository;
import be.envano.petclinic.vet.VetRoster;
import be.envano.petclinic.vet.persistence.VetJpaRepository;
import be.envano.petclinic.vet.persistence.VetJpaRepositoryAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class VetSpringConfiguration {

    @Bean
    public VetRoster vetRoster(
        TransactionTemplate transactionTemplate,
        VetJpaRepository repository,
        ApplicationEventPublisher eventPublisher
    ) {
        final VetRepository storage = new VetJpaRepositoryAdapter(repository);
        final Transaction transaction = TransactionFactory.configure(transactionTemplate);
        return new VetRoster(transaction, storage, eventPublisher::publishEvent);
    }

}

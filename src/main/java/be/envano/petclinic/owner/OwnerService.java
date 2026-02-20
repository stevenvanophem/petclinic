package be.envano.petclinic.owner;

import be.envano.petclinic.platform.journal.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class OwnerService {

    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

    private final Journal journal;
    private final OwnerRepository repository;

    OwnerService(
        Journal journal,
        OwnerRepository repository
    ) {
        this.journal = journal;
        this.repository = repository;
    }

    @Transactional
    public Owner register(OwnerCommand.Register command) {
        Objects.requireNonNull(command);

        logger.debug("Registering an owner");
        logger.trace("{}", command);

        Owner.Id id = repository.nextId();
        Owner owner = new Owner(id, command);
        owner.events().forEach(journal::appendEvent);
        return repository.add(owner);
    }

    @Transactional
    public Owner rename(OwnerCommand.Rename command) {
        Objects.requireNonNull(command);

        logger.debug("Renaming an owner");
        logger.trace("{}", command);

        Owner owner = repository.findById(command.id()).orElseThrow(OwnerException.NotFound::new);
        owner.rename(command);
        owner.events().forEach(journal::appendEvent);
        return repository.update(owner);
    }

    @Transactional
    public Owner changeContactDetails(OwnerCommand.ChangeContactDetails command) {
        Objects.requireNonNull(command);

        logger.debug("Changing owner contact details");
        logger.trace("{}", command);

        Owner owner = repository.findById(command.id()).orElseThrow(OwnerException.NotFound::new);
        owner.changeContactDetails(command);
        owner.events().forEach(journal::appendEvent);
        return repository.update(owner);
    }

    @Transactional(readOnly = true)
    public List<Owner> findAll() {
        logger.debug("Find all owners");
        return repository.findAll();
    }

}

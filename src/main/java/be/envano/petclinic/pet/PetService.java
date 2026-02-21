package be.envano.petclinic.pet;

import be.envano.petclinic.platform.journal.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    private final Journal journal;
    private final PetRepository repository;

    PetService(
        Journal journal,
        PetRepository repository
    ) {
        this.journal = journal;
        this.repository = repository;
    }

    @Transactional
    public Pet register(PetCommand.Register command) {
        Objects.requireNonNull(command);

        logger.debug("Registering a pet");
        logger.trace("{}", command);

        Pet.Id id = repository.nextId();
        Pet pet = new Pet(id, command);
        pet.events().forEach(journal::appendEvent);
        return repository.add(pet);
    }

    @Transactional
    public Pet rename(PetCommand.Rename command) {
        Objects.requireNonNull(command);

        logger.debug("Renaming a pet");
        logger.trace("{}", command);

        Pet pet = repository.findById(command.id()).orElseThrow(PetException.NotFound::new);
        pet.rename(command);
        pet.events().forEach(journal::appendEvent);
        return repository.update(pet);
    }

    @Transactional(readOnly = true)
    public List<Pet> findAll() {
        logger.debug("Find all pets");
        return repository.findAll();
    }

}

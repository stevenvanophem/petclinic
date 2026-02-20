package be.envano.petclinic.specialty;

import be.envano.petclinic.platform.journal.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SpecialtyService {

    private static final Logger logger = LoggerFactory.getLogger(SpecialtyService.class);

    private final Journal journal;
    private final SpecialtyRepository repository;

    SpecialtyService(
        Journal journal,
        SpecialtyRepository repository
    ) {
        this.repository = repository;
        this.journal = journal;
    }

    @Transactional
    public Specialty register(SpecialtyCommand.Register command) {
        Objects.requireNonNull(command);

        logger.debug("Registering specialty");
        logger.trace("{}", command);

        Specialty.Id id = repository.nextId();
        Specialty specialty = new Specialty(id, command);
        specialty.events().forEach(journal::appendEvent);
        return repository.add(specialty);
    }

    @Transactional
    public Specialty rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        logger.debug("Renaming specialty");
        logger.trace("{}", command);

        Specialty specialty = repository.findById(command.id()).orElseThrow(SpecialtyException.NotFound::new);
        specialty.rename(command);
        specialty.events().forEach(journal::appendEvent);
        return repository.update(specialty);
    }

    @Transactional(readOnly = true)
    public List<Specialty> findAll() {
        logger.debug("Find all specialties");

        return repository.findAll();
    }

}

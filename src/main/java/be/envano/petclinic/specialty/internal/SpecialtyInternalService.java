package be.envano.petclinic.specialty.internal;

import be.envano.petclinic.platform.journal.Journal;
import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCommand;
import be.envano.petclinic.specialty.SpecialtyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SpecialtyInternalService implements SpecialtyService {

    private static final Logger logger = LoggerFactory.getLogger(SpecialtyInternalService.class);

    private final Journal journal;
    private final SpecialtyRepository repository;

    public SpecialtyInternalService(
        Journal journal,
        SpecialtyRepository repository
    ) {
        this.repository = repository;
        this.journal = journal;
    }

    @Override
    @Transactional
    public Specialty register(SpecialtyCommand.Register command) {
        Objects.requireNonNull(command);

        logger.debug("Registering specialty");
        logger.trace("{}", command);

        Specialty.Id id = repository.nextId();
        SpecialtyWriteModel specialty = SpecialtyWriteModel.register(id, command);
        specialty.events().forEach(journal::appendEvent);
        return repository.add(specialty);
    }

    @Override
    @Transactional
    public Specialty rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        logger.debug("Renaming specialty");
        logger.trace("{}", command);

        SpecialtyWriteModel specialty = repository.findById(command.id()).orElseThrow();
        specialty.rename(command);
        specialty.events().forEach(journal::appendEvent);
        return repository.update(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialty> findAll() {
        logger.debug("Find all specialties");

        return repository.findAll();
    }

}

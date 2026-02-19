package be.envano.petclinic.specialty.internal;

import be.envano.petclinic.platform.journal.Journal;
import be.envano.petclinic.platform.transaction.Transaction;
import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCatalog;
import be.envano.petclinic.specialty.SpecialtyCommand;
import be.envano.petclinic.specialty.internal.jdbc.JdbcSpecialtyRepository;

import java.util.List;
import java.util.Objects;

import static java.lang.System.Logger;
import static java.lang.System.Logger.Level;
import static java.lang.System.getLogger;

public class SpecialtyCatalogService implements SpecialtyCatalog {

    private static final Logger LOGGER = getLogger(SpecialtyCatalogService.class.getName());

    private final Journal journal;
    private final Transaction transaction;
    private final JdbcSpecialtyRepository repository;

    public SpecialtyCatalogService(
        Journal journal,
        Transaction transaction,
        JdbcSpecialtyRepository repository
    ) {
        this.transaction = transaction;
        this.repository = repository;
        this.journal = journal;
    }

    @Override
    public Specialty register(SpecialtyCommand.Register command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Registering specialty");
        LOGGER.log(Level.TRACE, command::toString);

        return transaction.in(() -> {
            Specialty.Id id = repository.nextId();
            SpecialtyAggregate specialty = SpecialtyAggregate.register(id, command);
            specialty.events().forEach(journal::appendEvent);
            return repository.add(specialty);
        });
    }

    @Override
    public Specialty rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Renaming specialty");
        LOGGER.log(Level.TRACE, command::toString);

        return transaction.in(() -> {
            SpecialtyAggregate specialty = repository.findById(command.id()).orElseThrow();
            specialty.rename(command);
            specialty.events().forEach(journal::appendEvent);
            return repository.update(specialty);
        });
    }

    @Override
    public List<Specialty> findAll() {
        LOGGER.log(Level.DEBUG, "Find all specialties");

        return repository.findAll();
    }

}

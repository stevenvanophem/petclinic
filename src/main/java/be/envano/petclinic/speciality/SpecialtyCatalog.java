package be.envano.petclinic.speciality;

import be.envano.petclinic.util.transaction.Transaction;

import java.util.List;
import java.util.Objects;

import static java.lang.System.Logger;
import static java.lang.System.Logger.Level;
import static java.lang.System.getLogger;

public class SpecialtyCatalog {

    private static final Logger LOGGER = getLogger(SpecialtyCatalog.class.getName());

    private final Transaction transaction;
    private final SpecialtyRepository repository;
    private final SpecialtyEventPublisher eventPublisher;

    public SpecialtyCatalog(
        Transaction transaction,
        SpecialtyRepository repository,
        SpecialtyEventPublisher eventPublisher
    ) {
        this.eventPublisher = eventPublisher;
        this.transaction = transaction;
        this.repository = repository;
    }

    public Specialty register(SpecialtyCommand.Register command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Registering specialty");
        LOGGER.log(Level.TRACE, command.toString());

        return transaction.perform(() -> {
            Specialty specialty = new Specialty(command);
            specialty.events().forEach(eventPublisher::publish);
            return repository.save(specialty);
        });
    }

    public Specialty rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Renaming specialty");
        LOGGER.log(Level.TRACE, command.toString());

        return transaction.perform(() -> {
            Specialty specialty = repository.findById(command.id()).orElseThrow();
            specialty.rename(command);
            specialty.events().forEach(eventPublisher::publish);
            return repository.save(specialty);
        });
    }

    public List<Specialty> findAll() {
        return repository.findAll();
    }

}

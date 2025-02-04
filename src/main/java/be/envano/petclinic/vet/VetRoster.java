package be.envano.petclinic.vet;

import be.envano.petclinic.core.transaction.Transaction;

import java.util.Objects;

import static java.lang.System.Logger;
import static java.lang.System.Logger.Level;
import static java.lang.System.getLogger;

public class VetRoster {

    private static final Logger LOGGER = getLogger(VetRoster.class.getName());

    private final Transaction transaction;
    private final VetRepository repository;
    private final VetEventPublisher eventPublisher;

    public VetRoster(
        Transaction transaction,
        VetRepository repository,
        VetEventPublisher eventPublisher
    ) {
        this.transaction = transaction;
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    public Vet hire(VetCommand.Hire command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Hiring a vet");
        LOGGER.log(Level.TRACE, command.toString());

        return transaction.perform(() -> {
            Vet vet = new Vet(command);
            vet.events().forEach(eventPublisher::publish);
            return repository.save(vet);
        });
    }

    public void fire(VetCommand.Fire command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Fire a vet");
        LOGGER.log(Level.TRACE, command.toString());

        transaction.perform(() -> {
            final Vet.Id id = command.id();
            Vet vet = repository.findById(id).orElseThrow(() -> new VetException.NotFound(id));
            vet.events().forEach(eventPublisher::publish);
            repository.delete(vet);
        });
    }

    public Vet rename(VetCommand.Rename command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Renaming a vet");
        LOGGER.log(Level.TRACE, command.toString());

        return transaction.perform(() -> {
            final Vet.Id id = command.id();
            Vet vet = repository.findById(id).orElseThrow(() -> new VetException.NotFound(id));
            vet.rename(command);
            vet.events().forEach(eventPublisher::publish);
            return repository.save(vet);
        });
    }

    public Vet specialize(VetCommand.Specialize command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Specializing a vet");
        LOGGER.log(Level.TRACE, command.toString());

        return transaction.perform(() -> {
            Vet.Id id = command.id();
            Vet vet = repository.findById(id).orElseThrow(() -> new VetException.NotFound(id));
            vet.specialize(command);
            vet.events().forEach(eventPublisher::publish);
            return repository.save(vet);
        });
    }

}

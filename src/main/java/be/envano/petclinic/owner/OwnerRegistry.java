package be.envano.petclinic.owner;

import be.envano.petclinic.core.transaction.Transaction;

import java.util.Objects;

import static java.lang.System.Logger.Level;
import static java.lang.System.getLogger;

public class OwnerRegistry {

    private static final System.Logger LOGGER = getLogger(OwnerRegistry.class.getName());

    private final Transaction transaction;
    private final OwnerRepository repository;
    private final OwnerEventPublisher eventPublisher;

    public OwnerRegistry(
        Transaction transaction,
        OwnerRepository repository,
        OwnerEventPublisher eventPublisher
    ) {
        this.transaction = transaction;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public Owner register(OwnerCommand.Register command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Registering an owner");
        LOGGER.log(Level.TRACE, command.toString());

        return transaction.perform(() -> {
            Owner owner = new Owner(command);
            Owner storedOwner = repository.save(owner);
            storedOwner.events().forEach(eventPublisher::publish);
            return storedOwner;
        });
    }
    
    public Owner rename(OwnerCommand.Rename command) {
        Objects.requireNonNull(command);
        
        LOGGER.log(Level.DEBUG, "Renaming an owner");
        LOGGER.log(Level.TRACE, command.toString());
    
        return transaction.perform(() -> {
            final Owner.Id id = command.id();

            Owner owner = repository.findById(id).orElseThrow(() -> new OwnerException.NotFound(id));
            owner.rename(command);

            Owner storedOwner = repository.save(owner);
            storedOwner.events().forEach(eventPublisher::publish);

            return storedOwner;
        });
    }

}

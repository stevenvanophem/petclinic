package be.envano.petclinic.vet;

import be.envano.petclinic.core.journal.Journal;
import be.envano.petclinic.core.transaction.Transaction;

import java.util.Objects;

import static java.lang.System.Logger;
import static java.lang.System.Logger.Level;
import static java.lang.System.getLogger;

public class VetRoster {

	private static final Logger LOGGER = getLogger(VetRoster.class.getName());

	private final Journal journal;
	private final Transaction transaction;
	private final VetRepository repository;

	public VetRoster(
		Journal journal,
		Transaction transaction,
		VetRepository repository
	) {
		this.transaction = transaction;
		this.repository = repository;
		this.journal = journal;
	}

	public Vet hire(VetCommand.Hire command) {
		Objects.requireNonNull(command);

		LOGGER.log(Level.DEBUG, "Hiring a vet");
		LOGGER.log(Level.TRACE, command.toString());

		return transaction.in(() -> {
			Vet vet = new Vet(command);
			vet.events().forEach(journal::appendEvent);
			return repository.save(vet);
		});
	}

	public void fire(VetCommand.Fire command) {
		Objects.requireNonNull(command);

		LOGGER.log(Level.DEBUG, "Fire a vet");
		LOGGER.log(Level.TRACE, command.toString());

		transaction.in(() -> {
			final Vet.Id id = command.id();
			Vet vet = repository.findById(id).orElseThrow(() -> new VetException.NotFound(id));
			vet.events().forEach(journal::appendEvent);
			repository.delete(vet);
		});
	}

	public Vet rename(VetCommand.Rename command) {
		Objects.requireNonNull(command);

		LOGGER.log(Level.DEBUG, "Renaming a vet");
		LOGGER.log(Level.TRACE, command.toString());

		return transaction.in(() -> {
			final Vet.Id id = command.id();
			Vet vet = repository.findById(id).orElseThrow(() -> new VetException.NotFound(id));
			vet.rename(command);
			vet.events().forEach(journal::appendEvent);
			return repository.save(vet);
		});
	}

	public Vet specialize(VetCommand.Specialize command) {
		Objects.requireNonNull(command);

		LOGGER.log(Level.DEBUG, "Specializing a vet");
		LOGGER.log(Level.TRACE, command.toString());

		return transaction.in(() -> {
			Vet.Id id = command.id();
			Vet vet = repository.findById(id).orElseThrow(() -> new VetException.NotFound(id));
			vet.specialize(command);
			vet.events().forEach(journal::appendEvent);
			return repository.save(vet);
		});
	}

}

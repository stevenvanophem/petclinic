package be.envano.petclinic.speciality;


import be.envano.petclinic.core.journal.Journal;
import be.envano.petclinic.core.transaction.Transaction;

import java.util.List;
import java.util.Objects;

import static java.lang.System.Logger;
import static java.lang.System.Logger.Level;
import static java.lang.System.getLogger;

public class SpecialtyCatalog {

    private static final Logger LOGGER = getLogger(SpecialtyCatalog.class.getName());

	private final Journal journal;
	private final Transaction transaction;
	private final SpecialtyRepository repository;

    public SpecialtyCatalog(
		Journal journal,
        Transaction transaction,
        SpecialtyRepository repository
    ) {
        this.transaction = transaction;
		this.repository = repository;
		this.journal = journal;
	}

    public Specialty register(SpecialtyCommand.Register command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Registering specialty");
        LOGGER.log(Level.TRACE, command.toString());

        return transaction.in(() -> {
			Specialty.Id id = repository.nextId();
			Specialty specialty = new Specialty(id, command);
            specialty.events().forEach(journal::appendEvent);
            return repository.add(specialty);
        });
    }

    public Specialty rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Renaming specialty");
        LOGGER.log(Level.TRACE, command.toString());

        return transaction.in(() -> {
            Specialty specialty = repository.findById(command.id()).orElseThrow();
            specialty.rename(command);
            specialty.events().forEach(journal::appendEvent);
            return repository.update(specialty);
        });
    }

    public List<Specialty> findAll() {
        return repository.findAll();
    }

}

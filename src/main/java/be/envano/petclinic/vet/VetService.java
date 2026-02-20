package be.envano.petclinic.vet;

import be.envano.petclinic.platform.journal.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class VetService {

	private static final Logger logger = LoggerFactory.getLogger(VetService.class);

	private final Journal journal;
	private final VetRepository repository;

	VetService(
		Journal journal,
		VetRepository repository
	) {
		this.repository = repository;
		this.journal = journal;
	}

	@Transactional
	public Vet hire(VetCommand.Hire command) {
		Objects.requireNonNull(command);

		logger.debug("Hiring a vet");
		logger.trace("{}", command);

		Vet.Id id = repository.nextId();
		Vet vet = new Vet(id, command);
		vet.events().forEach(journal::appendEvent);
		return repository.add(vet);
	}

	@Transactional
	public void fire(VetCommand.Fire command) {
		Objects.requireNonNull(command);

		logger.debug("Fire a vet");
		logger.trace("{}", command);

		final Vet.Id id = command.id();
		Vet vet = repository.findById(id).orElseThrow(VetException.NotFound::new);
		vet.fire(command);
		vet.events().forEach(journal::appendEvent);
		repository.delete(vet);
	}

	@Transactional
	public Vet rename(VetCommand.Rename command) {
		Objects.requireNonNull(command);

		logger.debug("Renaming a vet");
		logger.trace("{}", command);

		final Vet.Id id = command.id();
		Vet vet = repository.findById(id).orElseThrow(VetException.NotFound::new);
		vet.rename(command);
		vet.events().forEach(journal::appendEvent);
		return repository.update(vet);
	}

	@Transactional
	public Vet specialize(VetCommand.Specialize command) {
		Objects.requireNonNull(command);

		logger.debug("Specializing a vet");
		logger.trace("{}", command);

		Vet.Id id = command.id();
		Vet vet = repository.findById(id).orElseThrow(VetException.NotFound::new);
		vet.specialize(command);
		vet.events().forEach(journal::appendEvent);
		return repository.update(vet);
	}

	@Transactional
	public Vet deSpecialize(VetCommand.DeSpecialize command) {
		Objects.requireNonNull(command);

		logger.debug("De-specializing a vet");
		logger.trace("{}", command);

		Vet.Id id = command.id();
		Vet vet = repository.findById(id).orElseThrow(VetException.NotFound::new);
		vet.deSpecialize(command);
		vet.events().forEach(journal::appendEvent);
		return repository.update(vet);
	}

	@Transactional(readOnly = true)
	public List<Vet> findAll() {
		logger.debug("Find all vets");
		return repository.findAll();
	}

}

package be.envano.petclinic.vet;

import static java.lang.System.Logger;
import static java.lang.System.Logger.*;

import java.util.Objects;

import be.envano.petclinic.specialty.Specialty;

public class VetRoster {

	private static final Logger LOGGER = System.getLogger(VetRoster.class.getName());

	private final VetRepository repository;
	private final VetIdSequencer idSequencer;
	private final VetEventPublisher eventPublisher;

	public VetRoster(VetRepository repository, VetEventPublisher eventPublisher, VetIdSequencer idSequencer) {
		Objects.requireNonNull(eventPublisher, "vet event published missing");
		Objects.requireNonNull(idSequencer, "vet id sequencer missing");
		Objects.requireNonNull(repository, "vet repository missing");
		this.idSequencer = idSequencer;
		this.repository = repository;
		this.eventPublisher = eventPublisher;
	}

	public Vet recruit(Vet.Recruit command) {
		Objects.requireNonNull(command, "vet command missing");

		LOGGER.log(Level.DEBUG, "Recruiting a new vet", command);
		LOGGER.log(Level.TRACE, command);

		final Vet.Id id = idSequencer.nextId();
		return Vet.recruit(id, command)
			.then(repository::save)
			.then(eventPublisher::recruited);
	}

	public Vet resign(Vet.Resign command) {
		Objects.requireNonNull(command, "vet command missing");

		LOGGER.log(Level.DEBUG, "A vet is resigning", command);
		LOGGER.log(Level.TRACE, command);

		final Vet.Id id = command.id();
		return repository.findById(id).orElseThrow(Vet.NotFound::new)
			.then(repository::delete)
			.then(eventPublisher::resigned);
	}

	public Vet specialize(Vet.Specialize command) {
		Objects.requireNonNull(command, "vet command missing");

		LOGGER.log(Level.DEBUG, "A vet is specializing");
		LOGGER.log(Level.TRACE, command);

		final Vet.Id id = command.id();
		final Specialty.Id specialtyId = command.specialtyId();
		return repository.findById(id).orElseThrow(Vet.NotFound::new)
			.then(vet -> vet.specialize(specialtyId))
			.then(repository::save)
			.then(vet -> eventPublisher.specialized(vet, specialtyId));
	}

	public Vet removeSpecialty(Vet.RemoveSpecialty command) {
		Objects.requireNonNull(command, "vet command missing");

		LOGGER.log(Level.DEBUG, "A vets specialty is to be removed");
		LOGGER.log(Level.TRACE, command);

		final Vet.Id id = command.id();
		final Specialty.Id specialtyId = command.specialtyId();
		return repository.findById(id).orElseThrow(Vet.NotFound::new)
			.then(vet -> vet.removeSpecialty(specialtyId))
			.then(repository::save)
			.then(vet -> eventPublisher.specialtyRemoved(vet, specialtyId));
	}

	public Vet correctName(Vet.CorrectName command) {
		Objects.requireNonNull(command, "vet command missing");

		LOGGER.log(Level.DEBUG, "A vets name is to be corrected");
		LOGGER.log(Level.TRACE, command);

		final Vet.Id id = command.id();
		final Vet.Name name = command.name();
		return repository.findById(id).orElseThrow(Vet.NotFound::new)
			.then(vet -> vet.correctName(name))
			.then(repository::save)
			.then(eventPublisher::nameCorrected);
	}

}

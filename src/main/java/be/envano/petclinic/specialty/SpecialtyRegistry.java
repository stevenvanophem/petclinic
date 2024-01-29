package be.envano.petclinic.specialty;

import static java.lang.System.getLogger;

import java.util.Objects;

public class SpecialtyRegistry {

	private static final System.Logger LOGGER = getLogger(SpecialtyRegistry.class.getName());

	private final SpecialtyRepository repository;
	private final SpecialtyIdSequencer idSequencer;
	private final SpecialtyEventPublisher eventPublisher;

	public SpecialtyRegistry(
		SpecialtyRepository repository,
		SpecialtyIdSequencer idSequencer,
		SpecialtyEventPublisher eventPublisher
	) {
		Objects.requireNonNull(repository, "specialty repository missing");
		Objects.requireNonNull(idSequencer, "specialty id sequencer missing");
		Objects.requireNonNull(eventPublisher, "specialty event publisher missing");
		this.eventPublisher = eventPublisher;
		this.idSequencer = idSequencer;
		this.repository = repository;
	}

	public Specialty register(Specialty.Register command) {
		Objects.requireNonNull(command, "specialty command missing");

		LOGGER.log(System.Logger.Level.DEBUG, "Registering a new specialty");
		LOGGER.log(System.Logger.Level.TRACE, command);

		final Specialty.Id id = idSequencer.nextId();
		return Specialty.register(id, command)
			.then(repository::save)
			.then(eventPublisher::registered);
	}

	public Specialty expunge(Specialty.Expunge command) {
		Objects.requireNonNull(command, "specialty command missing");

		LOGGER.log(System.Logger.Level.DEBUG, "Expunging a specialty from the registry");
		LOGGER.log(System.Logger.Level.TRACE, command);

		final Specialty.Id id = command.id();
		return repository.findById(id).orElseThrow(Specialty.NotFound::new)
			.then(repository::delete)
			.then(eventPublisher::expunged);
	}

}

package be.envano.petclinic.speciality;

import java.util.Objects;

import static java.lang.System.*;
import static java.lang.System.Logger.*;

public class SpecialtyCatalog {

    private static final Logger LOGGER = getLogger(SpecialtyCatalog.class.getName());

    private final SpecialtyRepository repository;
    private final SpecialityIdSequencer idSequencer;
    private final SpecialtyEventPublisher eventPublisher;

    public SpecialtyCatalog(
        SpecialtyRepository repository,
        SpecialityIdSequencer idSequencer,
        SpecialtyEventPublisher eventPublisher
    ) {
        this.eventPublisher = eventPublisher;
        this.idSequencer = idSequencer;
        this.repository = repository;
    }

    public Specialty register(SpecialtyCommand.Register command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Registering specialty");
        LOGGER.log(Level.TRACE, command.toString());

        final long id = idSequencer.nextId();
        return new Specialty(command, id)
            .map(repository::save)
            .tap(eventPublisher::registered);
    }

    public Specialty rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        LOGGER.log(Level.DEBUG, "Renaming specialty");
        LOGGER.log(Level.TRACE, command.toString());

        return repository.findById(command.id()).orElseThrow()
            .map(specialty -> specialty.rename(command))
            .map(repository::save)
            .tap(eventPublisher::renamed);
    }

}

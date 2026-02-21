package be.envano.petclinic.pet;

import java.util.Objects;

public interface PetCommand {

    record Rehydrate(
        Pet.Id id,
        Pet.Name name,
        Pet.BirthDate birthDate,
        Pet.Type type,
        Pet.OwnerId ownerId,
        int version
    ) {

        public Rehydrate {
            Objects.requireNonNull(id);
            Objects.requireNonNull(name);
            Objects.requireNonNull(birthDate);
            Objects.requireNonNull(type);
            Objects.requireNonNull(ownerId);
        }

    }

    record Register(
        Pet.Name name,
        Pet.BirthDate birthDate,
        Pet.Type type,
        Pet.OwnerId ownerId
    ) {

        public Register {
            Objects.requireNonNull(name);
            Objects.requireNonNull(birthDate);
            Objects.requireNonNull(type);
            Objects.requireNonNull(ownerId);
        }

    }

    record Rename(
        Pet.Id id,
        Pet.Name name,
        int version
    ) {

        public Rename {
            Objects.requireNonNull(id);
            Objects.requireNonNull(name);
        }

    }

}

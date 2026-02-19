package be.envano.petclinic.specialty;

import java.util.Objects;

public interface SpecialtyCommand {

    record Load(
        Specialty.Id id,
        Specialty.Name name,
        int version
    ) {

        public Load {
			Objects.requireNonNull(id);
            Objects.requireNonNull(name);
        }

    }

    record Register(
        Specialty.Name name
    ) {

        public Register {
            Objects.requireNonNull(name, "name missing");
        }

    }

    record Rename(
        Specialty.Id id,
        Specialty.Name name,
        int version
    ) {

        public Rename {
            Objects.requireNonNull(name, "name missing");
        }

    }

}

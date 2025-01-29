package be.envano.petclinic.speciality;

import java.util.Objects;

public interface SpecialtyCommand {

    record Load(
        long id,
        Specialty.Name name,
        int version
    ) {

        public Load {
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
        long id,
        Specialty.Name name,
        int version
    ) {

        public Rename {
            Objects.requireNonNull(name, "name missing");
        }

    }

}

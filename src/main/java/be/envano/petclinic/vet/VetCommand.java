package be.envano.petclinic.vet;

import be.envano.petclinic.specialty.Specialty;

import java.util.List;
import java.util.Objects;

public interface VetCommand {

    record Rehydrate(
        Vet.Id id,
        Vet.Name name,
        List<Specialty.Id> specialties,
        int version
    ) {

        public Rehydrate {
            Objects.requireNonNull(id);
            Objects.requireNonNull(name);
            Objects.requireNonNull(specialties);
            specialties = List.copyOf(specialties);
        }

    }

    record Hire(
        Vet.Name name,
        List<Specialty.Id> specialties
    ) {

        public Hire {
            Objects.requireNonNull(name);
            Objects.requireNonNull(specialties);
            specialties = List.copyOf(specialties);
        }

    }

    record Rename(
        Vet.Id id,
        Vet.Name name,
        int version
    ) {

        public Rename {
            Objects.requireNonNull(id);
            Objects.requireNonNull(name);
        }

    }

    record Specialize(
        Vet.Id id,
        List<Specialty.Id> specialties,
        int version
    ) {

        public Specialize {
            Objects.requireNonNull(id);
            Objects.requireNonNull(specialties);
            specialties = List.copyOf(specialties);
        }

    }

    record DeSpecialize(
        Vet.Id id,
        Specialty.Id speciality,
        int version
    ) {

        public DeSpecialize {
            Objects.requireNonNull(id);
            Objects.requireNonNull(speciality);
        }

    }

    record Fire(
        Vet.Id id,
        int version
    ) {

        public Fire {
            Objects.requireNonNull(id);
        }

    }

}

package be.envano.petclinic.vet;

import be.envano.petclinic.specialty.Specialty;

import java.util.List;
import java.util.Objects;

public interface VetCommand {

    record Load(
        Vet.Id id,
        Vet.Name name,
        List<Specialty.Id> specialties,
        int version
    ) {

        public Load {
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
        List<Specialty.Id> specialities,
        int version
    ) {

        public Specialize {
            Objects.requireNonNull(id);
            Objects.requireNonNull(specialities);
            specialities = List.copyOf(specialities);
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
        Vet.Name name
    ) {

        public Fire {
            Objects.requireNonNull(id);
            Objects.requireNonNull(name);
        }

    }

}

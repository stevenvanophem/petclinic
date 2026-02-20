package be.envano.petclinic.vet;

import be.envano.petclinic.specialty.Specialty;

import java.util.List;
import java.util.Objects;

public sealed interface VetEvent {

    record Hired(Vet vet) implements VetEvent {

        public Hired {
            Objects.requireNonNull(vet);
        }

    }

    record Fired(Vet.Id id) implements VetEvent {

        public Fired {
            Objects.requireNonNull(id);
        }

    }

    record Renamed(
        Vet vet,
        Vet.Name oldName
    ) implements VetEvent {

        public Renamed {
            Objects.requireNonNull(vet);
            Objects.requireNonNull(oldName);
        }

    }

    record Specialized(
        Vet vet,
        List<Specialty.Id> originalSpecialties
    ) implements VetEvent {

        public Specialized {
            Objects.requireNonNull(vet);
            Objects.requireNonNull(originalSpecialties);
            originalSpecialties = List.copyOf(originalSpecialties);
        }

    }

    record DeSpecialized(
        Vet vet,
        Specialty.Id removedSpecialty
    ) implements VetEvent {

        public DeSpecialized {
            Objects.requireNonNull(vet);
            Objects.requireNonNull(removedSpecialty);
        }

    }

}

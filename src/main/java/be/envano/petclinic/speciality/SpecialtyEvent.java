package be.envano.petclinic.speciality;

import java.util.Objects;

public sealed interface SpecialtyEvent {

    record Registered(
        Specialty specialty
    ) implements SpecialtyEvent {

        public Registered {
            Objects.requireNonNull(specialty);
        }

    }

    record Renamed(
        Specialty specialty
    ) implements SpecialtyEvent {

        public Renamed {
            Objects.requireNonNull(specialty);
        }

    }

}

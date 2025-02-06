package be.envano.petclinic.owner;

import java.util.Objects;

public interface OwnerCommand {

    record Load(
        Owner.Id id,
        Owner.Name name,
        Owner.Address address,
        Owner.Telephone telephone,
        Owner.City city,
        int version
    ) {

        public Load {
            Objects.requireNonNull(id);
            Objects.requireNonNull(name);
            Objects.requireNonNull(address);
            Objects.requireNonNull(city);
            Objects.requireNonNull(telephone);
        }

    }

    record Register(
        Owner.Name name,
        Owner.Address address,
        Owner.Telephone telephone,
        Owner.City city
    ) {

        public Register {
            Objects.requireNonNull(name);
            Objects.requireNonNull(address);
            Objects.requireNonNull(city);
            Objects.requireNonNull(telephone);
        }

    }

    record ChangeContactDetails(
        Owner.Id id,
        Owner.Address address,
        Owner.Telephone telephone,
        Owner.City city,
        int version
    ) {

        public ChangeContactDetails {
            Objects.requireNonNull(id);
            Objects.requireNonNull(address);
            Objects.requireNonNull(city);
            Objects.requireNonNull(telephone);
        }

    }

    record Rename(
        Owner.Id id,
        Owner.Name name,
        int version
    ) {

        public Rename {
            Objects.requireNonNull(id);
            Objects.requireNonNull(name);
        }

    }

}

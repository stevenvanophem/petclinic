package be.envano.petclinic.specialty;

import java.util.Objects;

public record Specialty(
    Id id,
    Name name,
    int version
) {

    public Specialty {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
    }

    public record Id(long value) {

        public Id {
            if (value < 1)
                throw new IllegalArgumentException("specialty id must be positive");
        }

        public static Id fromLong(Long value) {
            return new Id(value);
        }

        public static Id one() {
            return new Id(1L);
        }

        public long toLong() {
            return value;
        }

    }

    public record Name(String value) {

        public Name {
            Objects.requireNonNull(value, "specialty name cannot be null");
            if (value.isBlank())
                throw new IllegalArgumentException("specialty name cannot be blank");
            if (value.length() > 80)
                throw new IllegalArgumentException("specialty name cannot be longer than 80 characters");
        }

        public static Name fromString(String value) {
            return new Name(value);
        }

        @Override
        public String toString() {
            return value;
        }

    }

}

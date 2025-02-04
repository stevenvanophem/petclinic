package be.envano.petclinic.pet;

public class Pet {

    public record Id(long value) {

        public Id {
            if (value < 1)
                throw new IllegalArgumentException("Pet id must be positive");
        }

        public static Id fromLong(long value) {
            return new Id(value);
        }

    }

}

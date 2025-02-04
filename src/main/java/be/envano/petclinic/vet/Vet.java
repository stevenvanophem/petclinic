package be.envano.petclinic.vet;

import java.util.Objects;

public class Vet {

    private long id;
    private Name name;
    private int version;

    public static Vet load(VetCommand.Load command) {
        return new Vet(command);
    }

    public Vet(VetCommand.Load command) {
        Objects.requireNonNull(command);
        this.id = command.id();
        this.name = command.name();
        this.version = command.version();
    }

    public static Vet hire(VetCommand.Hire command) {
        return new Vet(command);
    }

    private Vet(VetCommand.Hire command) {
        Objects.requireNonNull(command);
        this.name = command.name();
    }

    public void rename(VetCommand.Rename command) {
        Objects.requireNonNull(command);

        if (this.name.equals(command.name()))
            throw new IllegalArgumentException("Name cannot be the same");

        this.name = command.name();
    }

    public long id() {
        return id;
    }

    public Name name() {
        return name;
    }

    public int version() {
        return version;
    }

    public record Name(First first, Last last) {

        public Name {
            Objects.requireNonNull(first);
            Objects.requireNonNull(last);
        }

        public record First(String value) {

            public First {
                Objects.requireNonNull(value);
                if (value.isBlank())
                    throw new IllegalArgumentException("Vet first name cannot be blank.");
                if (value.length() > 30)
                    throw new IllegalArgumentException("Vet first name cannot be longer than 30 characters.");
            }

            @Override
            public String toString() {
                return value;
            }

        }

        public record Last(String value) {

            public Last {
                Objects.requireNonNull(value);
                if (value.isBlank())
                    throw new IllegalArgumentException("Vet last name cannot be blank.");
                if (value.length() > 30)
                    throw new IllegalArgumentException("Vet last name cannot be longer than 30 characters.");
            }

            @Override
            public String toString() {
                return value;
            }

        }

    }

}

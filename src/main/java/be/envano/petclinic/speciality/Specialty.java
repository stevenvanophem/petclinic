package be.envano.petclinic.speciality;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class Specialty {

    private final long id;
    private Name name;
    private int version;

    public static Specialty load(SpecialtyCommand.Load command) {
        return new Specialty(command);
    }

    private Specialty(SpecialtyCommand.Load command) {
        Objects.requireNonNull(command);
        this.id = command.id();
        this.name = command.name();
        this.version = command.version();
    }

    Specialty(SpecialtyCommand.Register command, long id) {
        Objects.requireNonNull(command);
        this.id = id;
        this.name = command.name();
    }

    Specialty rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        if (this.id != command.id())
            throw new IllegalStateException("specialty ids do not match");
        if (this.version != command.version())
            throw new IllegalStateException("specialty versions do not match");

        this.name = command.name();
        return this;
    }

    public <T> T map(Function<Specialty, T> function) {
        return function.apply(this);
    }

    public Specialty tap(Consumer<Specialty> action) {
        action.accept(this);
        return this;
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

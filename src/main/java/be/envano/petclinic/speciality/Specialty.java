package be.envano.petclinic.speciality;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Specialty {

    private final List<SpecialtyEvent> events = new ArrayList<>();

    private Id id;
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

    Specialty(SpecialtyCommand.Register command) {
        Objects.requireNonNull(command);
        this.name = command.name();
        this.events.add(new SpecialtyEvent.Registered(this));
    }

    void rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        if (this.version != command.version())
            throw new IllegalStateException("specialty versions do not match");

        this.name = command.name();
        this.events.add(new SpecialtyEvent.Renamed(this));
    }

    public Id id() {
        return id;
    }

    public Name name() {
        return name;
    }

    public int version() {
        return version;
    }

    public List<SpecialtyEvent> events() {
        return List.copyOf(events);
    }

    public record Id(long value) {

        public Id {
            if (value < 1)
                throw new IllegalArgumentException("specialty id must be positive");
        }

        public static Id fromLong(Long value) {
            return new Id(value);
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

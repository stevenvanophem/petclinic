package be.envano.petclinic.specialty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Specialty {

    private final List<SpecialtyEvent> events = new ArrayList<>();

    private final Id id;
    private Name name;
    private final int version;

    public Specialty(SpecialtyCommand.Rehydrate command) {
        Objects.requireNonNull(command);
        this.id = command.id();
        this.name = command.name();
        this.version = command.version();
    }

    public Specialty(Id id, SpecialtyCommand.Register command) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(command);

        this.id = id;
        this.name = command.name();
        this.version = 0;
        this.events.add(new SpecialtyEvent.Registered(this));
    }

    public void rename(SpecialtyCommand.Rename command) {
        Objects.requireNonNull(command);

        Name originalName = this.name;

        if (this.version != command.version())
            throw new IllegalStateException("specialty versions do not match");

        this.name = command.name();
        this.events.add(new SpecialtyEvent.Renamed(this, originalName));
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

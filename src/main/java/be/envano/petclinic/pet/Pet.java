package be.envano.petclinic.pet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pet {

    private final List<PetEvent> events = new ArrayList<>();

    private final Id id;
    private Name name;
    private final BirthDate birthDate;
    private final Type type;
    private final OwnerId ownerId;
    private final int version;

    Pet(PetCommand.Rehydrate command) {
        Objects.requireNonNull(command);
        this.id = command.id();
        this.name = command.name();
        this.birthDate = command.birthDate();
        this.type = command.type();
        this.ownerId = command.ownerId();
        this.version = command.version();
    }

    Pet(Id id, PetCommand.Register command) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(command);
        this.id = id;
        this.name = command.name();
        this.birthDate = command.birthDate();
        this.type = command.type();
        this.ownerId = command.ownerId();
        this.version = 0;
        this.events.add(new PetEvent.Registered(this));
    }

    void rename(PetCommand.Rename command) {
        Objects.requireNonNull(command);

        Name originalName = this.name;

        if (this.name.equals(command.name()))
            throw new IllegalArgumentException("Name cannot be the same");
        if (this.version != command.version())
            throw new PetException.VersionConflict();

        this.name = command.name();
        this.events.add(new PetEvent.Renamed(this, originalName));
    }

    public Id id() {
        return id;
    }

    public Name name() {
        return name;
    }

    public BirthDate birthDate() {
        return birthDate;
    }

    public Type type() {
        return type;
    }

    public OwnerId ownerId() {
        return ownerId;
    }

    public int version() {
        return version;
    }

    List<PetEvent> events() {
        return List.copyOf(events);
    }

    public record Id(long value) {

        public Id {
            if (value < 1)
                throw new IllegalArgumentException("pet id must be positive");
        }

        public static Id fromLong(long value) {
            return new Id(value);
        }

        public long toLong() {
            return value;
        }

    }

    public record Name(String value) {

        public Name {
            Objects.requireNonNull(value);
            if (value.isBlank())
                throw new IllegalArgumentException("Pet name cannot be blank.");
            if (value.length() > 30)
                throw new IllegalArgumentException("Pet name cannot be longer than 30 characters.");
        }

        public static Name fromString(String value) {
            return new Name(value);
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public record BirthDate(LocalDate value) {

        public BirthDate {
            Objects.requireNonNull(value);
        }

        public static BirthDate fromLocalDate(LocalDate value) {
            return new BirthDate(value);
        }

        @Override
        public String toString() {
            return value.toString();
        }

    }

    public record Type(String value) {

        public Type {
            Objects.requireNonNull(value);
            if (value.isBlank())
                throw new IllegalArgumentException("Pet type cannot be blank.");
            if (value.length() > 80)
                throw new IllegalArgumentException("Pet type cannot be longer than 80 characters.");
        }

        public static Type fromString(String value) {
            return new Type(value);
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public record OwnerId(long value) {

        public OwnerId {
            if (value < 1)
                throw new IllegalArgumentException("pet owner id must be positive");
        }

        public static OwnerId fromLong(long value) {
            return new OwnerId(value);
        }

        public long toLong() {
            return value;
        }

    }

}

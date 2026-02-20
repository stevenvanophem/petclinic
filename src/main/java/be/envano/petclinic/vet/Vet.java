package be.envano.petclinic.vet;

import be.envano.petclinic.specialty.Specialty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vet {

    private final List<VetEvent> events = new ArrayList<>();

    private final Id id;
    private Name name;
    private List<Specialty.Id> specialties;
    private final int version;

    Vet(VetCommand.Rehydrate command) {
        Objects.requireNonNull(command);
        this.id = command.id();
        this.name = command.name();
        this.version = command.version();
        this.specialties = List.copyOf(command.specialties());
    }

    Vet(Id id, VetCommand.Hire command) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(command);
        this.id = id;
        this.name = command.name();
        this.specialties = List.copyOf(command.specialties());
        this.version = 0;
        this.events.add(new VetEvent.Hired(this));
    }

    void rename(VetCommand.Rename command) {
        Objects.requireNonNull(command);

        Name originalName = this.name;

        if (this.name.equals(command.name()))
            throw new IllegalArgumentException("Name cannot be the same");
        if (this.version != command.version())
            throw new VetException.VersionConflict();

        this.name = command.name();
        this.events.add(new VetEvent.Renamed(this, originalName));
    }

    void specialize(VetCommand.Specialize command) {
        Objects.requireNonNull(command);

        List<Specialty.Id> original = this.specialties;

        for (Specialty.Id specialty : this.specialties) {
            if (command.specialties().contains(specialty))
                throw new IllegalArgumentException("Specialty cannot be the same");
        }
        if (this.version != command.version())
            throw new VetException.VersionConflict();

        List<Specialty.Id> newSpecialties = new ArrayList<>(this.specialties);
        newSpecialties.addAll(command.specialties());

        this.specialties = List.copyOf(newSpecialties);
        this.events.add(new VetEvent.Specialized(this, original));
    }

    void deSpecialize(VetCommand.DeSpecialize command) {
        Objects.requireNonNull(command);

        Specialty.Id specialityToRemove = command.speciality();

        if (!this.specialties.contains(specialityToRemove))
            throw new IllegalArgumentException("Specialty does not exist");
        if (this.version != command.version())
            throw new VetException.VersionConflict();

        List<Specialty.Id> specialties = new ArrayList<>(this.specialties);
        specialties.remove(specialityToRemove);

        this.specialties = List.copyOf(specialties);
        this.events.add(new VetEvent.DeSpecialized(this, specialityToRemove));
    }

    void fire(VetCommand.Fire command) {
        Objects.requireNonNull(command);

        if (this.version != command.version())
            throw new VetException.VersionConflict();

        this.events.add(new VetEvent.Fired(this.id));
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

    public List<Specialty.Id> specialties() {
        return specialties;
    }

    List<VetEvent> events() {
        return List.copyOf(events);
    }

    public record Id(long value) {

        public Id {
            if (value < 1)
                throw new IllegalArgumentException("vet id must be positive");
        }

        public static Id fromLong(long value) {
            return new Id(value);
        }

    }

    public record Name(First first, Last last) {

        public Name {
            Objects.requireNonNull(first);
            Objects.requireNonNull(last);
        }

        public static Name fromStrings(String first, String last) {
            return new Name(
                new First(first),
                new Last(last)
            );
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

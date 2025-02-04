package be.envano.petclinic.vet;

import be.envano.petclinic.speciality.Specialty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vet {

    private final List<VetEvent> events = new ArrayList<>();

    private Id id;
    private Name name;
    private List<Specialty.Id> specialties;
    private int version;

    public Vet(VetCommand.Load command) {
        Objects.requireNonNull(command);
        this.id = command.id();
        this.name = command.name();
        this.version = command.version();
        this.specialties = List.copyOf(command.specialties());
    }

    public Vet(VetCommand.Hire command) {
        Objects.requireNonNull(command);
        this.name = command.name();
        this.specialties = List.copyOf(command.specialties());
        this.events.add(new VetEvent.Hired(this));
    }

    public void rename(VetCommand.Rename command) {
        Objects.requireNonNull(command);

        Name originalName = this.name;

        if (this.name.equals(command.name()))
            throw new IllegalArgumentException("Name cannot be the same");
        if (this.version != command.version())
            throw new IllegalArgumentException("Vet versions must be the same");

        this.name = command.name();
        this.events.add(new VetEvent.Renamed(this, originalName));
    }

    public void specialize(VetCommand.Specialize command) {
        Objects.requireNonNull(command);

        List<Specialty.Id> original = this.specialties;

        for (Specialty.Id specialty : this.specialties) {
            if (command.specialities().contains(specialty))
                throw new IllegalArgumentException("Specialty cannot be the same");
        }
        if (this.version != command.version())
            throw new IllegalArgumentException("Vet versions must be the same");

        List<Specialty.Id> newSpecialties = new ArrayList<>(this.specialties);
        newSpecialties.addAll(command.specialities());

        this.specialties = List.copyOf(newSpecialties);
        this.events.add(new VetEvent.Specialized(this, original));
    }

    public void deSpecialize(VetCommand.DeSpecialize command) {
        Objects.requireNonNull(command);

        Specialty.Id specialityToRemove = command.speciality();

        if (!this.specialties.contains(specialityToRemove))
            throw new IllegalArgumentException("Specialty does not exist");
        if (this.version != command.version())
            throw new IllegalArgumentException("Vet versions must be the same");

        List<Specialty.Id> specialties = new ArrayList<>(this.specialties);
        specialties.remove(specialityToRemove);

        this.specialties = List.copyOf(specialties);
        this.events.add(new VetEvent.DeSpecialized(this, specialityToRemove));
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

    public List<VetEvent> events() {
        return events;
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

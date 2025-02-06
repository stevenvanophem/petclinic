package be.envano.petclinic.owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Owner {

    private final List<OwnerEvent> events = new ArrayList<>();

    private Id id;
    private Name name;
    private Address address;
    private City city;
    private Telephone telephone;
    private int version;

    public Owner(OwnerCommand.Load command) {
        Objects.requireNonNull(command);
        this.id = command.id();
        this.name = command.name();
        this.address = command.address();
        this.city = command.city();
        this.telephone = command.telephone();
        this.version = command.version();
    }

    public Owner(OwnerCommand.Register command) {
        Objects.requireNonNull(command);
        this.name = command.name();
        this.address = command.address();
        this.city = command.city();
        this.telephone = command.telephone();
        this.events.add(new OwnerEvent.Registered(this));
    }

    public void rename(OwnerCommand.Rename command) {
        Objects.requireNonNull(command);

        Name originalName = this.name;

        if (command.version() != this.version)
            throw new IllegalArgumentException("Versions don't match");
        if (command.name().equals(this.name))
            throw new IllegalArgumentException("Name already exists");

        this.name = command.name();
        this.events.add(new OwnerEvent.Renamed(this, originalName));
    }

    public void changeContactDetails(OwnerCommand.ChangeContactDetails command) {
        Objects.requireNonNull(command);

        final City originalCity = this.city;
        final Address originalAddress = this.address;
        final Telephone originalTelephone = this.telephone;

        if (command.version() != this.version)
            throw new IllegalArgumentException("Versions don't match");

        this.address = command.address();
        this.city = command.city();
        this.telephone = command.telephone();
        this.events.add(new OwnerEvent.ContactDetailsChanged(
            this,
            originalAddress,
            originalTelephone,
            originalCity
        ));
    }

    public List<OwnerEvent> events() {
        return events;
    }

    public Id id() {
        return id;
    }

    public Name name() {
        return name;
    }

    public Address address() {
        return address;
    }

    public City city() {
        return city;
    }

    public Telephone telephone() {
        return telephone;
    }

    public record Id(long value) {

        public Id {
            if (value < 1)
                throw new IllegalArgumentException("Value must be positive");
        }

        public static Id fromLong(long value) {
            return new Id(value);
        }

    }

    public record Name(Name.First first, Name.Last last) {

        public Name {
            Objects.requireNonNull(first);
            Objects.requireNonNull(last);
        }

        public static Name fromStrings(String first, String last) {
            return new Name(
                new Name.First(first),
                new Name.Last(last)
            );
        }

        public record First(String value) {

            public First {
                Objects.requireNonNull(value);
                if (value.isBlank())
                    throw new IllegalArgumentException("Owner first name cannot be blank.");
                if (value.length() > 30)
                    throw new IllegalArgumentException("Owner first name cannot be longer than 30 characters.");
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
                    throw new IllegalArgumentException("Owner last name cannot be blank.");
                if (value.length() > 30)
                    throw new IllegalArgumentException("Owner last name cannot be longer than 30 characters.");
            }

            @Override
            public String toString() {
                return value;
            }

        }

    }

    public record Address(String value) {

        public Address {
            Objects.requireNonNull(value);
            if (value.isBlank())
                throw new IllegalArgumentException("Owner address cannot be blank.");
            if (value.length() > 255)
                throw new IllegalArgumentException("Owner address cannot be longer than 255 characters.");
        }

        public static Address fromString(String address) {
            return new Address(address);
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public record City(String value) {

        public City {
            Objects.requireNonNull(value);
            if (value.isBlank())
                throw new IllegalArgumentException("Owner city cannot be blank.");
            if (value.length() > 80)
                throw new IllegalArgumentException("Owner city cannot be longer than 80 characters.");
        }

        public static City fromString(String city) {
            return new City(city);
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public record Telephone(String value) {

        public Telephone {
            Objects.requireNonNull(value);
            if (value.isBlank())
                throw new IllegalArgumentException("Owner telephone cannot be blank.");
            if (value.length() > 20)
                throw new IllegalArgumentException("Owner telephone cannot be longer than 20 characters.");
        }

        public static Telephone fromString(String telephone) {
            return new Telephone(telephone);
        }

        @Override
        public String toString() {
            return value;
        }

    }


}

package be.envano.petclinic.vet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import be.envano.petclinic.specialty.Specialty;

public class Vet {

	private final Id id;
	private final List<Specialty.Id> specialties;
	private Name name;

	Vet(Id id, Recruit command) {
		Objects.requireNonNull(id, "vet id missing");
		Objects.requireNonNull(command, "vet command missing");
		this.id = id;
		this.name = command.name();
		this.specialties = new ArrayList<>(command.specialties());
	}

	static Vet recruit(Id id, Recruit command) {
		return new Vet(id, command);
	}

	<T> T then(Function<Vet, T> function) {
		return function.apply(this);
	}

	public Vet specialize(Specialty.Id specialtyId) {
		Objects.requireNonNull(specialtyId, "vet specialty id missing");
		if (this.specialties.contains(specialtyId))
			throw new IllegalArgumentException("vet already has specialty");
		this.specialties.add(specialtyId);
		return this;
	}

	public Vet removeSpecialty(Specialty.Id specialtyId) {
		Objects.requireNonNull(specialtyId, "vet specialty id missing");
		if (!this.specialties.contains(specialtyId))
			throw new IllegalArgumentException("vet does not have specialty");
		this.specialties.remove(specialtyId);
		return this;
	}

	public Vet correctName(Name name) {
		Objects.requireNonNull(name, "vet name missing");
		this.name = name;
		return this;
	}

	public record Id(int id) {

		public Id {
			if (id < 0)
				throw new IllegalArgumentException("vet id negative");
		}

	}

	public record Name(FirstName firstName, LastName lastName) {

		public Name {
			Objects.requireNonNull(firstName, "vet first name missing");
			Objects.requireNonNull(lastName, "vet last name missing");
		}

		public record FirstName(String value) {

			public FirstName {
				Objects.requireNonNull(value, "vet first name missing");
				if (value.isBlank())
					throw new IllegalArgumentException("vet first name blank");
				if (value.length() > 30)
					throw new IllegalArgumentException("vet first name too long");
			}

		}

		public record LastName(String value) {

			public LastName {
				Objects.requireNonNull(value, "vet last name missing");
				if (value.isBlank())
					throw new IllegalArgumentException("vet last name blank");
				if (value.length() > 30)
					throw new IllegalArgumentException("vet last name too long");
			}

		}

	}

	public record Recruit(Name name, List<Specialty.Id> specialties) {

		public Recruit {
			Objects.requireNonNull(name, "vet name missing");
			Objects.requireNonNull(specialties, "vet specialties missing");
		}

	}

	public record Recruited(Vet vet) {}

	public static class NotFound extends RuntimeException {
	}

	public record Resign(Vet.Id id) {

		public Resign {
			Objects.requireNonNull(id, "vet id missing");
		}

	}

	public record Resigned(Vet vet) {}

	public record Specialize(Vet.Id id, Specialty.Id specialtyId) {}

	public record Specialized(Vet vet, Specialty.Id id) {}

	public record RemoteSpecialty(Vet.Id id, Specialty.Id specialtyId) {}

	public record CorrectName(Vet.Id id, Vet.Name name) {}

}

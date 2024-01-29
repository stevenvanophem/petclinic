package be.envano.petclinic.specialty;

import java.util.Objects;
import java.util.function.Function;

public class Specialty {

	private final Id id;
	private final Name name;

	private Specialty(Id id, Register command) {
		Objects.requireNonNull(command);
		this.id = id;
		this.name = command.name();
	}

	public static Specialty register(Id id, Register command) {
		return new Specialty(id, command);
	}

	public <T> T then(Function<Specialty, T> function) {
		return function.apply(this);
	}

	public record Id(int id) {

		public Id {
			if (id < 0)
				throw new IllegalArgumentException("specialty id negative");
		}

	}

	public record Name(String name) {

		public Name {
			Objects.requireNonNull(name, "specialty name missing");
			if (name.isBlank())
				throw new IllegalArgumentException("specialty name blank");
		}

	}

	public static class NotFound extends RuntimeException {}

	public record Register(Specialty.Name name) {

		public Register {
			Objects.requireNonNull(name, "specialty name missing");
		}

	}

	public record Expunge(Specialty.Id id) {}

}

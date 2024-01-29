package be.envano.petclinic.specialty;

public class Specialty {

	private final Id id;

	public Specialty(Id id) {
		this.id = id;
	}

	public record Id(int value) {

		public Id {
			if (value <= 0) {
				throw new IllegalArgumentException("Id must be positive");
			}
		}

	}

}

package be.envano.petclinic.specialty;

public class SpecialtyTestFactory {

	private SpecialtyTestFactory() {
	}

	public static class Radiology {

		public static final Specialty.Name NAME = new Specialty.Name("radiology");

	}

	public static class Surgery {

		public static final Specialty.Id ID = Specialty.Id.fromLong(2L);
		public static final Specialty.Name NAME = new Specialty.Name("surgery");

	}

	public static class Dentistry {

		public static final Specialty.Name NAME = new Specialty.Name("dentistry");

	}

}

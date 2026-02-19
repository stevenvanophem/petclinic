package be.envano.petclinic.specialty.support;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCommand;

public class SpecialtyTestFactory {

	private SpecialtyTestFactory() {
	}

	public static class Radiology {

		public static final Specialty.Name NAME = new Specialty.Name("radiology");

		public static SpecialtyCommand.Register createRegisterCommand() {
			return new SpecialtyCommand.Register(NAME);
		}

		public static Specialty load() {
			return new Specialty(Specialty.Id.fromLong(1L), NAME, 0);
		}

	}

	public static class Surgery {

		public static final Specialty.Id ID = Specialty.Id.fromLong(2L);
		public static final Specialty.Name NAME = new Specialty.Name("surgery");

		public static SpecialtyCommand.Register createRegisterCommand() {
			return new SpecialtyCommand.Register(NAME);
		}

		public static Specialty load() {
			return new Specialty(ID, NAME, 0);
		}

	}

	public static class Dentistry {

		public static final Specialty.Name NAME = new Specialty.Name("dentistry");

		public static SpecialtyCommand.Register createRegisterCommand() {
			return new SpecialtyCommand.Register(NAME);
		}

		public static Specialty load() {
			return new Specialty(Specialty.Id.fromLong(3L), NAME, 0);
		}

	}

}

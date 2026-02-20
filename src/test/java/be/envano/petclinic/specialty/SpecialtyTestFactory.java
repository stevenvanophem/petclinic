package be.envano.petclinic.specialty;

public class SpecialtyTestFactory {

	private SpecialtyTestFactory() {
	}

	public static class Radiology {

		public static final Specialty.Id ID = Specialty.Id.fromLong(1L);
		public static final Specialty.Name NAME = new Specialty.Name("radiology");
		public static SpecialtyCommand.Register createRegisterCommand() {
			return new SpecialtyCommand.Register(NAME);
		}
		public static SpecialtyCommand.Rehydrate createRehydrateCommand() {
			return createRehydrateCommand(0);
		}
		public static SpecialtyCommand.Rehydrate createRehydrateCommand(int version) {
			return new SpecialtyCommand.Rehydrate(ID, NAME, version);
		}

	}

	public static class Surgery {

		public static final Specialty.Id ID = Specialty.Id.fromLong(2L);
		public static final Specialty.Name NAME = new Specialty.Name("surgery");
		public static SpecialtyCommand.Register createRegisterCommand() {
			return new SpecialtyCommand.Register(NAME);
		}
		public static SpecialtyCommand.Rehydrate createRehydrateCommand() {
			return createRehydrateCommand(0);
		}
		public static SpecialtyCommand.Rehydrate createRehydrateCommand(int version) {
			return new SpecialtyCommand.Rehydrate(ID, NAME, version);
		}

	}

	public static class Dentistry {

		public static final Specialty.Id ID = Specialty.Id.fromLong(3L);
		public static final Specialty.Name NAME = new Specialty.Name("dentistry");
		public static SpecialtyCommand.Register createRegisterCommand() {
			return new SpecialtyCommand.Register(NAME);
		}
		public static SpecialtyCommand.Rehydrate createRehydrateCommand() {
			return createRehydrateCommand(0);
		}
		public static SpecialtyCommand.Rehydrate createRehydrateCommand(int version) {
			return new SpecialtyCommand.Rehydrate(ID, NAME, version);
		}

	}

}

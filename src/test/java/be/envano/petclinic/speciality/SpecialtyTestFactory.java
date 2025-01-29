package be.envano.petclinic.speciality;

public class SpecialtyTestFactory {

    private SpecialtyTestFactory() {
    }

    public static class Radiology {

        public static final long ID = 1L;
        public static final int VERSION = 0;
        public static final Specialty.Name NAME = new Specialty.Name("radiology");

        public static SpecialtyCommand.Load loadCommand() {
            return new SpecialtyCommand.Load(ID, NAME, VERSION);
        }

    }

    public static class Surgery {

        public static final long ID = 2L;
        public static final int VERSION = 0;
        public static final Specialty.Name NAME = new Specialty.Name("surgery");

        public static SpecialtyCommand.Load loadCommand() {
            return new SpecialtyCommand.Load(ID, NAME, VERSION);
        }

    }

    public static class Dentistry {

        public static final long ID = 3L;
        public static final int VERSION = 0;
        public static final Specialty.Name NAME = new Specialty.Name("dentistry");

        public static SpecialtyCommand.Load loadCommand() {
            return new SpecialtyCommand.Load(ID, NAME, VERSION);
        }

    }

}

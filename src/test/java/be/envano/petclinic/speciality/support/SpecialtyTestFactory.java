package be.envano.petclinic.speciality.support;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCommand;

public class SpecialtyTestFactory {

    private SpecialtyTestFactory() {
    }

    public static class Radiology {

        public static final Specialty.Name NAME = new Specialty.Name("radiology");

        public static SpecialtyCommand.Register createRegisterCommand() {
            return new SpecialtyCommand.Register(NAME);
        }

        public static Specialty load() {
            return Specialty.load(new SpecialtyCommand.Load(Specialty.Id.fromLong(1L), NAME, 0));
        }

    }

    public static class Surgery {

        public static final Specialty.Name NAME = new Specialty.Name("surgery");

        public static SpecialtyCommand.Register createRegisterCommand() {
            return new SpecialtyCommand.Register(NAME);
        }

        public static Specialty load() {
            return Specialty.load(new SpecialtyCommand.Load(Specialty.Id.fromLong(2L), NAME, 0));
        }

    }

    public static class Dentistry {

        public static final Specialty.Name NAME = new Specialty.Name("dentistry");

        public static SpecialtyCommand.Register createRegisterCommand() {
            return new SpecialtyCommand.Register(NAME);
        }

        public static Specialty load() {
            return Specialty.load(new SpecialtyCommand.Load(Specialty.Id.fromLong(3L), NAME, 0));
        }

    }

}

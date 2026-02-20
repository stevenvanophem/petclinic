package be.envano.petclinic.specialty;

public interface SpecialtyException {

    class NotFound extends RuntimeException {

        public NotFound() {
            super("Specialty was not found");
        }

    }

    class VersionConflict extends RuntimeException {

        public VersionConflict() {
            super("Specialty version does not match current state");
        }

    }

    class DuplicateName extends RuntimeException {

        public DuplicateName() {
            super("Specialty name already exists");
        }

    }

}

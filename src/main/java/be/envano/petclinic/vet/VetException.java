package be.envano.petclinic.vet;

public interface VetException {

    class NotFound extends RuntimeException {

        public NotFound() {
            super("Vet was not found");
        }

    }

    class VersionConflict extends RuntimeException {

        public VersionConflict() {
            super("Vet version does not match current state");
        }

    }

    class DuplicateName extends RuntimeException {

        public DuplicateName() {
            super("Vet name already exists");
        }

    }

}

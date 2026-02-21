package be.envano.petclinic.pet;

public interface PetException {

    class NotFound extends RuntimeException {

        public NotFound() {
            super("Pet was not found");
        }

    }

    class VersionConflict extends RuntimeException {

        public VersionConflict() {
            super("Pet version does not match current state");
        }

    }

    class DuplicateName extends RuntimeException {

        public DuplicateName() {
            super("Pet name already exists for owner");
        }

    }

}

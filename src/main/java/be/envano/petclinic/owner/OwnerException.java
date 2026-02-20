package be.envano.petclinic.owner;

public interface OwnerException {

    class NotFound extends RuntimeException {

        public NotFound() {
            super("Owner was not found");
        }

    }

    class VersionConflict extends RuntimeException {

        public VersionConflict() {
            super("Owner version does not match current state");
        }

    }

    class DuplicateName extends RuntimeException {

        public DuplicateName() {
            super("Owner name already exists");
        }

    }

}

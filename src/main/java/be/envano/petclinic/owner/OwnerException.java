package be.envano.petclinic.owner;

public interface OwnerException {

    class NotFound extends RuntimeException {

        public NotFound(Owner.Id id) {
            super("Owner with id " + id + " not found");
        }

    }

}

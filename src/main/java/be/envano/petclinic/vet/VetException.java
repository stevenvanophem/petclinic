package be.envano.petclinic.vet;

public interface VetException {

    class NotFound extends RuntimeException {

        public NotFound(Vet.Id id) {
            super("vet with id " + id + " not found");
        }

    }

}

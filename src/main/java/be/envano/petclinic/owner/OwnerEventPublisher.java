package be.envano.petclinic.owner;

public interface OwnerEventPublisher {

    void publish(OwnerEvent event);

}

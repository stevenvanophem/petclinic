package be.envano.petclinic.speciality;

public interface SpecialtyEventPublisher {

    void publish(SpecialtyEvent event);

    default void registered(Specialty specialty) {
        final var event = new SpecialtyEvent.Registered(specialty);
        this.publish(event);
    }

    default void renamed(Specialty specialty) {
        final var event = new SpecialtyEvent.Renamed(specialty);
        this.publish(event);
    }

}

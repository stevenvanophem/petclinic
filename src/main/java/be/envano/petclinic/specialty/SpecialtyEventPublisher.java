package be.envano.petclinic.specialty;

public interface SpecialtyEventPublisher {

	void publish(Object event);

	default Specialty registered(Specialty specialty) {
		final var event = new SpecialtyEvents.Registered(specialty);
		this.publish(event);
		return specialty;
	}

	default Specialty expunged(Specialty specialty) {
		final var event = new SpecialtyEvents.Expunged(specialty);
		this.publish(event);
		return specialty;
	}

}

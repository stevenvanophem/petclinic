package be.envano.petclinic.vet;

public interface VetEventPublisher {

	void publish(Object event);

	default Vet recruited(Vet vet) {
		final var event = new Vet.Recruited(vet);
		this.publish(event);
		return vet;
	}

	default Vet resigned(Vet vet) {
		final var event = new Vet.Resigned(vet);
		this.publish(event);
		return vet;
	}

	default Vet specialized(Vet vet) {
		final var event = new Vet.Specialized(vet);
		this.publish(event);
		return vet;
	}

}

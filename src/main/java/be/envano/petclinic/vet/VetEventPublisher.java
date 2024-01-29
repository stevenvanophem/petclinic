package be.envano.petclinic.vet;

import static be.envano.petclinic.vet.VetEvents.*;

import be.envano.petclinic.specialty.Specialty;

public interface VetEventPublisher {

	void publish(Object event);

	default Vet recruited(Vet vet) {
		final var event = new Recruited(vet);
		this.publish(event);
		return vet;
	}

	default Vet resigned(Vet vet) {
		final var event = new Resigned(vet);
		this.publish(event);
		return vet;
	}

	default Vet specialized(Vet vet, Specialty.Id specialtyId) {
		final var event = new Specialized(vet, specialtyId);
		this.publish(event);
		return vet;
	}

	default Vet specialtyRemoved(Vet vet, Specialty.Id specialtyId) {
		final var event = new SpecialtyRemoved(vet, specialtyId);
		this.publish(event);
		return vet;
	}

	default Vet nameCorrected(Vet vet) {
		final var event = new NameCorrected(vet);
		this.publish(event);
		return vet;
	}

}

package be.envano.petclinic.vet;

import java.util.Optional;

public interface VetRepository {

	Vet save(Vet vet);

	Optional<Vet> findById(Vet.Id id);

	Vet delete(Vet vet);

}

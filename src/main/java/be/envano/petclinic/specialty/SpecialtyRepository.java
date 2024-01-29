package be.envano.petclinic.specialty;

import java.util.Optional;

public interface SpecialtyRepository {

	Specialty save(Specialty specialty);

	Optional<Specialty> findById(Specialty.Id id);

	Specialty delete(Specialty specialty);

}

package be.envano.petclinic.speciality;

import java.util.List;
import java.util.Optional;

public interface SpecialtyRepository {

    Specialty save(Specialty specialty);

    Optional<Specialty> findById(Specialty.Id id);

    List<Specialty> findAll();

}

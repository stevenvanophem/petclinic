package be.envano.petclinic.speciality.persistence;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialityJpaRepository extends JpaRepository<SpecialtyJpaModel, Long> {

}

package be.envano.petclinic.speciality.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialityJpaRepository extends JpaRepository<SpecialtyJpaModel, Long> {

}

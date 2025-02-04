package be.envano.petclinic.vet.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VetJpaRepository extends JpaRepository<VetJpaModel, Long> {
}

package be.envano.petclinic.owner.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerJpaRepository extends JpaRepository<OwnerJpaModel, Long> {
}

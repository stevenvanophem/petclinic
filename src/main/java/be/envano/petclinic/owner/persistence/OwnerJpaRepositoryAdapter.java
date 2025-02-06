package be.envano.petclinic.owner.persistence;

import be.envano.petclinic.owner.Owner;
import be.envano.petclinic.owner.OwnerRepository;

import java.util.Optional;

public class OwnerJpaRepositoryAdapter implements OwnerRepository {

    private final OwnerJpaRepository repository;

    public OwnerJpaRepositoryAdapter(OwnerJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Owner save(Owner owner) {
        return null;
    }

    @Override
    public Optional<Owner> findById(Owner.Id id) {
        return Optional.empty();
    }

}

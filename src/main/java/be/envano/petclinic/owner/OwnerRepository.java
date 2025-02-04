package be.envano.petclinic.owner;

import java.util.Optional;

public interface OwnerRepository {

    Owner save(Owner owner);

    Optional<Owner> findById(Owner.Id id);

}

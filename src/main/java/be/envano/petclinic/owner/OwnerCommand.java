package be.envano.petclinic.owner;

import be.envano.petclinic.pet.Pet;

import java.util.List;

public interface OwnerCommand {

    record Register(
        Owner.Name name,
        Owner.Address address,
        Owner.Telephone telephone,
        Owner.City city,
        List<Pet.Id> pets
    ) {}

    record ChangeContactDetails(
        Owner.Id id,
        Owner.Address address,
        Owner.Telephone telephone,
        Owner.City city,
        int version
    ) {}

    record Rename(
        Owner.Id id,
        Owner.Name name,
        int version
    ) {}

    record AdoptPet(
        Owner.Id id,
        Pet.Id petId,
        int version
    ) {}

    record RelinquishPet(
        Owner.Id id,
        Pet.Id petId,
        int version
    ) {}

}

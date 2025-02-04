package be.envano.petclinic.owner;

import be.envano.petclinic.pet.Pet;

public sealed interface OwnerEvent {

    record Registered(Owner owner) implements OwnerEvent {}

    record ContactDetailsChanged(
        Owner owner,
        Owner.Address originalAddress,
        Owner.Telephone originalTelephone,
        Owner.City originalCity
    ) implements OwnerEvent {}

    record Renamed(
        Owner owner,
        Owner.Name originalName
    ) implements OwnerEvent {}

    record PetAdopted(
        Owner owner,
        Pet.Id adoptedPetId
    ) implements OwnerEvent {}

    record RelinquishPet(
        Owner owner,
        Pet.Id relinquishedPetId
    ) implements OwnerEvent {}

}

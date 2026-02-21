package be.envano.petclinic.pet.rest;

import be.envano.petclinic.pet.Pet;

import java.util.Objects;

final class PetResponseFactory {

    private PetResponseFactory() {
    }

    static PetRestModel.Response create(Pet pet) {
        Objects.requireNonNull(pet);
        return new PetRestModel.Response(
            pet.id().toLong(),
            pet.name().toString(),
            pet.birthDate().value(),
            pet.type().toString(),
            pet.ownerId().toLong(),
            pet.version()
        );
    }

}

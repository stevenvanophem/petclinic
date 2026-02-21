package be.envano.petclinic.pet.rest;

import be.envano.petclinic.pet.Pet;
import be.envano.petclinic.pet.PetCommand;

import java.util.Objects;

final class PetCommandFactory {

    private PetCommandFactory() {
    }

    static PetCommand.Register create(PetRestModel.PostRequest request) {
        Objects.requireNonNull(request);
        return new PetCommand.Register(
            Pet.Name.fromString(request.name()),
            Pet.BirthDate.fromLocalDate(request.birthDate()),
            Pet.Type.fromString(request.type()),
            Pet.OwnerId.fromLong(request.ownerId())
        );
    }

    static PetCommand.Rename create(PetRestModel.RenameRequest request, long id) {
        Objects.requireNonNull(request);
        return new PetCommand.Rename(
            Pet.Id.fromLong(id),
            Pet.Name.fromString(request.name()),
            request.version()
        );
    }

}

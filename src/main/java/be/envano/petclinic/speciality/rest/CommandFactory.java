package be.envano.petclinic.speciality.rest;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCommand;

final class CommandFactory {

     private CommandFactory() {
    }

    static SpecialtyCommand.Register create(RestModel.PostRequest request) {
        final var name = Specialty.Name.fromString(request.name());
        return new SpecialtyCommand.Register(name);
    }

    static SpecialtyCommand.Rename create(RestModel.RenameRequest request, long id) {
         final var name = Specialty.Name.fromString(request.name());
         return new SpecialtyCommand.Rename(id, name, request.version());
    }

}

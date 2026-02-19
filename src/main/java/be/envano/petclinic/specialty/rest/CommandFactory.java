package be.envano.petclinic.specialty.rest;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCommand;

final class CommandFactory {

    private CommandFactory() {
    }

    static SpecialtyCommand.Register create(RestModel.PostRequest request) {
        final var name = Specialty.Name.fromString(request.name());
        return new SpecialtyCommand.Register(name);
    }

    static SpecialtyCommand.Rename create(RestModel.RenameRequest request, long idAsLong) {
        final var id = Specialty.Id.fromLong(idAsLong);
        final var name = Specialty.Name.fromString(request.name());
        return new SpecialtyCommand.Rename(id, name, request.version());
    }

}

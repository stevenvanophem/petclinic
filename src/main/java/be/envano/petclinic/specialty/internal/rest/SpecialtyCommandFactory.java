package be.envano.petclinic.specialty.internal.rest;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCommand;

final class SpecialtyCommandFactory {

    private SpecialtyCommandFactory() {
    }

    static SpecialtyCommand.Register create(SpecialtyRestModel.PostRequest request) {
        final var name = Specialty.Name.fromString(request.name());
        return new SpecialtyCommand.Register(name);
    }

    static SpecialtyCommand.Rename create(SpecialtyRestModel.RenameRequest request, long idAsLong) {
        final var id = Specialty.Id.fromLong(idAsLong);
        final var name = Specialty.Name.fromString(request.name());
        return new SpecialtyCommand.Rename(id, name, request.version());
    }

}

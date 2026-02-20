package be.envano.petclinic.vet.rest;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.vet.Vet;
import be.envano.petclinic.vet.VetCommand;

import java.util.List;

final class VetCommandFactory {

    private VetCommandFactory() {
    }

    static VetCommand.Hire create(VetRestModel.PostRequest request) {
        Vet.Name name = Vet.Name.fromStrings(request.firstName(), request.lastName());
        List<Specialty.Id> specialties = request.specialtyIds().stream()
            .map(Specialty.Id::fromLong)
            .toList();
        return new VetCommand.Hire(name, specialties);
    }

    static VetCommand.Rename create(VetRestModel.RenameRequest request, long idAsLong) {
        Vet.Id id = Vet.Id.fromLong(idAsLong);
        Vet.Name name = Vet.Name.fromStrings(request.firstName(), request.lastName());
        return new VetCommand.Rename(id, name, request.version());
    }

    static VetCommand.Specialize create(VetRestModel.SpecializeRequest request, long idAsLong) {
        Vet.Id id = Vet.Id.fromLong(idAsLong);
        List<Specialty.Id> specialties = request.specialtyIds().stream()
            .map(Specialty.Id::fromLong)
            .toList();
        return new VetCommand.Specialize(id, specialties, request.version());
    }

    static VetCommand.DeSpecialize create(VetRestModel.DeSpecializeRequest request, long idAsLong, long specialtyIdAsLong) {
        Vet.Id id = Vet.Id.fromLong(idAsLong);
        Specialty.Id specialtyId = Specialty.Id.fromLong(specialtyIdAsLong);
        return new VetCommand.DeSpecialize(id, specialtyId, request.version());
    }

    static VetCommand.Fire create(VetRestModel.FireRequest request, long idAsLong) {
        Vet.Id id = Vet.Id.fromLong(idAsLong);
        return new VetCommand.Fire(id, request.version());
    }

}

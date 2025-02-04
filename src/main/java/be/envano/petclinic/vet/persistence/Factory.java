package be.envano.petclinic.vet.persistence;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.vet.Vet;
import be.envano.petclinic.vet.VetCommand;

class Factory {

    private Factory() {
    }

    static VetJpaModel create(Vet vet) {
        VetJpaModel model = new VetJpaModel();
        model.id = vet.id().value();
        model.firstName = vet.name().first().toString();
        model.lastName = vet.name().last().toString();
        model.specialtyIds = vet.specialties().stream()
            .map(Specialty.Id::value)
            .toList();
        return model;
    }

    static Vet create(VetJpaModel flushed) {
        var command = new VetCommand.Load(
            Vet.Id.fromLong(flushed.id),
            Vet.Name.fromStrings(flushed.firstName, flushed.lastName),
            flushed.specialtyIds.stream()
                .map(Specialty.Id::fromLong)
                .toList(),
            flushed.version
        );
        return new Vet(command);
    }

}

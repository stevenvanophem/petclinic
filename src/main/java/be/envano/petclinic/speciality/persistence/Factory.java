package be.envano.petclinic.speciality.persistence;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCommand;

class Factory {

    private Factory() {
    }

    static SpecialtyJpaModel create(Specialty specialty) {
        SpecialtyJpaModel model = new SpecialtyJpaModel();
        model.id = specialty.id();
        model.name = specialty.name().toString();
        return model;
    }

    static Specialty create(SpecialtyJpaModel model) {
        SpecialtyCommand.Load command = new SpecialtyCommand.Load(
            model.id,
            Specialty.Name.fromString(model.name),
            model.version
        );
        return Specialty.load(command);
    }

}

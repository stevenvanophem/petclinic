package be.envano.petclinic.vet.rest;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.vet.Vet;

final class VetResponseFactory {

    private VetResponseFactory() {
    }

    static VetRestModel.Response create(Vet vet) {
        return new VetRestModel.Response(
            vet.id().value(),
            vet.name().first().toString(),
            vet.name().last().toString(),
            vet.specialties().stream().map(Specialty.Id::value).toList(),
            vet.version()
        );
    }

    static VetRestModel.FireResponse createFireResponse() {
        return new VetRestModel.FireResponse("fired");
    }

}

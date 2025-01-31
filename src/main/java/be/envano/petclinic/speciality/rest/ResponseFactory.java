package be.envano.petclinic.speciality.rest;

import be.envano.petclinic.speciality.Specialty;

final class ResponseFactory {

    private ResponseFactory() {
    }

    static RestModel.Response create(Specialty specialty) {
        return new RestModel.Response(
            specialty.id(),
            specialty.name().toString(),
            specialty.version()
        );
    }

}

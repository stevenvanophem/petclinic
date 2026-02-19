package be.envano.petclinic.specialty.internal.rest;

import be.envano.petclinic.specialty.Specialty;

final class ResponseFactory {

    private ResponseFactory() {
    }

    static RestModel.Response create(Specialty specialty) {
        return new RestModel.Response(
            specialty.id().toLong(),
            specialty.name().toString(),
            specialty.version()
        );
    }

}

package be.envano.petclinic.specialty.internal.rest;

import be.envano.petclinic.specialty.Specialty;

final class SpecialtyResponseFactory {

    private SpecialtyResponseFactory() {
    }

    static SpecialtyRestModel.Response create(Specialty specialty) {
        return new SpecialtyRestModel.Response(
            specialty.id().toLong(),
            specialty.name().toString(),
            specialty.version()
        );
    }

}

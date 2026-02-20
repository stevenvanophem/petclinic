package be.envano.petclinic.vet.rest;

import java.util.List;

interface VetRestModel {

    record PostRequest(
        String firstName,
        String lastName,
        List<Long> specialtyIds
    ) {}

    record RenameRequest(
        String firstName,
        String lastName,
        int version
    ) {}

    record SpecializeRequest(
        List<Long> specialtyIds,
        int version
    ) {}

    record DeSpecializeRequest(
        int version
    ) {}

    record FireRequest(
        int version
    ) {}

    record Response(
        long id,
        String firstName,
        String lastName,
        List<Long> specialtyIds,
        int version
    ) {}

    record FireResponse(
        String status
    ) {}

}

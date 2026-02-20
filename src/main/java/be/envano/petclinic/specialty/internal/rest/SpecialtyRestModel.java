package be.envano.petclinic.specialty.internal.rest;

interface SpecialtyRestModel {

    record PostRequest(
        String name
    ) {}

    record RenameRequest(
        String name,
        int version
    ) {}

    record Response(
        long id,
        String name,
        int version
    ) {}

}

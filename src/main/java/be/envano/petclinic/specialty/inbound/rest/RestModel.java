package be.envano.petclinic.specialty.inbound.rest;

interface RestModel {

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

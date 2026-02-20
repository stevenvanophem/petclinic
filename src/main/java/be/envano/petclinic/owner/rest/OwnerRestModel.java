package be.envano.petclinic.owner.rest;

interface OwnerRestModel {

    record PostRequest(
        String firstName,
        String lastName,
        String address,
        String telephone,
        String city
    ) {}

    record RenameRequest(
        String firstName,
        String lastName,
        int version
    ) {}

    record ChangeContactDetailsRequest(
        String address,
        String telephone,
        String city,
        int version
    ) {}

    record Response(
        long id,
        String firstName,
        String lastName,
        String address,
        String telephone,
        String city,
        int version
    ) {}

}

package be.envano.petclinic.pet.rest;

import java.time.LocalDate;

interface PetRestModel {

    record PostRequest(
        String name,
        LocalDate birthDate,
        String type,
        long ownerId
    ) {}

    record RenameRequest(
        String name,
        int version
    ) {}

    record Response(
        long id,
        String name,
        LocalDate birthDate,
        String type,
        long ownerId,
        int version
    ) {}

}

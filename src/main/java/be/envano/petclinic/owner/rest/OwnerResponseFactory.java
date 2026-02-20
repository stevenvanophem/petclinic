package be.envano.petclinic.owner.rest;

import be.envano.petclinic.owner.Owner;

import java.util.Objects;

final class OwnerResponseFactory {

    private OwnerResponseFactory() {
    }

    static OwnerRestModel.Response create(Owner owner) {
        Objects.requireNonNull(owner);
        return new OwnerRestModel.Response(
            owner.id().toLong(),
            owner.name().first().toString(),
            owner.name().last().toString(),
            owner.address().toString(),
            owner.telephone().toString(),
            owner.city().toString(),
            owner.version()
        );
    }

}

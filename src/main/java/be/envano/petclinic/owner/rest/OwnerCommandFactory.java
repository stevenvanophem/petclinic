package be.envano.petclinic.owner.rest;

import be.envano.petclinic.owner.Owner;
import be.envano.petclinic.owner.OwnerCommand;

import java.util.Objects;

final class OwnerCommandFactory {

    private OwnerCommandFactory() {
    }

    static OwnerCommand.Register create(OwnerRestModel.PostRequest request) {
        Objects.requireNonNull(request);
        return new OwnerCommand.Register(
            Owner.Name.fromStrings(request.firstName(), request.lastName()),
            Owner.Address.fromString(request.address()),
            Owner.Telephone.fromString(request.telephone()),
            Owner.City.fromString(request.city())
        );
    }

    static OwnerCommand.Rename create(OwnerRestModel.RenameRequest request, long id) {
        Objects.requireNonNull(request);
        return new OwnerCommand.Rename(
            Owner.Id.fromLong(id),
            Owner.Name.fromStrings(request.firstName(), request.lastName()),
            request.version()
        );
    }

    static OwnerCommand.ChangeContactDetails create(OwnerRestModel.ChangeContactDetailsRequest request, long id) {
        Objects.requireNonNull(request);
        return new OwnerCommand.ChangeContactDetails(
            Owner.Id.fromLong(id),
            Owner.Address.fromString(request.address()),
            Owner.Telephone.fromString(request.telephone()),
            Owner.City.fromString(request.city()),
            request.version()
        );
    }

}

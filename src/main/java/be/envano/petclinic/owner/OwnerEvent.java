package be.envano.petclinic.owner;

public sealed interface OwnerEvent {

    record Registered(Owner owner) implements OwnerEvent {}

    record ContactDetailsChanged(
        Owner owner,
        Owner.Address originalAddress,
        Owner.Telephone originalTelephone,
        Owner.City originalCity
    ) implements OwnerEvent {}

    record Renamed(
        Owner owner,
        Owner.Name originalName
    ) implements OwnerEvent {}

}

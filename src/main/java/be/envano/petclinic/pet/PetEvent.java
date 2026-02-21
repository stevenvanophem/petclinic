package be.envano.petclinic.pet;

public sealed interface PetEvent {

    record Registered(Pet pet) implements PetEvent {}

    record Renamed(
        Pet pet,
        Pet.Name originalName
    ) implements PetEvent {}

}

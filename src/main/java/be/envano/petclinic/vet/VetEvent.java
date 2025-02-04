package be.envano.petclinic.vet;

public sealed interface VetEvent {

    record Hired(Vet vet) implements VetEvent {}

    record Fired(long id) implements VetEvent {}

    record Renamed(Vet vet, Vet.Name oldName) {}

}

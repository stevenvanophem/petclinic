package be.envano.petclinic.vet;

import be.envano.petclinic.speciality.Specialty;

import java.util.List;

public sealed interface VetEvent {

    record Hired(Vet vet) implements VetEvent {}

    record Fired(long id) implements VetEvent {}

    record Renamed(
        Vet vet,
        Vet.Name oldName
    ) implements VetEvent {}

    record Specialized(
        Vet vet,
        List<Specialty.Id> originalSpecialties
    ) implements VetEvent {}

    record DeSpecialized(
        Vet vet,
        Specialty.Id removedSpecialty
    ) implements VetEvent {}

}

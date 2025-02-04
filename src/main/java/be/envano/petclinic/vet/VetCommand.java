package be.envano.petclinic.vet;

import be.envano.petclinic.speciality.Specialty;

import java.util.List;

public interface VetCommand {

    record Load(
        Vet.Id id,
        Vet.Name name,
        List<Specialty.Id> specialties,
        int version
    ) {}

    record Hire(
        Vet.Name name,
        List<Specialty.Id> specialties
    ) {}

    record Rename(Vet.Id id, Vet.Name name, int version) {}

    record Specialize(
        Vet.Id id,
        List<Specialty.Id> specialities,
        int version
    ) {}

    record DeSpecialize(
        Vet.Id id,
        Specialty.Id speciality,
        int version
    ) {}

    record Fire(Vet.Id id, Vet.Name name) {}

}

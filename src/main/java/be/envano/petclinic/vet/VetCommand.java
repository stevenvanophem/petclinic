package be.envano.petclinic.vet;

public interface VetCommand {

    record Load(long id, Vet.Name name, int version) {}

    record Hire(Vet.Name name) {}

    record Rename(long id, Vet.Name name, int version) {}

    record Fire(long id, Vet.Name name) {}

}

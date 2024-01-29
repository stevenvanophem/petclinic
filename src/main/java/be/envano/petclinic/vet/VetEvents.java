package be.envano.petclinic.vet;

import be.envano.petclinic.specialty.Specialty;

public class VetEvents {

	public record Recruited(Vet vet) {}

	public record Resigned(Vet vet) {}

	public record Specialized(Vet vet, Specialty.Id id) {}

	public record SpecialtyRemoved(Vet vet, Specialty.Id id) {}

	public record NameCorrected(Vet vet) {}

}

package be.envano.petclinic.specialty;

public class SpecialtyEvents {

	public record Registered(Specialty specialty) {}

	public record Expunged(Specialty specialty) {}

}

package be.envano.petclinic.specialty;

import java.util.List;

public interface SpecialtyCatalog {

    Specialty register(SpecialtyCommand.Register command);

    Specialty rename(SpecialtyCommand.Rename command);

    List<Specialty> findAll();

}

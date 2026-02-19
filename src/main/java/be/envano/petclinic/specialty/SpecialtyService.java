package be.envano.petclinic.specialty;

import java.util.List;

public interface SpecialtyService {

    Specialty register(SpecialtyCommand.Register command);

    Specialty rename(SpecialtyCommand.Rename command);

    List<Specialty> findAll();

}

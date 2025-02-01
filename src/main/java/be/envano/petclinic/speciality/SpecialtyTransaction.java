package be.envano.petclinic.speciality;

import java.util.function.Supplier;

public interface SpecialtyTransaction {

    Specialty perform(Supplier<Specialty> supplier);

}

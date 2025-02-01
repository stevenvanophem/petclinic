package be.envano.petclinic.speciality.support;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyTransaction;

import java.util.function.Supplier;

public class SpecialtyTestTransaction implements SpecialtyTransaction {

    private int count;

    @Override
    public Specialty perform(Supplier<Specialty> supplier) {
        Specialty specialty = supplier.get();
        this.count = count + 1;
        return specialty;
    }

    public int count() {
        return count;
    }


}

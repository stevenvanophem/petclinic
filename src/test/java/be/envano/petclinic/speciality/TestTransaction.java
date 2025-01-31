package be.envano.petclinic.speciality;

import be.envano.petclinic.Transaction;

import java.util.function.Supplier;

public class TestTransaction implements Transaction {

    private int count;

    @Override
    public <T> T perform(Supplier<T> supplier) {
        T t = supplier.get();
        this.count = count + 1;
        return t;
    }

    public int count() {
        return count;
    }

}

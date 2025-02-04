package be.envano.petclinic.core.transaction.support;

import be.envano.petclinic.core.transaction.Transaction;

import java.util.function.Supplier;

public class TestTransaction implements Transaction {

    private int count;

    @Override
    public <T> T perform(Supplier<T> supplier) {
        T specialty = supplier.get();
        this.count = count + 1;
        return specialty;
    }

    public int count() {
        return count;
    }

}

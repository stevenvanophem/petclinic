package be.envano.petclinic.platform.transaction.support;

import be.envano.petclinic.platform.transaction.Transaction;

import java.util.function.Supplier;

public class TestTransaction implements Transaction {

    private int count;

    @Override
    public <T> T in(Supplier<T> supplier) {
        T specialty = supplier.get();
        this.count = count + 1;
        return specialty;
    }

    @Override
    public void in(Runnable runnable) {
        this.count = count + 1;
        runnable.run();
    }

    public int count() {
        return count;
    }

}

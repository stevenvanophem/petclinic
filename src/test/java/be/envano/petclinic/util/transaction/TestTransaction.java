package be.envano.petclinic.util.transaction;

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

package be.envano.petclinic.platform.transaction;

import java.util.function.Supplier;

public interface Transaction {

    <T> T in(Supplier<T> supplier);

    void in(Runnable runnable);

}

package be.envano.petclinic.util.transaction;

import java.util.function.Supplier;

public interface Transaction {

    <T> T perform(Supplier<T> supplier);

}

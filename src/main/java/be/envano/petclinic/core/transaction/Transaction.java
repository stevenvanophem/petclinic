package be.envano.petclinic.core.transaction;

import java.util.function.Supplier;

public interface Transaction {

    <T> T perform(Supplier<T> supplier);

}

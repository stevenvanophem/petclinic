package be.envano.petclinic;

import java.util.function.Supplier;

public interface Transaction {

    <T> T perform(Supplier<T> supplier);

}

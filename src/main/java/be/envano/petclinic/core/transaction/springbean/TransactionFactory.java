package be.envano.petclinic.core.transaction.springbean;

import be.envano.petclinic.core.transaction.Transaction;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

public class TransactionFactory {

    private TransactionFactory() {
    }

    public static Transaction configure(TransactionTemplate transactionTemplate) {
        return new Transaction() {
            @Override
            public <T> T in(Supplier<T> supplier) {
                return transactionTemplate.execute(tx -> supplier.get());
            }

            @Override
            public void in(Runnable runnable) {
                transactionTemplate.executeWithoutResult(tx -> runnable.run());
            }
        };
    }

}

package be.envano.petclinic.util.transaction.springbean;

import be.envano.petclinic.util.transaction.Transaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Configuration
public class TransactionConfiguration {

    @Bean
    public Transaction transaction(TransactionTemplate template) {
        return new Transaction() {
            @Override
            public <T> T perform(Supplier<T> supplier) {
                return template.execute(status -> supplier.get());
            }
        };
    }

}

package be.envano.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

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

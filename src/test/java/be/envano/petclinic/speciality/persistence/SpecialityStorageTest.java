package be.envano.petclinic.speciality.persistence;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.support.SpecialtyTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class SpecialityStorageTest {

    @Autowired
    private SpecialityJpaRepositoryAdapter storage;

    @Test
    @DisplayName("I can store a specialty")
    void testStore() {
        final Specialty surgery = SpecialtyTestFactory.Surgery.load();

        Specialty result = storage.save(surgery);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotEqualTo(0L);
        assertThat(result.name()).isEqualTo(SpecialtyTestFactory.Surgery.NAME);
        assertThat(result.version()).isEqualTo(0);
    }

    @Test
    @DisplayName("I can find a specialty by id")
    void testFindById() {
        final Specialty surgery = SpecialtyTestFactory.Surgery.load();
        final Specialty specialty = storage.save(surgery);

        Optional<Specialty> result = storage.findById(specialty.id());

        assertThat(result)
            .isPresent()
            .hasValueSatisfying(value -> {
                assertThat(value.id()).isNotEqualTo(0L);
                assertThat(value.name()).isEqualTo(SpecialtyTestFactory.Surgery.NAME);
                assertThat(value.version()).isEqualTo(0);
            });
    }

    @TestConfiguration
    static class LocalConfiguration {

        @Bean
        SpecialityJpaRepositoryAdapter specialityStorage(SpecialityJpaRepository repository) {
            return new SpecialityJpaRepositoryAdapter(repository);
        }

    }

}
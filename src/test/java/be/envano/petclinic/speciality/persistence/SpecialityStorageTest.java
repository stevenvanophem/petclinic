package be.envano.petclinic.speciality.persistence;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCommand;
import be.envano.petclinic.speciality.SpecialtyTestFactory;
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
    private SpecialityStorage storage;

    @Test
    @DisplayName("I can store a specialty")
    void testStore() {
        final SpecialtyCommand.Load surgery = SpecialtyTestFactory.Surgery.loadCommand();

        Specialty result = storage.save(Specialty.load(surgery));

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(SpecialtyTestFactory.Surgery.ID);
        assertThat(result.name()).isEqualTo(SpecialtyTestFactory.Surgery.NAME);
        assertThat(result.version()).isEqualTo(SpecialtyTestFactory.Surgery.VERSION);
    }

    @Test
    @DisplayName("I can find a specialty by id")
    void testFindById() {
        final SpecialtyCommand.Load surgery = SpecialtyTestFactory.Surgery.loadCommand();
        final Specialty specialty = storage.save(Specialty.load(surgery));

        Optional<Specialty> result = storage.findById(specialty.id());

        assertThat(result)
            .isPresent()
            .hasValueSatisfying(value -> {
                assertThat(value.id()).isEqualTo(SpecialtyTestFactory.Surgery.ID);
                assertThat(value.name()).isEqualTo(SpecialtyTestFactory.Surgery.NAME);
                assertThat(value.version()).isEqualTo(SpecialtyTestFactory.Surgery.VERSION);
            });
    }

    @TestConfiguration
    static class LocalConfiguration {

        @Bean
        SpecialityStorage specialityStorage(SpecialityJpaRepository repository) {
            return new SpecialityStorage(repository);
        }

    }

}
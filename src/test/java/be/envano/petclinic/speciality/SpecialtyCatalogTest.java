package be.envano.petclinic.speciality;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SpecialtyCatalogTest {

    private final TestTransaction transaction = new TestTransaction();
    private final SpecialtyRepository repository = new SpecialtyTestRepository();
    private final SpecialtyTestEventPublisher eventPublisher = new SpecialtyTestEventPublisher();

    private final SpecialtyCatalog catalog = new SpecialtyCatalog(
        transaction,
        repository,
        eventPublisher
    );

    @Test
    @DisplayName("I can register a new specialty to the catalog")
    void testRegister() {
        SpecialtyCommand.Register command = new SpecialtyCommand.Register(SpecialtyTestFactory.Surgery.NAME);

        Specialty result = catalog.register(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo(SpecialtyTestFactory.Surgery.NAME);
        assertThat(transaction.count()).isEqualTo(1);
        assertThat(eventPublisher.events.getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(SpecialtyEvent.Registered.class.getSimpleName());
    }

    @Test
    @DisplayName("I can rename a specialty")
    void testRename() {
        final Specialty.Name newName = Specialty.Name.fromString("sugar");

        repository.save(Specialty.load(SpecialtyTestFactory.Surgery.loadCommand()));

        final var command = new SpecialtyCommand.Rename(
            SpecialtyTestFactory.Surgery.ID,
            newName,
            SpecialtyTestFactory.Surgery.VERSION
        );

        Specialty result = catalog.rename(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(SpecialtyTestFactory.Surgery.ID);
        assertThat(result.name()).isEqualTo(newName);
        assertThat(transaction.count()).isEqualTo(1);
        assertThat(eventPublisher.events.getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(SpecialtyEvent.Renamed.class.getSimpleName());
    }

    @Test
    @DisplayName("Can't rename when the version mismatches")
    void testRenameMismatch() {
        final Specialty.Name newName = Specialty.Name.fromString("sugar");

        repository.save(Specialty.load(new SpecialtyCommand.Load(
            SpecialtyTestFactory.Surgery.ID,
            SpecialtyTestFactory.Surgery.NAME,
            5
        )));

        final var command = new SpecialtyCommand.Rename(
            SpecialtyTestFactory.Surgery.ID,
            newName,
            4
        );

        assertThatThrownBy(() -> catalog.rename(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("specialty versions do not match");
    }

    private static class SpecialtyTestRepository implements SpecialtyRepository {

        private final AtomicLong idSequence = new AtomicLong();
        private final AtomicInteger versionSequence = new AtomicInteger();

        private final List<Specialty> specialties = new ArrayList<>();

        @Override
        public Specialty save(Specialty specialty) {
            Objects.requireNonNull(specialty);
            this.specialties.add(specialty);
            SpecialtyCommand.Load command = new SpecialtyCommand.Load(
                idSequence.incrementAndGet(),
                specialty.name(),
                versionSequence.incrementAndGet()
            );
            return Specialty.load(command);
        }

        @Override
        public Optional<Specialty> findById(long id) {
            return this.specialties.stream()
                .filter(specialty -> specialty.id() == id)
                .findFirst();
        }

    }

    private static class SpecialtyTestEventPublisher implements SpecialtyEventPublisher {

        private final List<SpecialtyEvent> events = new ArrayList<>();

        @Override
        public void publish(SpecialtyEvent event) {
            this.events.add(event);
        }

    }


}
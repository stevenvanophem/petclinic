package be.envano.petclinic.speciality;

import be.envano.petclinic.core.transaction.support.TestTransaction;
import be.envano.petclinic.speciality.support.SpecialtyTestEventPublisher;
import be.envano.petclinic.speciality.support.SpecialtyTestFactory;
import be.envano.petclinic.speciality.support.SpecialtyTestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        assertThat(result.id()).isEqualTo(Specialty.Id.one());
        assertThat(result.name()).isEqualTo(SpecialtyTestFactory.Surgery.NAME);
        assertThat(transaction.count()).isEqualTo(1);
        assertThat(eventPublisher.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(SpecialtyEvent.Registered.class.getSimpleName());
    }

    @Test
    @DisplayName("I can rename a specialty")
    void testRename() {
        final Specialty.Name newName = Specialty.Name.fromString("sugar");

        Specialty stored = repository.save(SpecialtyTestFactory.Surgery.load());

        final var command = new SpecialtyCommand.Rename(
            stored.id(),
            newName,
            stored.version()
        );

        Specialty result = catalog.rename(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(stored.id());
        assertThat(result.name()).isEqualTo(newName);
        assertThat(transaction.count()).isEqualTo(1);
        assertThat(eventPublisher.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(SpecialtyEvent.Renamed.class.getSimpleName());
    }

    @Test
    @DisplayName("Can't rename when the version mismatches")
    void testRenameMismatch() {
        final Specialty.Name newName = Specialty.Name.fromString("sugar");

        Specialty stored = repository.save(Specialty.load(new SpecialtyCommand.Load(
            Specialty.Id.fromLong(1L),
            SpecialtyTestFactory.Surgery.NAME,
            1
        )));

        final var command = new SpecialtyCommand.Rename(
            stored.id(),
            newName,
            4
        );

        assertThatThrownBy(() -> catalog.rename(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("specialty versions do not match");
    }

    @Test
    @DisplayName("I can find all specialities")
    void testFindAll() {
        repository.save(SpecialtyTestFactory.Surgery.load());
        repository.save(SpecialtyTestFactory.Dentistry.load());
        repository.save(SpecialtyTestFactory.Radiology.load());

        List<Specialty> results = catalog.findAll();

        assertThat(results.size()).isEqualTo(3);
        assertThat(eventPublisher.events().size()).isEqualTo(0);
        assertThat(transaction.count()).isEqualTo(0);
    }

}
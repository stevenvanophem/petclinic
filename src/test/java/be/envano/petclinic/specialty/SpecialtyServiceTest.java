package be.envano.petclinic.specialty;

import be.envano.petclinic.platform.journal.support.TestJournal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpecialtyServiceTest {

	private final TestJournal journal = new TestJournal();
	private final SpecialtyRepository repository = mock(SpecialtyRepository.class);

    private final SpecialtyService catalog = new SpecialtyService(
        journal,
        repository
    );

    @Test
    @DisplayName("I can register a new specialty to the catalog")
    void testRegister() {
        SpecialtyCommand.Register command = new SpecialtyCommand.Register(SpecialtyTestFactory.Surgery.NAME);
        when(repository.nextId()).thenReturn(Specialty.Id.one());
        when(repository.add(org.mockito.ArgumentMatchers.any(Specialty.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Specialty result = catalog.register(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(Specialty.Id.one());
        assertThat(result.name()).isEqualTo(SpecialtyTestFactory.Surgery.NAME);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(SpecialtyEvent.Registered.class.getSimpleName());
    }

    @Test
    @DisplayName("I can rename a specialty")
    void testRename() {
        final Specialty.Name newName = Specialty.Name.fromString("sugar");
        Specialty stored = new Specialty(new SpecialtyCommand.Rehydrate(
            SpecialtyTestFactory.Surgery.ID,
            SpecialtyTestFactory.Surgery.NAME,
            0
        ));
        when(repository.findById(SpecialtyTestFactory.Surgery.ID)).thenReturn(Optional.of(stored));
        when(repository.update(org.mockito.ArgumentMatchers.any(Specialty.class)))
            .thenAnswer(invocation -> {
                Specialty aggregate = invocation.getArgument(0);
                return new Specialty(new SpecialtyCommand.Rehydrate(aggregate.id(), aggregate.name(), aggregate.version() + 1));
            });

        final var command = new SpecialtyCommand.Rename(
            stored.id(),
            newName,
            stored.version()
        );

        Specialty result = catalog.rename(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(stored.id());
        assertThat(result.name()).isEqualTo(newName);
        assertThat(result.version()).isEqualTo(1);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(SpecialtyEvent.Renamed.class.getSimpleName());
    }

    @Test
    @DisplayName("Can't rename when the version mismatches")
    void testRenameMismatch() {
        final Specialty.Name newName = Specialty.Name.fromString("sugar");
        Specialty stored = new Specialty(new SpecialtyCommand.Rehydrate(
            Specialty.Id.fromLong(1L),
            SpecialtyTestFactory.Surgery.NAME,
            1
        ));
        when(repository.findById(Specialty.Id.fromLong(1L))).thenReturn(Optional.of(stored));

        final var command = new SpecialtyCommand.Rename(
            stored.id(),
            newName,
            4
        );

        assertThatThrownBy(() -> catalog.rename(command))
            .isInstanceOf(SpecialtyException.VersionConflict.class)
            .hasMessageContaining("Specialty version does not match current state");
    }

    @Test
    @DisplayName("I can find all specialities")
    void testFindAll() {
        List<Specialty> specialties = List.of(
            new Specialty(new SpecialtyCommand.Rehydrate(
            SpecialtyTestFactory.Surgery.ID,
            SpecialtyTestFactory.Surgery.NAME,
            0
        )),
            new Specialty(new SpecialtyCommand.Rehydrate(
            Specialty.Id.fromLong(3L),
            SpecialtyTestFactory.Dentistry.NAME,
            0
        )),
            new Specialty(new SpecialtyCommand.Rehydrate(
            Specialty.Id.fromLong(1L),
            SpecialtyTestFactory.Radiology.NAME,
            0
        ))
        );
        when(repository.findAll()).thenReturn(specialties);

        List<Specialty> results = catalog.findAll();

        assertThat(results.size()).isEqualTo(3);
        assertThat(journal.events().size()).isEqualTo(0);
    }

}

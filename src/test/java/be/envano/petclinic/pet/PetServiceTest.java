package be.envano.petclinic.pet;

import be.envano.petclinic.platform.journal.support.TestJournal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PetServiceTest {

    private final TestJournal journal = new TestJournal();
    private final PetRepository repository = mock(PetRepository.class);

    private final PetService service = new PetService(
        journal,
        repository
    );

    @Test
    @DisplayName("I can register a new pet")
    void testRegister() {
        PetCommand.Register command = PetTestFactory.Leo.createRegisterCommand();
        when(repository.nextId()).thenReturn(Pet.Id.fromLong(1L));
        when(repository.add(org.mockito.ArgumentMatchers.any(Pet.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Pet result = service.register(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(Pet.Id.fromLong(1L));
        assertThat(result.name()).isEqualTo(PetTestFactory.Leo.NAME);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(PetEvent.Registered.class.getSimpleName());
    }

    @Test
    @DisplayName("I can rename a pet")
    void testRename() {
        Pet stored = new Pet(PetTestFactory.Leo.createRehydrateCommand());
        when(repository.findById(PetTestFactory.Leo.ID)).thenReturn(Optional.of(stored));
        when(repository.update(org.mockito.ArgumentMatchers.any(Pet.class)))
            .thenAnswer(invocation -> {
                Pet aggregate = invocation.getArgument(0);
                return new Pet(new PetCommand.Rehydrate(
                    aggregate.id(),
                    aggregate.name(),
                    aggregate.birthDate(),
                    aggregate.type(),
                    aggregate.ownerId(),
                    aggregate.version() + 1
                ));
            });

        PetCommand.Rename command = new PetCommand.Rename(
            stored.id(),
            PetTestFactory.Rosy.NAME,
            stored.version()
        );

        Pet result = service.rename(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(stored.id());
        assertThat(result.name()).isEqualTo(PetTestFactory.Rosy.NAME);
        assertThat(result.version()).isEqualTo(1);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(PetEvent.Renamed.class.getSimpleName());
    }

    @Test
    @DisplayName("Can't rename when the version mismatches")
    void testRenameVersionMismatch() {
        Pet stored = new Pet(PetTestFactory.Leo.createRehydrateCommand(1));
        when(repository.findById(PetTestFactory.Leo.ID)).thenReturn(Optional.of(stored));

        PetCommand.Rename command = new PetCommand.Rename(
            stored.id(),
            PetTestFactory.Rosy.NAME,
            3
        );

        assertThatThrownBy(() -> service.rename(command))
            .isInstanceOf(PetException.VersionConflict.class)
            .hasMessageContaining("Pet version does not match current state");
    }

    @Test
    @DisplayName("I can find all pets")
    void testFindAll() {
        List<Pet> pets = List.of(
            new Pet(PetTestFactory.Leo.createRehydrateCommand()),
            new Pet(PetTestFactory.Basil.createRehydrateCommand())
        );
        when(repository.findAll()).thenReturn(pets);

        List<Pet> results = service.findAll();

        assertThat(results.size()).isEqualTo(2);
        assertThat(journal.events().size()).isEqualTo(0);
    }

}

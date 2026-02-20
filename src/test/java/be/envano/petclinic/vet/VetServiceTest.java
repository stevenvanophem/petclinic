package be.envano.petclinic.vet;

import be.envano.petclinic.platform.journal.support.TestJournal;
import be.envano.petclinic.specialty.Specialty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VetServiceTest {

    private final TestJournal journal = new TestJournal();
    private final VetRepository repository = mock(VetRepository.class);

    private final VetService service = new VetService(
        journal,
        repository
    );

    @Test
    @DisplayName("I can hire a vet")
    void testHire() {
        VetCommand.Hire command = new VetCommand.Hire(VetTestFactory.JamesCarter.NAME, VetTestFactory.JamesCarter.SPECIALTIES);
        when(repository.nextId()).thenReturn(VetTestFactory.JamesCarter.ID);
        when(repository.add(any(Vet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vet result = service.hire(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(VetTestFactory.JamesCarter.ID);
        assertThat(result.name()).isEqualTo(VetTestFactory.JamesCarter.NAME);
        assertThat(result.version()).isEqualTo(0);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(VetEvent.Hired.class.getSimpleName());
    }

    @Test
    @DisplayName("I can rename a vet")
    void testRename() {
        Vet stored = new Vet(new VetCommand.Rehydrate(
            VetTestFactory.JamesCarter.ID,
            VetTestFactory.JamesCarter.NAME,
            VetTestFactory.JamesCarter.SPECIALTIES,
            0
        ));
        Vet.Name renamed = VetTestFactory.SamBaker.NAME;
        when(repository.findById(VetTestFactory.JamesCarter.ID)).thenReturn(Optional.of(stored));
        when(repository.update(any(Vet.class))).thenAnswer(invocation -> {
            Vet vet = invocation.getArgument(0);
            return new Vet(new VetCommand.Rehydrate(vet.id(), vet.name(), vet.specialties(), vet.version() + 1));
        });

        Vet result = service.rename(new VetCommand.Rename(stored.id(), renamed, stored.version()));

        assertThat(result.name()).isEqualTo(renamed);
        assertThat(result.version()).isEqualTo(1);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(VetEvent.Renamed.class.getSimpleName());
    }

    @Test
    @DisplayName("Version mismatch throws version conflict")
    void testRenameVersionConflict() {
        Vet stored = new Vet(new VetCommand.Rehydrate(
            VetTestFactory.JamesCarter.ID,
            VetTestFactory.JamesCarter.NAME,
            VetTestFactory.JamesCarter.SPECIALTIES,
            1
        ));
        when(repository.findById(VetTestFactory.JamesCarter.ID)).thenReturn(Optional.of(stored));

        VetCommand.Rename command = new VetCommand.Rename(
            stored.id(),
            VetTestFactory.SamBaker.NAME,
            0
        );

        assertThatThrownBy(() -> service.rename(command))
            .isInstanceOf(VetException.VersionConflict.class)
            .hasMessageContaining("Vet version does not match current state");
    }

    @Test
    @DisplayName("I can de-specialize a vet")
    void testDeSpecialize() {
        Vet stored = new Vet(new VetCommand.Rehydrate(
            VetTestFactory.JamesCarter.ID,
            VetTestFactory.JamesCarter.NAME,
            VetTestFactory.JamesCarter.SPECIALTIES,
            0
        ));
        Specialty.Id toRemove = VetTestFactory.JamesCarter.SPECIALTIES.getFirst();
        when(repository.findById(VetTestFactory.JamesCarter.ID)).thenReturn(Optional.of(stored));
        when(repository.update(any(Vet.class))).thenAnswer(invocation -> {
            Vet vet = invocation.getArgument(0);
            return new Vet(new VetCommand.Rehydrate(vet.id(), vet.name(), vet.specialties(), vet.version() + 1));
        });

        Vet result = service.deSpecialize(new VetCommand.DeSpecialize(stored.id(), toRemove, stored.version()));

        assertThat(result.specialties().contains(toRemove)).isFalse();
        assertThat(result.version()).isEqualTo(1);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(VetEvent.DeSpecialized.class.getSimpleName());
    }

    @Test
    @DisplayName("I can find all vets")
    void testFindAll() {
        List<Vet> vets = List.of(
            new Vet(new VetCommand.Rehydrate(
                VetTestFactory.JamesCarter.ID,
                VetTestFactory.JamesCarter.NAME,
                VetTestFactory.JamesCarter.SPECIALTIES,
                0
            )),
            new Vet(new VetCommand.Rehydrate(
                VetTestFactory.HelenLeary.ID,
                VetTestFactory.HelenLeary.NAME,
                VetTestFactory.HelenLeary.SPECIALTIES,
                1
            ))
        );
        when(repository.findAll()).thenReturn(vets);

        List<Vet> result = service.findAll();

        assertThat(result.size()).isEqualTo(2);
        assertThat(journal.events().size()).isEqualTo(0);
    }

}

package be.envano.petclinic.owner;

import be.envano.petclinic.platform.journal.support.TestJournal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OwnerServiceTest {

    private final TestJournal journal = new TestJournal();
    private final OwnerRepository repository = mock(OwnerRepository.class);

    private final OwnerService service = new OwnerService(
        journal,
        repository
    );

    @Test
    @DisplayName("I can register a new owner")
    void testRegister() {
        OwnerCommand.Register command = OwnerTestFactory.JamesCarter.createRegisterCommand();
        when(repository.nextId()).thenReturn(Owner.Id.fromLong(1L));
        when(repository.add(org.mockito.ArgumentMatchers.any(Owner.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Owner result = service.register(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(Owner.Id.fromLong(1L));
        assertThat(result.name()).isEqualTo(OwnerTestFactory.JamesCarter.NAME);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(OwnerEvent.Registered.class.getSimpleName());
    }

    @Test
    @DisplayName("I can rename an owner")
    void testRename() {
        Owner stored = new Owner(OwnerTestFactory.JamesCarter.createRehydrateCommand());
        when(repository.findById(OwnerTestFactory.JamesCarter.ID)).thenReturn(Optional.of(stored));
        when(repository.update(org.mockito.ArgumentMatchers.any(Owner.class)))
            .thenAnswer(invocation -> {
                Owner aggregate = invocation.getArgument(0);
                return new Owner(new OwnerCommand.Rehydrate(
                    aggregate.id(),
                    aggregate.name(),
                    aggregate.address(),
                    aggregate.telephone(),
                    aggregate.city(),
                    aggregate.version() + 1
                ));
            });

        OwnerCommand.Rename command = new OwnerCommand.Rename(
            stored.id(),
            OwnerTestFactory.SamBaker.NAME,
            stored.version()
        );

        Owner result = service.rename(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(stored.id());
        assertThat(result.name()).isEqualTo(OwnerTestFactory.SamBaker.NAME);
        assertThat(result.version()).isEqualTo(1);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(OwnerEvent.Renamed.class.getSimpleName());
    }

    @Test
    @DisplayName("I can change owner contact details")
    void testChangeContactDetails() {
        Owner stored = new Owner(OwnerTestFactory.JamesCarter.createRehydrateCommand());
        when(repository.findById(OwnerTestFactory.JamesCarter.ID)).thenReturn(Optional.of(stored));
        when(repository.update(org.mockito.ArgumentMatchers.any(Owner.class)))
            .thenAnswer(invocation -> {
                Owner aggregate = invocation.getArgument(0);
                return new Owner(new OwnerCommand.Rehydrate(
                    aggregate.id(),
                    aggregate.name(),
                    aggregate.address(),
                    aggregate.telephone(),
                    aggregate.city(),
                    aggregate.version() + 1
                ));
            });

        OwnerCommand.ChangeContactDetails command = new OwnerCommand.ChangeContactDetails(
            stored.id(),
            OwnerTestFactory.HelenLeary.ADDRESS,
            OwnerTestFactory.HelenLeary.TELEPHONE,
            OwnerTestFactory.HelenLeary.CITY,
            stored.version()
        );

        Owner result = service.changeContactDetails(command);

        assertThat(result).isNotNull();
        assertThat(result.address()).isEqualTo(OwnerTestFactory.HelenLeary.ADDRESS);
        assertThat(result.telephone()).isEqualTo(OwnerTestFactory.HelenLeary.TELEPHONE);
        assertThat(result.city()).isEqualTo(OwnerTestFactory.HelenLeary.CITY);
        assertThat(result.version()).isEqualTo(1);
        assertThat(journal.events().getFirst())
            .extracting(event -> event.getClass().getSimpleName())
            .isEqualTo(OwnerEvent.ContactDetailsChanged.class.getSimpleName());
    }

    @Test
    @DisplayName("Can't rename when the version mismatches")
    void testRenameVersionMismatch() {
        Owner stored = new Owner(OwnerTestFactory.JamesCarter.createRehydrateCommand(1));
        when(repository.findById(OwnerTestFactory.JamesCarter.ID)).thenReturn(Optional.of(stored));

        OwnerCommand.Rename command = new OwnerCommand.Rename(
            stored.id(),
            OwnerTestFactory.HelenLeary.NAME,
            3
        );

        assertThatThrownBy(() -> service.rename(command))
            .isInstanceOf(OwnerException.VersionConflict.class)
            .hasMessageContaining("Owner version does not match current state");
    }

    @Test
    @DisplayName("I can find all owners")
    void testFindAll() {
        List<Owner> owners = List.of(
            new Owner(OwnerTestFactory.JamesCarter.createRehydrateCommand()),
            new Owner(OwnerTestFactory.HelenLeary.createRehydrateCommand())
        );
        when(repository.findAll()).thenReturn(owners);

        List<Owner> results = service.findAll();

        assertThat(results.size()).isEqualTo(2);
        assertThat(journal.events().size()).isEqualTo(0);
    }

}

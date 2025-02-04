package be.envano.petclinic.vet;

public class VetRoster {

    private final VetRepository repository;
    private final VetEventPublisher eventPublisher;

    public VetRoster(
        VetRepository repository,
        VetEventPublisher eventPublisher
    ) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

}

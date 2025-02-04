package be.envano.petclinic.vet.persistence;

import be.envano.petclinic.vet.Vet;
import be.envano.petclinic.vet.VetRepository;

import java.util.Optional;

public class VetJpaRepositoryAdapter implements VetRepository {

    private final VetJpaRepository repository;

    public VetJpaRepositoryAdapter(VetJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vet save(Vet vet) {
        VetJpaModel model = Factory.create(vet);
        VetJpaModel flushed = repository.saveAndFlush(model);
        return Factory.create(flushed);
    }

    @Override
    public Optional<Vet> findById(Vet.Id id) {
        return repository.findById(id.value()).map(Factory::create);
    }

    @Override
    public void delete(Vet vet) {
        VetJpaModel model = Factory.create(vet);
        repository.delete(model);
    }

}

package be.envano.petclinic.speciality.persistence;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyRepository;

import java.util.List;
import java.util.Optional;

public class SpecialityJpaRepositoryAdapter implements SpecialtyRepository {

    private final SpecialityJpaRepository repository;

    public SpecialityJpaRepositoryAdapter(SpecialityJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Specialty save(Specialty specialty) {
        SpecialtyJpaModel model = Factory.create(specialty);
        SpecialtyJpaModel flushed = repository.saveAndFlush(model);
        return Factory.create(flushed);
    }

    @Override
    public Optional<Specialty> findById(Specialty.Id id) {
        return repository.findById(id.value()).map(Factory::create);
    }

    @Override
    public List<Specialty> findAll() {
        return repository.findAll().stream()
            .map(Factory::create)
            .toList();
    }

}

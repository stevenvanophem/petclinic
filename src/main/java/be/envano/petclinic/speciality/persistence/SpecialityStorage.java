package be.envano.petclinic.speciality.persistence;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpecialityStorage implements SpecialtyRepository {

    private final SpecialityJpaRepository repository;

    public SpecialityStorage(SpecialityJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Specialty save(Specialty specialty) {
        return Factory.create(specialty)
            .andThen(repository::save)
            .andThen(Factory::create);
    }

    @Override
    public Optional<Specialty> findById(long id) {
        return repository.findById(id).map(Factory::create);
    }

}

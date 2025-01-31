package be.envano.petclinic.speciality.support;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCommand;
import be.envano.petclinic.speciality.SpecialtyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SpecialtyTestRepository implements SpecialtyRepository {

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

    @Override
    public List<Specialty> findAll() {
        return this.specialties;
    }

}

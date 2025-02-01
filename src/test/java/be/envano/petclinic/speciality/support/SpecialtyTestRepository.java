package be.envano.petclinic.speciality.support;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyRepository;
import org.springframework.test.util.ReflectionTestUtils;

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
        ReflectionTestUtils.setField(specialty, "id", idSequence.incrementAndGet());
        ReflectionTestUtils.setField(specialty, "version", versionSequence.incrementAndGet());
        this.specialties.add(specialty);
        return specialty;
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

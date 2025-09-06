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
    private final List<Specialty> specialties = new ArrayList<>();

	@Override
	public Specialty.Id nextId() {
		long id = idSequence.incrementAndGet();
		return new Specialty.Id(id);
	}

	@Override
    public Specialty add(Specialty specialty) {
        Objects.requireNonNull(specialty);
        this.specialties.add(specialty);
        return specialty;
    }

	@Override
	public Specialty update(Specialty specialty) {
		Objects.requireNonNull(specialty);
		specialties.removeIf(currentSpecialty -> currentSpecialty.id() == specialty.id());
		specialties.add(specialty);
		return specialty;
	}

	@Override
    public Optional<Specialty> findById(Specialty.Id id) {
        return this.specialties.stream()
            .filter(specialty -> specialty.id() == id)
            .findFirst();
    }

    @Override
    public List<Specialty> findAll() {
        return this.specialties;
    }

}

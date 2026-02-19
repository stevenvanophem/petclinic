package be.envano.petclinic.specialty.support;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCommand;
import be.envano.petclinic.specialty.internal.SpecialtyAggregate;
import be.envano.petclinic.specialty.internal.SpecialtyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class SpecialtyTestRepository implements SpecialtyRepository {

    private final AtomicLong idSequence = new AtomicLong();
    private final List<SpecialtyAggregate> specialties = new ArrayList<>();

	@Override
	public Specialty.Id nextId() {
		long id = idSequence.incrementAndGet();
		return new Specialty.Id(id);
	}

	@Override
    public Specialty add(SpecialtyAggregate specialty) {
        Objects.requireNonNull(specialty);
        this.specialties.add(specialty);
        return specialty.toSnapshot();
    }

	@Override
	public Specialty update(SpecialtyAggregate specialty) {
		Objects.requireNonNull(specialty);
		SpecialtyCommand.Load load = new SpecialtyCommand.Load(
			specialty.id(),
			specialty.name(),
			specialty.version() + 1
		);
		SpecialtyAggregate updated = SpecialtyAggregate.load(load);
		specialties.removeIf(currentSpecialty -> currentSpecialty.id().equals(specialty.id()));
		specialties.add(updated);
		return updated.toSnapshot();
	}

	@Override
    public Optional<SpecialtyAggregate> findById(Specialty.Id id) {
        return this.specialties.stream()
            .filter(specialty -> specialty.id().equals(id))
            .findFirst();
    }

    @Override
    public List<Specialty> findAll() {
        return this.specialties.stream()
			.map(SpecialtyAggregate::toSnapshot)
			.toList();
    }

}

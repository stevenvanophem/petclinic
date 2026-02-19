package be.envano.petclinic.specialty.internal;

import be.envano.petclinic.specialty.Specialty;

import java.util.List;
import java.util.Optional;

public interface SpecialtyRepository {

	Specialty.Id nextId();

    Specialty add(SpecialtyAggregate specialty);

    Specialty update(SpecialtyAggregate specialty);

    Optional<SpecialtyAggregate> findById(Specialty.Id id);

    List<Specialty> findAll();

}

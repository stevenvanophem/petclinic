package be.envano.petclinic.speciality.persistence;

import be.envano.petclinic.speciality.Specialty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.function.Function;

@Entity
@Table(name = "specialties")
class SpecialtyJpaModel {

    @Id
    Long id;

    String name;

    @Version
    int version;

    protected SpecialtyJpaModel() {
    }

    <T> T andThen(Function<SpecialtyJpaModel, T> function) {
        return function.apply(this);
    }

}

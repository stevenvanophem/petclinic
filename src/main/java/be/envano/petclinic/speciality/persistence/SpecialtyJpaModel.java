package be.envano.petclinic.speciality.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "specialties")
class SpecialtyJpaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "specialty_seq")
    @SequenceGenerator(name = "specialty_seq", sequenceName = "specialty_sequence", allocationSize = 1)
    Long id;

    String name;

    @Version
    int version;

    protected SpecialtyJpaModel() {
    }

}

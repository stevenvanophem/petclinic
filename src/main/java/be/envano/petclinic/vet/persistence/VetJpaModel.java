package be.envano.petclinic.vet.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.List;

@Entity
@Table(name = "vets")
class VetJpaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vet_seq")
    @SequenceGenerator(name = "vet_seq", sequenceName = "vet_sequence", allocationSize = 1)
    Long id;

    String firstName;

    String lastName;

    @ElementCollection
    @CollectionTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"))
    List<Long> specialtyIds;

    @Version
    int version;

    protected VetJpaModel() {
    }

}

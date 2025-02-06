package be.envano.petclinic.owner.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "OWNERS")
public class OwnerJpaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "owner_seq")
    @SequenceGenerator(name = "owner_seq", sequenceName = "owner_sequence", allocationSize = 1)
    Long id;

    @Column(length = 30)
    String firstName;

    @Column(length = 30)
    String lastName;

    String address;

    @Column(length = 80)
    String city;

    @Column(length = 20)
    String telephone;

    @Version
    int version;

    protected OwnerJpaModel() {
    }

}

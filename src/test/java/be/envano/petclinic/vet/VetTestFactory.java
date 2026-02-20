package be.envano.petclinic.vet;

import be.envano.petclinic.specialty.Specialty;

import java.util.List;

class VetTestFactory {

    private VetTestFactory() {
    }

    static class JamesCarter {

        static final Vet.Id ID = Vet.Id.fromLong(1L);
        static final Vet.Name NAME = Vet.Name.fromStrings("James", "Carter");
        static final List<Specialty.Id> SPECIALTIES = List.of(
            Specialty.Id.fromLong(1L),
            Specialty.Id.fromLong(2L)
        );

    }

    static class HelenLeary {

        static final Vet.Id ID = Vet.Id.fromLong(2L);
        static final Vet.Name NAME = Vet.Name.fromStrings("Helen", "Leary");
        static final List<Specialty.Id> SPECIALTIES = List.of(
            Specialty.Id.fromLong(3L)
        );

    }

}

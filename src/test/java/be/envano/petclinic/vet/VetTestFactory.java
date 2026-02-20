package be.envano.petclinic.vet;

import be.envano.petclinic.specialty.Specialty;

import java.util.List;

public class VetTestFactory {

    private VetTestFactory() {
    }

    public static class JamesCarter {

        public static final Vet.Id ID = Vet.Id.fromLong(1L);
        public static final Vet.Name NAME = Vet.Name.fromStrings("James", "Carter");
        public static final String FIRST_NAME = "James";
        public static final String LAST_NAME = "Carter";
        public static final List<Specialty.Id> SPECIALTIES = List.of(
            Specialty.Id.fromLong(1L),
            Specialty.Id.fromLong(2L)
        );
        public static final long RADIOLOGY_ID = 1L;
        public static final long SURGERY_ID = 2L;
        public static final String RADIOLOGY_NAME = "radiology";
        public static final String SURGERY_NAME = "surgery";

    }

    public static class HelenLeary {

        public static final Vet.Id ID = Vet.Id.fromLong(2L);
        public static final Vet.Name NAME = Vet.Name.fromStrings("Helen", "Leary");
        public static final String FIRST_NAME = "Helen";
        public static final String LAST_NAME = "Leary";
        public static final List<Specialty.Id> SPECIALTIES = List.of(
            Specialty.Id.fromLong(3L)
        );
        public static final long DENTISTRY_ID = 3L;
        public static final String DENTISTRY_NAME = "dentistry";

    }

    public static class SamBaker {

        public static final Vet.Name NAME = Vet.Name.fromStrings("Sam", "Baker");
        public static final String FIRST_NAME = "Sam";
        public static final String LAST_NAME = "Baker";

    }

}

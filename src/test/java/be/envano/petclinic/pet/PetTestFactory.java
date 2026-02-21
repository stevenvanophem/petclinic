package be.envano.petclinic.pet;

import java.time.LocalDate;

public class PetTestFactory {

    private PetTestFactory() {
    }

    public static class Leo {

        public static final Pet.Id ID = Pet.Id.fromLong(1L);
        public static final Pet.Name NAME = Pet.Name.fromString("Leo");
        public static final String NAME_VALUE = "Leo";
        public static final Pet.BirthDate BIRTH_DATE = Pet.BirthDate.fromLocalDate(LocalDate.of(2010, 9, 7));
        public static final LocalDate BIRTH_DATE_VALUE = LocalDate.of(2010, 9, 7);
        public static final Pet.Type TYPE = Pet.Type.fromString("cat");
        public static final String TYPE_VALUE = "cat";
        public static final Pet.OwnerId OWNER_ID = Pet.OwnerId.fromLong(1L);
        public static final long OWNER_ID_VALUE = 1L;

        public static PetCommand.Register createRegisterCommand() {
            return new PetCommand.Register(NAME, BIRTH_DATE, TYPE, OWNER_ID);
        }

        public static PetCommand.Rehydrate createRehydrateCommand() {
            return createRehydrateCommand(0);
        }

        public static PetCommand.Rehydrate createRehydrateCommand(int version) {
            return new PetCommand.Rehydrate(ID, NAME, BIRTH_DATE, TYPE, OWNER_ID, version);
        }

    }

    public static class Basil {

        public static final Pet.Id ID = Pet.Id.fromLong(2L);
        public static final Pet.Name NAME = Pet.Name.fromString("Basil");
        public static final String NAME_VALUE = "Basil";
        public static final Pet.BirthDate BIRTH_DATE = Pet.BirthDate.fromLocalDate(LocalDate.of(2012, 8, 6));
        public static final LocalDate BIRTH_DATE_VALUE = LocalDate.of(2012, 8, 6);
        public static final Pet.Type TYPE = Pet.Type.fromString("hamster");
        public static final String TYPE_VALUE = "hamster";
        public static final Pet.OwnerId OWNER_ID = Pet.OwnerId.fromLong(2L);
        public static final long OWNER_ID_VALUE = 2L;

        public static PetCommand.Register createRegisterCommand() {
            return new PetCommand.Register(NAME, BIRTH_DATE, TYPE, OWNER_ID);
        }

        public static PetCommand.Rehydrate createRehydrateCommand() {
            return createRehydrateCommand(0);
        }

        public static PetCommand.Rehydrate createRehydrateCommand(int version) {
            return new PetCommand.Rehydrate(ID, NAME, BIRTH_DATE, TYPE, OWNER_ID, version);
        }

    }

    public static class Rosy {

        public static final Pet.Id ID = Pet.Id.fromLong(3L);
        public static final Pet.Name NAME = Pet.Name.fromString("Rosy");
        public static final String NAME_VALUE = "Rosy";
        public static final Pet.BirthDate BIRTH_DATE = Pet.BirthDate.fromLocalDate(LocalDate.of(2011, 4, 17));
        public static final LocalDate BIRTH_DATE_VALUE = LocalDate.of(2011, 4, 17);
        public static final Pet.Type TYPE = Pet.Type.fromString("dog");
        public static final String TYPE_VALUE = "dog";
        public static final Pet.OwnerId OWNER_ID = Pet.OwnerId.fromLong(3L);
        public static final long OWNER_ID_VALUE = 3L;

        public static PetCommand.Register createRegisterCommand() {
            return new PetCommand.Register(NAME, BIRTH_DATE, TYPE, OWNER_ID);
        }

        public static PetCommand.Rehydrate createRehydrateCommand() {
            return createRehydrateCommand(0);
        }

        public static PetCommand.Rehydrate createRehydrateCommand(int version) {
            return new PetCommand.Rehydrate(ID, NAME, BIRTH_DATE, TYPE, OWNER_ID, version);
        }

    }

}

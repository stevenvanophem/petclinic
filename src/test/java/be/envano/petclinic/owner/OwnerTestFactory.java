package be.envano.petclinic.owner;

public class OwnerTestFactory {

    private OwnerTestFactory() {
    }

    public static class GeorgeFranklin {

        public static final Owner.Id ID = Owner.Id.fromLong(1L);
        public static final Owner.Name NAME = Owner.Name.fromStrings("George", "Franklin");
        public static final String FIRST_NAME = "George";
        public static final String LAST_NAME = "Franklin";
        public static final Owner.Address ADDRESS = Owner.Address.fromString("110 W. Liberty St.");
        public static final String ADDRESS_VALUE = "110 W. Liberty St.";
        public static final Owner.Telephone TELEPHONE = Owner.Telephone.fromString("6085551023");
        public static final String TELEPHONE_VALUE = "6085551023";
        public static final Owner.City CITY = Owner.City.fromString("Madison");
        public static final String CITY_VALUE = "Madison";

        public static OwnerCommand.Register createRegisterCommand() {
            return new OwnerCommand.Register(NAME, ADDRESS, TELEPHONE, CITY);
        }

        public static OwnerCommand.Rehydrate createRehydrateCommand() {
            return createRehydrateCommand(0);
        }

        public static OwnerCommand.Rehydrate createRehydrateCommand(int version) {
            return new OwnerCommand.Rehydrate(ID, NAME, ADDRESS, TELEPHONE, CITY, version);
        }

    }

    public static class BettyDavis {

        public static final Owner.Id ID = Owner.Id.fromLong(2L);
        public static final Owner.Name NAME = Owner.Name.fromStrings("Betty", "Davis");
        public static final String FIRST_NAME = "Betty";
        public static final String LAST_NAME = "Davis";
        public static final Owner.Address ADDRESS = Owner.Address.fromString("638 Cardinal Ave.");
        public static final String ADDRESS_VALUE = "638 Cardinal Ave.";
        public static final Owner.Telephone TELEPHONE = Owner.Telephone.fromString("6085551749");
        public static final String TELEPHONE_VALUE = "6085551749";
        public static final Owner.City CITY = Owner.City.fromString("Sun Prairie");
        public static final String CITY_VALUE = "Sun Prairie";

        public static OwnerCommand.Register createRegisterCommand() {
            return new OwnerCommand.Register(NAME, ADDRESS, TELEPHONE, CITY);
        }

        public static OwnerCommand.Rehydrate createRehydrateCommand() {
            return createRehydrateCommand(0);
        }

        public static OwnerCommand.Rehydrate createRehydrateCommand(int version) {
            return new OwnerCommand.Rehydrate(ID, NAME, ADDRESS, TELEPHONE, CITY, version);
        }

    }

    public static class EduardoRodriquez {

        public static final Owner.Id ID = Owner.Id.fromLong(3L);
        public static final Owner.Name NAME = Owner.Name.fromStrings("Eduardo", "Rodriquez");
        public static final String FIRST_NAME = "Eduardo";
        public static final String LAST_NAME = "Rodriquez";
        public static final Owner.Address ADDRESS = Owner.Address.fromString("2693 Commerce St.");
        public static final String ADDRESS_VALUE = "2693 Commerce St.";
        public static final Owner.Telephone TELEPHONE = Owner.Telephone.fromString("6085558763");
        public static final String TELEPHONE_VALUE = "6085558763";
        public static final Owner.City CITY = Owner.City.fromString("McFarland");
        public static final String CITY_VALUE = "McFarland";

        public static OwnerCommand.Register createRegisterCommand() {
            return new OwnerCommand.Register(NAME, ADDRESS, TELEPHONE, CITY);
        }

        public static OwnerCommand.Rehydrate createRehydrateCommand() {
            return createRehydrateCommand(0);
        }

        public static OwnerCommand.Rehydrate createRehydrateCommand(int version) {
            return new OwnerCommand.Rehydrate(ID, NAME, ADDRESS, TELEPHONE, CITY, version);
        }

    }

}


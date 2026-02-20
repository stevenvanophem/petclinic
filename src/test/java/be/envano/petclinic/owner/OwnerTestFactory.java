package be.envano.petclinic.owner;

public class OwnerTestFactory {

    private OwnerTestFactory() {
    }

    public static class JamesCarter {

        public static final Owner.Id ID = Owner.Id.fromLong(1L);
        public static final Owner.Name NAME = Owner.Name.fromStrings("James", "Carter");
        public static final String FIRST_NAME = "James";
        public static final String LAST_NAME = "Carter";
        public static final Owner.Address ADDRESS = Owner.Address.fromString("Main Street 1");
        public static final String ADDRESS_VALUE = "Main Street 1";
        public static final Owner.Telephone TELEPHONE = Owner.Telephone.fromString("0123456789");
        public static final String TELEPHONE_VALUE = "0123456789";
        public static final Owner.City CITY = Owner.City.fromString("Brussels");
        public static final String CITY_VALUE = "Brussels";

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

    public static class HelenLeary {

        public static final Owner.Id ID = Owner.Id.fromLong(2L);
        public static final Owner.Name NAME = Owner.Name.fromStrings("Helen", "Leary");
        public static final String FIRST_NAME = "Helen";
        public static final String LAST_NAME = "Leary";
        public static final Owner.Address ADDRESS = Owner.Address.fromString("Hill Road 9");
        public static final String ADDRESS_VALUE = "Hill Road 9";
        public static final Owner.Telephone TELEPHONE = Owner.Telephone.fromString("0987654321");
        public static final String TELEPHONE_VALUE = "0987654321";
        public static final Owner.City CITY = Owner.City.fromString("Ghent");
        public static final String CITY_VALUE = "Ghent";

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

    public static class SamBaker {

        public static final Owner.Name NAME = Owner.Name.fromStrings("Sam", "Baker");
        public static final String FIRST_NAME = "Sam";
        public static final String LAST_NAME = "Baker";
        public static final Owner.Address ADDRESS = Owner.Address.fromString("Park Lane 7");
        public static final String ADDRESS_VALUE = "Park Lane 7";
        public static final Owner.Telephone TELEPHONE = Owner.Telephone.fromString("0111222333");
        public static final String TELEPHONE_VALUE = "0111222333";
        public static final Owner.City CITY = Owner.City.fromString("Antwerp");
        public static final String CITY_VALUE = "Antwerp";

    }

}

package Tools;

public class Constants {
    public static final int maxCustomerNameLength = 15;
    public static final int maxCustomerSurnameLength = 20;
    public static final int maxCustomerServiceVisitsCount = 5;
    public static final int maxLicensePlateCodeLength = 10;
    public static final int vehicleSize =
            // maxVisits (5) * serviceVisitSize
            Constants.maxCustomerServiceVisitsCount * Constants.serviceVisitSize +
            // id (1 int)
            Integer.BYTES +
            // maxName (15 characters) + maxSurname (20 characters) + maxLicensePlate (10 characters)
            (Constants.maxCustomerNameLength + Constants.maxCustomerSurnameLength + Constants.maxLicensePlateCodeLength) * Byte.BYTES +
            // nameLength (1 byte), surnameLength (1 byte), licensePlateLength (1 byte), serviceVisitsCount (1 byte)
            4 * Byte.BYTES;
    public static final int maxServiceVisitDescLength = 20;
    public static final int serviceVisitSize =
            // maxDesc (20 characters)
            (Constants.maxServiceVisitDescLength) * Byte.BYTES +
            // descLength (1 byte)
            Byte.BYTES +
            // date (1 int)
            Integer.BYTES +
            // price (1 double)
            Double.BYTES;

    // maxVisits (5) * serviceVisitSize, id (1 int), maxName (15 bytes), maxSurname (20 bytes), nameLength (1 byte), surnameLength (1 byte), serviceVisitsCount (1 byte)
    public static final int customerSize =
            Constants.maxCustomerServiceVisitsCount * Constants.serviceVisitSize +
                    Integer.BYTES +
                    (Constants.maxCustomerNameLength + Constants.maxCustomerSurnameLength + 1 + 1 + 1) * Byte.BYTES;

    public static final int vehicleByCustomerIDSize =
            // blockAddress (1 int), customerID (1 int)
            2 * Integer.BYTES;

    public static final int vehicleByLicensePlateSize =
            // blockAddress (1 int)
            Integer.BYTES +
            // maxLicensePlate (10 characters), licensePlateLength (1 byte)
            (Constants.maxLicensePlateCodeLength + 1) * Byte.BYTES;

    public static final int integerBits = 32;
}

package Tools;

public class Constants {
    public static final int maxCustomerNameLength = 15;
    public static final int maxCustomerSurnameLength = 20;
    public static final int maxCustomerServiceVisitsCount = 5;
    // maxVisits (5) * serviceVisitSize, id (1 int), maxName (15 bytes), maxSurname (20 bytes), nameLength (1 byte), surnameLength (1 byte), serviceVisitsCount (1 byte)
    public static final int customerSize =
            Constants.maxCustomerServiceVisitsCount * Constants.serviceVisitSize +
            Integer.BYTES +
            (Constants.maxCustomerNameLength + Constants.maxCustomerSurnameLength + 1 + 1 + 1) * Byte.BYTES;

    public static final int maxServiceVisitDescLength = 20;
    // maxDesc (20 bytes), descLength (1 byte), date (1 int), id (1 int), price (1 double)
    public static final int serviceVisitSize =
            (Constants.maxServiceVisitDescLength + 1) * Byte.BYTES +
            (1 + 1) * Integer.BYTES + Double.BYTES;
}

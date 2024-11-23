package Model;

import HeapFile.IData;
import Tools.Constants;
import Tools.StringProcessing;

import java.io.*;

public class Customer implements IData<Customer> {
    private ServiceVisit[] serviceVisits = new ServiceVisit[5];
    private String name;
    private String surname;
    private int ID;
    private byte nameLength;
    private byte surnameLength;
    private byte serviceVisitsCount;

    public Customer(int ID, String name, String surname, ServiceVisit[] serviceVisits) {
        this.ID = ID;

        this.name = StringProcessing.initStringAttribute(name, Constants.maxCustomerNameLength);
        this.nameLength = (byte) this.name.length();

        this.surname = StringProcessing.initStringAttribute(surname, Constants.maxCustomerSurnameLength);
        this.surnameLength = (byte) this.surname.length();

        for (int i = 0; i < Constants.maxCustomerServiceVisitsCount; i++) {
            if (serviceVisits != null && i < serviceVisits.length) {
                this.serviceVisits[i] = serviceVisits[i];
            } else {
                this.serviceVisits[i] = new ServiceVisit(0, 0, "");
            }
        }

        if (serviceVisits == null) {
            this.serviceVisitsCount = 0;
        } else if (serviceVisits.length >= Constants.maxCustomerServiceVisitsCount) {
            this.serviceVisitsCount = Constants.maxCustomerServiceVisitsCount;
        } else {
            this.serviceVisitsCount = (byte) serviceVisits.length;
        }
    }

    @Override
    public Customer createClass() {
        return new Customer(0, "", "", null);
    }

    @Override
    public boolean isEqualTo(Customer other) {
        return this.ID == other.ID;
    }

    @Override
    public int getSize() {
        // maxVisits (5) * serviceVisitSize, id (1 int), maxName (15 bytes), maxSurname (20 bytes), nameLength (1 byte), surnameLength (1 byte), serviceVisitsCount (1 byte)
        return Constants.customerSize;
    }

    @Override
    public byte[] getByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteArrayOutputStream);

        try {
            for (ServiceVisit serviceVisit : this.serviceVisits) {
                outStream.write(serviceVisit.getByteArray());
            }

            outStream.writeByte(this.nameLength);
            outStream.writeByte(this.surnameLength);
            outStream.writeByte(this.serviceVisitsCount);

            byte[] nameBytes = StringProcessing.stringAttributeToByteArray(this.name, Constants.maxCustomerNameLength, this.nameLength);
            // zapis pole bajtov o fixnej dlzke (max dlzke mena)
            outStream.write(nameBytes);

            byte[] surnameBytes = StringProcessing.stringAttributeToByteArray(this.surname, Constants.maxCustomerSurnameLength, this.surnameLength);
            // zapis pole bajtov o fixnej dlzke (max dlzke priezviska)
            outStream.write(surnameBytes);

            outStream.writeInt(this.ID);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array!");
        }
    }

    @Override
    public void fromByteArray(byte[] byteArray) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        DataInputStream inStream = new DataInputStream(byteArrayInputStream);

        try {
            for (int i = 0; i < this.serviceVisits.length; i++) {
                byte[] byteArrayServiceVisit = new byte[Constants.serviceVisitSize];
                System.arraycopy(byteArray, i * Constants.serviceVisitSize, byteArrayServiceVisit, 0, Constants.serviceVisitSize);
                serviceVisits[i].fromByteArray(byteArrayServiceVisit);
            }

            inStream.skipBytes(Constants.maxCustomerServiceVisitsCount * Constants.serviceVisitSize);

            this.nameLength = inStream.readByte();
            this.surnameLength = inStream.readByte();
            this.serviceVisitsCount = inStream.readByte();

            byte[] nameBytes = new byte[Constants.maxCustomerNameLength];
            inStream.readFully(nameBytes);
            this.name = StringProcessing.byteArrayToStringAttribute(nameBytes, this.nameLength);

            byte[] surnameBytes = new byte[Constants.maxCustomerSurnameLength];
            inStream.readFully(surnameBytes);
            this.surname = StringProcessing.byteArrayToStringAttribute(surnameBytes, this.surnameLength);

            this.ID = inStream.readInt();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array!");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ServiceVisit serviceVisit : serviceVisits) {
            sb.append("\n\t").append(serviceVisit);
        }

        return "Customer{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", ID=" + ID +
                ", nameLength=" + nameLength +
                ", surnameLength=" + surnameLength +
                ", serviceVisitsCount=" + serviceVisitsCount +
                "," + sb +
                '}';
    }
}

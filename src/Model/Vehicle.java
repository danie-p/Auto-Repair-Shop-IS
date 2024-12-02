package Model;

import ExtendibleHashFile.IHashData;
import FileDataStructure.IData;
import Tools.BitSetUtility;
import Tools.Constants;
import Tools.StringProcessor;

import java.io.*;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;

public class Vehicle implements IHashData<Vehicle> {
    private final ServiceVisit[] serviceVisits = new ServiceVisit[Constants.maxCustomerServiceVisitsCount];
    private String customerName;
    private String customerSurname;
    private String licensePlateCode;
    private int customerID;
    private byte customerNameLength;
    private byte customerSurnameLength;
    private byte licensePlateCodeLength;
    private byte serviceVisitsCount;

    public Vehicle(String customerName, String customerSurname, int customerID, String licensePlateCode, ServiceVisit[] serviceVisits) {
        this.customerID = customerID;

        this.customerName = StringProcessor.initStringAttribute(customerName, Constants.maxCustomerNameLength);
        this.customerNameLength = (byte) this.customerName.length();

        this.customerSurname = StringProcessor.initStringAttribute(customerSurname, Constants.maxCustomerSurnameLength);
        this.customerSurnameLength = (byte) this.customerSurname.length();

        this.licensePlateCode = StringProcessor.initStringAttribute(licensePlateCode, Constants.maxLicensePlateCodeLength);
        this.licensePlateCodeLength = (byte) this.licensePlateCode.length();

        for (int i = 0; i < Constants.maxCustomerServiceVisitsCount; i++) {
            if (serviceVisits != null && i < serviceVisits.length) {
                this.serviceVisits[i] = serviceVisits[i];
            } else {
                this.serviceVisits[i] = new ServiceVisit(0, 0, null);
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

    /**
     * @param serviceVisit pridávaná návšteva setvisu
     * @return návštevu servisu ne/bolo možné pridať (vracia false, ak je u daného vozidla už zapísaný maximálny počet návštev servisu)
     */
    public boolean addServiceVisit(ServiceVisit serviceVisit) {
        if (this.serviceVisitsCount == Constants.maxCustomerServiceVisitsCount)
            return false;

        this.serviceVisits[this.serviceVisitsCount] = serviceVisit;
        this.serviceVisitsCount++;

        return true;
    }

    @Override
    public Vehicle createClass() {
        return new Vehicle("", "", 0, "", null);
    }

    @Override
    public boolean isEqualTo(Vehicle other) {
        // postacuje, aby sa rovnal jeden z unikatnych klucov
        return this.customerID == other.customerID || this.licensePlateCode.equals(other.licensePlateCode);
    }

    @Override
    public int getSize() {
        return Constants.vehicleSize;
    }

    @Override
    public byte[] getByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteArrayOutputStream);

        try {
            for (ServiceVisit serviceVisit : this.serviceVisits) {
                outStream.write(serviceVisit.getByteArray());
            }

            outStream.writeByte(this.customerNameLength);
            outStream.writeByte(this.customerSurnameLength);
            outStream.writeByte(this.licensePlateCodeLength);
            outStream.writeByte(this.serviceVisitsCount);

            byte[] customerNameBytes = StringProcessor.stringAttributeToByteArray(this.customerName, Constants.maxCustomerNameLength, this.customerNameLength);
            // zapis pole bajtov o fixnej dlzke (max dlzke mena)
            outStream.write(customerNameBytes);

            byte[] customerSurnameBytes = StringProcessor.stringAttributeToByteArray(this.customerSurname, Constants.maxCustomerSurnameLength, this.customerSurnameLength);
            // zapis pole bajtov o fixnej dlzke (max dlzke priezviska)
            outStream.write(customerSurnameBytes);

            byte[] licensePlateCodeBytes = StringProcessor.stringAttributeToByteArray(this.licensePlateCode, Constants.maxLicensePlateCodeLength, this.licensePlateCodeLength);
            // zapis pole bajtov o fixnej dlzke (max dlzke ECV)
            outStream.write(licensePlateCodeBytes);

            outStream.writeInt(this.customerID);

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

            this.customerNameLength = inStream.readByte();
            this.customerSurnameLength = inStream.readByte();
            this.licensePlateCodeLength = inStream.readByte();
            this.serviceVisitsCount = inStream.readByte();

            byte[] customerNameBytes = new byte[Constants.maxCustomerNameLength];
            inStream.readFully(customerNameBytes);
            this.customerName = StringProcessor.byteArrayToStringAttribute(customerNameBytes, this.customerNameLength);

            byte[] customerSurnameBytes = new byte[Constants.maxCustomerSurnameLength];
            inStream.readFully(customerSurnameBytes);
            this.customerSurname = StringProcessor.byteArrayToStringAttribute(customerSurnameBytes, this.customerSurnameLength);

            byte[] licensePlateCodeBytes = new byte[Constants.maxLicensePlateCodeLength];
            inStream.readFully(licensePlateCodeBytes);
            this.licensePlateCode = StringProcessor.byteArrayToStringAttribute(licensePlateCodeBytes, this.licensePlateCodeLength);

            this.customerID = inStream.readInt();
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

        return "Vehicle {" +
                "customerName='" + customerName + '\'' +
                ", customerSurname='" + customerSurname + '\'' +
                ", customerID=" + customerID +
                ", licensePlateCode='" + licensePlateCode + '\'' +
                "," + sb +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle vehicle)) return false;
        return customerID == vehicle.customerID &&
                customerNameLength == vehicle.customerNameLength &&
                customerSurnameLength == vehicle.customerSurnameLength &&
                licensePlateCodeLength == vehicle.licensePlateCodeLength &&
                serviceVisitsCount == vehicle.serviceVisitsCount &&
                Arrays.equals(serviceVisits, vehicle.serviceVisits) &&
                customerName.equals(vehicle.customerName) &&
                customerSurname.equals(vehicle.customerSurname) &&
                licensePlateCode.equals(vehicle.licensePlateCode);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(customerName, customerSurname, licensePlateCode, customerID, customerNameLength, customerSurnameLength, licensePlateCodeLength, serviceVisitsCount);
        result = 31 * result + Arrays.hashCode(serviceVisits);
        return result;
    }

    public String getLicensePlateCode() {
        return licensePlateCode;
    }

    public int getCustomerID() {
        return customerID;
    }

    @Override
    public BitSet getHash() {
        return BitSetUtility.intToBitSet(this.customerID);
    }
}

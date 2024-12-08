package Model;

import ExtendibleHashFile.IHashData;
import Tools.BitSetUtility;
import Tools.Constants;
import Tools.StringProcessor;

import java.io.*;
import java.util.BitSet;

public class VehicleByLicensePlate implements IHashData<VehicleByLicensePlate> {
    // adresa bloku, v ktorom je dane vozidlo ulozene v heap file
    private int blockAddress;
    // klucovy atribut
    private String keyLicensePlateCode;
    private byte licensePlateCodeLength;
    private int customerID;

    public VehicleByLicensePlate(int blockAddress, String keyLicensePlateCode, int customerID) {
        this.blockAddress = blockAddress;
        this.keyLicensePlateCode = StringProcessor.initStringAttribute(keyLicensePlateCode, Constants.maxLicensePlateCodeLength);
        this.licensePlateCodeLength = (byte) this.keyLicensePlateCode.length();
        this.customerID = customerID;
    }

    @Override
    public BitSet getHash() {
        return BitSetUtility.strToBitSet(this.keyLicensePlateCode);
    }

    @Override
    public VehicleByLicensePlate createClass() {
        return new VehicleByLicensePlate(-1, "", -1);
    }

    @Override
    public boolean isEqualTo(VehicleByLicensePlate other) {
        return this.keyLicensePlateCode.equals(other.keyLicensePlateCode);
    }

    @Override
    public int getSize() {
        return Constants.vehicleByLicensePlateSize;
    }

    @Override
    public byte[] getByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteArrayOutputStream);

        try {
            outStream.writeInt(this.blockAddress);
            outStream.writeInt(this.customerID);
            outStream.writeByte(this.licensePlateCodeLength);

            byte[] licensePlateCodeBytes = StringProcessor.stringAttributeToByteArray(this.keyLicensePlateCode, Constants.maxLicensePlateCodeLength, this.licensePlateCodeLength);
            // zapis pole bajtov o fixnej dlzke (max dlzke ECV)
            outStream.write(licensePlateCodeBytes);

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
            this.blockAddress = inStream.readInt();
            this.customerID = inStream.readInt();
            this.licensePlateCodeLength = inStream.readByte();

            byte[] licensePlateCodeBytes = new byte[Constants.maxLicensePlateCodeLength];
            inStream.readFully(licensePlateCodeBytes);
            this.keyLicensePlateCode = StringProcessor.byteArrayToStringAttribute(licensePlateCodeBytes, this.licensePlateCodeLength);
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array!");
        }
    }

    @Override
    public String toString() {
        return "VehicleByLicensePlate{" +
                "blockAddress=" + blockAddress +
                ", licensePlateCode='" + keyLicensePlateCode + '\'' +
                '}';
    }

    public int getBlockAddress() {
        return blockAddress;
    }

    public String getKeyLicensePlateCode() {
        return keyLicensePlateCode;
    }

    public int getCustomerID() {
        return customerID;
    }
}

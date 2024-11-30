package Model;

import ExtendibleHashFile.IHashData;
import Tools.Constants;
import Tools.StringProcessor;

import java.io.*;
import java.util.BitSet;

public class VehicleByLicensePlate implements IHashData<VehicleByLicensePlate> {
    // adresa bloku, v ktorom je dane vozidlo ulozene v heap file
    private int blockAddress;
    // klucovy atribut
    private String licensePlateCode;
    private byte licensePlateCodeLength;

    public VehicleByLicensePlate(int blockAddress, String licensePlateCode) {
        this.blockAddress = blockAddress;
        this.licensePlateCode = StringProcessor.initStringAttribute(licensePlateCode, Constants.maxLicensePlateCodeLength);
        this.licensePlateCodeLength = (byte) this.licensePlateCode.length();
    }

    @Override
    public BitSet getHash() {
        // TODO: implement hash function for string license plate
        return null;
    }

    @Override
    public VehicleByLicensePlate createClass() {
        return new VehicleByLicensePlate(-1, "");
    }

    @Override
    public boolean isEqualTo(VehicleByLicensePlate other) {
        return this.licensePlateCode.equals(other.licensePlateCode);
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
            outStream.writeByte(this.licensePlateCodeLength);

            byte[] licensePlateCodeBytes = StringProcessor.stringAttributeToByteArray(this.licensePlateCode, Constants.maxLicensePlateCodeLength, this.licensePlateCodeLength);
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
            this.licensePlateCodeLength = inStream.readByte();

            byte[] licensePlateCodeBytes = new byte[Constants.maxLicensePlateCodeLength];
            inStream.readFully(licensePlateCodeBytes);
            this.licensePlateCode = StringProcessor.byteArrayToStringAttribute(licensePlateCodeBytes, this.licensePlateCodeLength);
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array!");
        }
    }

    @Override
    public String toString() {
        return "VehicleByLicensePlate{" +
                "blockAddress=" + blockAddress +
                ", licensePlateCode='" + licensePlateCode + '\'' +
                '}';
    }
}

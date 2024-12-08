package Model;

import ExtendibleHashFile.IHashData;
import Tools.BitSetUtility;
import Tools.Constants;
import Tools.StringProcessor;

import java.io.*;
import java.util.BitSet;

public class VehicleByCustomerID implements IHashData<VehicleByCustomerID> {
    // adresa bloku, v ktorom je dane vozidlo ulozene v heap file
    private int blockAddress;
    // klucovy atribut
    private int keyCustomerID;
    private String licensePlateCode;
    private byte licensePlateCodeLength;

    public VehicleByCustomerID(int blockAddress, int keyCustomerID, String licensePlateCode) {
        this.blockAddress = blockAddress;
        this.keyCustomerID = keyCustomerID;
        this.licensePlateCode = StringProcessor.initStringAttribute(licensePlateCode, Constants.maxLicensePlateCodeLength);
        this.licensePlateCodeLength = (byte) this.licensePlateCode.length();
    }

    @Override
    public BitSet getHash() {
        return BitSetUtility.intToBitSet(this.keyCustomerID);
    }

    @Override
    public VehicleByCustomerID createClass() {
        return new VehicleByCustomerID(-1, -1, "");
    }

    @Override
    public boolean isEqualTo(VehicleByCustomerID other) {
        return this.keyCustomerID == other.keyCustomerID;
    }

    @Override
    public int getSize() {
        return Constants.vehicleByCustomerIDSize;
    }

    @Override
    public byte[] getByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteArrayOutputStream);

        try {
            outStream.writeInt(this.blockAddress);
            outStream.writeInt(this.keyCustomerID);
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
            this.keyCustomerID = inStream.readInt();

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
        return "VehicleByCustomerID{" +
                "blockAddress=" + blockAddress +
                ", customerID=" + keyCustomerID +
                '}';
    }

    public int getBlockAddress() {
        return blockAddress;
    }

    public int getKeyCustomerID() {
        return keyCustomerID;
    }

    public String getLicensePlateCode() {
        return licensePlateCode;
    }
}

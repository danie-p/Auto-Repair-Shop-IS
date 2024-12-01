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
    private int customerID;

    public VehicleByCustomerID(int blockAddress, int customerID) {
        this.blockAddress = blockAddress;
        this.customerID = customerID;
    }

    @Override
    public BitSet getHash() {
        return BitSetUtility.intToBitSet(this.customerID);
    }

    @Override
    public VehicleByCustomerID createClass() {
        return new VehicleByCustomerID(-1, -1);
    }

    @Override
    public boolean isEqualTo(VehicleByCustomerID other) {
        return this.customerID == other.customerID;
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
            this.blockAddress = inStream.readInt();
            this.customerID = inStream.readInt();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array!");
        }
    }

    @Override
    public String toString() {
        return "VehicleByCustomerID{" +
                "blockAddress=" + blockAddress +
                ", customerID=" + customerID +
                '}';
    }

    public int getBlockAddress() {
        return blockAddress;
    }

    public void setBlockAddress(int blockAddress) {
        this.blockAddress = blockAddress;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
}

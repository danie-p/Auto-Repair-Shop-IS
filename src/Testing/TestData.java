package Testing;

import ExtendibleHashFile.IHashData;
import Tools.BitSetUtility;
import Tools.Constants;
import Tools.StringProcessor;

import java.io.*;
import java.util.BitSet;

public class TestData implements IHashData<TestData> {
    private String name;
    private byte nameLength;
    private int id;

    public TestData(String name, int id) {
        this.name = StringProcessor.initStringAttribute(name, 10);
        this.nameLength = (byte) this.name.length();
        this.id = id;
    }

    @Override
    public BitSet getHash() {
        return BitSetUtility.intToBitSet(id);
    }

    @Override
    public TestData createClass() {
        return new TestData("", -1);
    }

    @Override
    public boolean isEqualTo(TestData other) {
        return this.id == other.id;
    }

    @Override
    public int getSize() {
        return (10 + 1) * Byte.BYTES + Integer.BYTES;
    }

    @Override
    public byte[] getByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteArrayOutputStream);

        try {
            outStream.writeInt(this.id);
            outStream.writeByte(this.nameLength);

            byte[] nameBytes = StringProcessor.stringAttributeToByteArray(this.name, 10, this.nameLength);
            outStream.write(nameBytes);

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
            this.id = inStream.readInt();
            this.nameLength = inStream.readByte();

            byte[] nameBytes = new byte[10];
            inStream.readFully(nameBytes);
            this.name = StringProcessor.byteArrayToStringAttribute(nameBytes, this.nameLength);
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array!");
        }
    }

    @Override
    public String toString() {
        return "TestData{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}

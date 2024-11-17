package Model;

import HeapFile.IData;
import Tools.Constants;
import Tools.StringProcessing;

import java.io.*;

public class ServiceVisit implements IData<ServiceVisit> {
    private int date;
    private double price;
    private String desc;
    private byte descLength;
    private int ID;

    public ServiceVisit(int date, double price, String desc) {
        this.date = date;
        this.price = price;

        this.desc = StringProcessing.initStringAttribute(desc, Constants.maxServiceVisitDescLength);
        this.descLength = (byte) this.desc.length();
    }

    @Override
    public ServiceVisit createClass() {
        return new ServiceVisit(0, 0, "");
    }

    @Override
    public boolean isEqualTo(ServiceVisit other) {
        return this.ID == other.ID;
    }

    @Override
    public int getSize() {
        // maxDesc (20 bytes), descLength (1 byte), date (1 int), id (1 int), price (1 double)
        return Constants.serviceVisitSize;
    }

    @Override
    public byte[] getByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteArrayOutputStream);

        try {
            outStream.writeByte(this.descLength);
            outStream.writeInt(this.date);
            outStream.writeDouble(this.price);

            byte[] descBytes = StringProcessing.stringAttributeToByteArray(this.desc, Constants.maxServiceVisitDescLength, this.descLength);
            // zapis pole bajtov o fixnej dlzke (max dlzke popisu)
            outStream.write(descBytes);

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
            this.descLength = inStream.readByte();
            this.date = inStream.readInt();
            this.price = inStream.readDouble();

            // precitaj pole bajtov o fixnej dlzke (max dlzke popisu)
            byte[] descBytes = new byte[Constants.maxServiceVisitDescLength];
            inStream.readFully(descBytes);
            this.desc = StringProcessing.byteArrayToStringAttribute(descBytes, this.descLength);

            this.ID = inStream.readInt();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array!");
        }
    }

    @Override
    public String toString() {
        return "ServiceVisit{" +
                "date=" + date +
                ", price=" + price +
                ", desc='" + desc + '\'' +
                ", descLength=" + descLength +
                ", ID=" + ID +
                '}';
    }
}

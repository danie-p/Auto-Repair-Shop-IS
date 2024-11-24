package Model;

import Tools.Constants;
import Tools.StringProcessor;

import java.io.*;
import java.util.Objects;

public class ServiceVisit {
    private int date;
    private double price;
    private String desc;
    private byte descLength;

    public ServiceVisit(int date, double price, String desc) {
        this.date = date;
        this.price = price;

        this.desc = StringProcessor.initStringAttribute(desc, Constants.maxServiceVisitDescLength);
        this.descLength = (byte) this.desc.length();
    }

    public int getSize() {
        // maxDesc (20 bytes), descLength (1 byte), date (1 int), id (1 int), price (1 double)
        return Constants.serviceVisitSize;
    }

    public byte[] getByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteArrayOutputStream);

        try {
            outStream.writeByte(this.descLength);
            outStream.writeInt(this.date);
            outStream.writeDouble(this.price);

            byte[] descBytes = StringProcessor.stringAttributeToByteArray(this.desc, Constants.maxServiceVisitDescLength, this.descLength);
            // zapis pole bajtov o fixnej dlzke (max dlzke popisu)
            outStream.write(descBytes);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array!");
        }
    }

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
            this.desc = StringProcessor.byteArrayToStringAttribute(descBytes, this.descLength);
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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceVisit serviceVisit)) return false;
        return date == serviceVisit.date &&
                Double.compare(price, serviceVisit.price) == 0 &&
                descLength == serviceVisit.descLength &&
                desc.equals(serviceVisit.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, price, desc, descLength);
    }
}

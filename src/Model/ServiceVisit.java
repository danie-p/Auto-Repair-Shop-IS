package Model;

import Tools.Constants;
import Tools.StringProcessor;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Objects;

public class ServiceVisit {
    private final String[] serviceDescriptions = new String[Constants.maxServiceDescriptionsCount];
    private final byte[] descLengths = new byte[Constants.maxServiceDescriptionsCount];
    private int date;
    private double price;
    private byte serviceDescriptionsCount;

    public ServiceVisit(int date, double price, String[] serviceDescriptions) {
        this.date = date;
        this.price = price;

        for (int i = 0; i < Constants.maxServiceDescriptionsCount; i++) {
            if (serviceDescriptions != null && i < serviceDescriptions.length) {
                this.serviceDescriptions[i] = StringProcessor.initStringAttribute(serviceDescriptions[i], Constants.maxServiceVisitDescLength);
            } else {
                this.serviceDescriptions[i] = "";
            }
            this.descLengths[i] = (byte) this.serviceDescriptions[i].length();
        }

        if (serviceDescriptions == null) {
            this.serviceDescriptionsCount = 0;
        } else if (serviceDescriptions.length >= Constants.maxServiceDescriptionsCount) {
            this.serviceDescriptionsCount = Constants.maxServiceDescriptionsCount;
        } else {
            this.serviceDescriptionsCount = (byte) serviceDescriptions.length;
        }
    }

    public int getSize() {
        // maxDesc (20 bytes), descLength (1 byte), date (1 int), id (1 int), price (1 double)
        return Constants.serviceVisitSize;
    }

    public byte[] getByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteArrayOutputStream);

        try {
            outStream.write(this.descLengths);

            for (int i = 0; i < this.serviceDescriptions.length; i++) {
                byte[] serviceDescBytes = StringProcessor.stringAttributeToByteArray(this.serviceDescriptions[i], Constants.maxServiceVisitDescLength, this.descLengths[i]);
                // zapis pole bajtov o fixnej dlzke (max dlzke popisu prace)
                outStream.write(serviceDescBytes);
            }

            outStream.writeByte(this.serviceDescriptionsCount);
            outStream.writeInt(this.date);
            outStream.writeDouble(this.price);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array!");
        }
    }

    public void fromByteArray(byte[] byteArray) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        DataInputStream inStream = new DataInputStream(byteArrayInputStream);

        try {
            inStream.readFully(this.descLengths);

            for (int i = 0; i < this.serviceDescriptions.length; i++) {
                // precitaj pole bajtov o fixnej dlzke (max dlzke popisu prace)
                byte[] serviceDescBytes = new byte[Constants.maxServiceVisitDescLength];
                inStream.readFully(serviceDescBytes);
                this.serviceDescriptions[i] = StringProcessor.byteArrayToStringAttribute(serviceDescBytes, this.descLengths[i]);
            }

            this.serviceDescriptionsCount = inStream.readByte();
            this.date = inStream.readInt();
            this.price = inStream.readDouble();

        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array!");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < serviceDescriptionsCount; i++) {
            String substr = serviceDescriptions[i].substring(0, descLengths[i]);
            sb.append("'").append(substr);
            if (i == serviceDescriptionsCount - 1) {
                sb.append("'");
            } else {
                sb.append("', ");
            }
        }

        return "ServiceVisit {" +
                "date=" + LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC) +
                ", price=" + price +
                ", serviceDescriptions=" + sb +
                '}';
    }

    public String toStringAttributes() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < serviceDescriptionsCount; i++) {
            String substr = serviceDescriptions[i].substring(0, descLengths[i]);
            sb.append("\n     ").append(substr);
        }

        if (serviceDescriptionsCount == 0)
            sb.append("\n     None");

        return "Date: " + LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC) +
                "\nPrice: " + price +
                "\nService Descriptions:" + sb;
    }

    public String toStringAttributesOneLine() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < serviceDescriptionsCount; i++) {
            String substr = serviceDescriptions[i].substring(0, descLengths[i]);
            sb.append("'").append(substr).append("'");

            if (i != serviceDescriptionsCount - 1)
                sb.append(", ");
        }

        if (serviceDescriptionsCount == 0)
            sb.append("None");

        return "Date: " + LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC) +
                "; Price: " + price +
                "; Service Descriptions: " + sb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceVisit that)) return false;
        return date == that.date &&
                Double.compare(price, that.price) == 0 &&
                serviceDescriptionsCount == that.serviceDescriptionsCount &&
                Arrays.equals(serviceDescriptions, that.serviceDescriptions) &&
                Arrays.equals(descLengths, that.descLengths);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(date, price, serviceDescriptionsCount);
        result = 31 * result + Arrays.hashCode(serviceDescriptions);
        result = 31 * result + Arrays.hashCode(descLengths);
        return result;
    }

    public String[] getServiceDescriptions() {
        return serviceDescriptions;
    }

    public byte[] getDescLengths() {
        return descLengths;
    }

    public int getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public byte getServiceDescriptionsCount() {
        return serviceDescriptionsCount;
    }
}

package ExtendibleHashFile;

public class DirectoryItem {
    private int address;
    private int localDepth;

    public DirectoryItem(int address, int localDepth) {
        this.address = address;
        this.localDepth = localDepth;
    }

    public static DirectoryItem fromCSV(String line) {
        String[] attributes = line.split(",");
        int address = Integer.parseInt(attributes[0]);
        int localDepth = Integer.parseInt(attributes[1]);

        return new DirectoryItem(address, localDepth);
    }

    public void incrementLocalDepth() {
        this.localDepth++;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int blockAddress) {
        this.address = blockAddress;
    }

    public int getLocalDepth() {
        return localDepth;
    }

    public void setLocalDepth(int localDepth) {
        this.localDepth = localDepth;
    }

    public String toCSV() {
        return this.address + "," + this.localDepth;
    }
}

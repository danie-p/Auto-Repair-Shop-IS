package ExtendibleHashFile;

public class DirectoryItem {
    private int address;
    private int localDepth;

    public DirectoryItem(int address, int localDepth) {
        this.address = address;
        this.localDepth = localDepth;
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
}

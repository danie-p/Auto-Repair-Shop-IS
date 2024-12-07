package ExtendibleHashFile;

public class DirectoryItem {
    private int blockAddress;
    private int localDepth;
    private int validCount;

    public DirectoryItem(int blockAddress, int localDepth, int validCount) {
        this.blockAddress = blockAddress;
        this.localDepth = localDepth;
        this.validCount = validCount;
    }

    public static DirectoryItem fromCSV(String line) {
        String[] attributes = line.split(",");
        int address = Integer.parseInt(attributes[0]);
        int localDepth = Integer.parseInt(attributes[1]);
        int validCount = Integer.parseInt(attributes[2]);

        return new DirectoryItem(address, localDepth, validCount);
    }

    public void incrementLocalDepth() {
        this.localDepth++;
    }

    public int getBlockAddress() {
        return blockAddress;
    }

    public void setBlockAddress(int blockAddress) {
        this.blockAddress = blockAddress;
    }

    public int getLocalDepth() {
        return localDepth;
    }

    public void setLocalDepth(int localDepth) {
        this.localDepth = localDepth;
    }

    public int getValidCount() {
        return validCount;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public String toCSV() {
        return this.blockAddress + "," + this.localDepth + "," + this.validCount;
    }
}

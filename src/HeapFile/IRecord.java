package HeapFile;

public interface IRecord {
    int getSize();
    byte[] getByteArray();
    void fromByteArray(byte[] byteArray);
}

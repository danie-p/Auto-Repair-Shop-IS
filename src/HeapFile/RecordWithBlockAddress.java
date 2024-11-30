package HeapFile;

import FileDataStructure.IData;

public class RecordWithBlockAddress<T extends IData<T>> {
    private int blockAddress;
    private T record;

    public RecordWithBlockAddress(int blockAddress, T record) {
        this.blockAddress = blockAddress;
        this.record = record;
    }

    public int getBlockAddress() {
        return blockAddress;
    }

    public void setBlockAddress(int blockAddress) {
        this.blockAddress = blockAddress;
    }

    public T getRecord() {
        return record;
    }

    public void setRecord(T record) {
        this.record = record;
    }

    @Override
    public String toString() {
        return "RecordWithBlockAddress{" +
                "blockAddress=" + blockAddress +
                ", record=" + record +
                '}';
    }
}

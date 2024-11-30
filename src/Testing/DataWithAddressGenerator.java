package Testing;

import FileDataStructure.IData;
import HeapFile.RecordWithBlockAddress;

@FunctionalInterface
public interface DataWithAddressGenerator<T extends IData<T>> {
    RecordWithBlockAddress<T> generateDataWithAddress();
}

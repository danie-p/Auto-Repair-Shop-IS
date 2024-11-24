package Testing;

import HeapFile.IData;
import HeapFile.RecordWithBlockAddress;

@FunctionalInterface
public interface DataGenerator<T extends IData<T>> {
    T generateData();
}

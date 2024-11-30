package FileDataStructure;

import java.util.BitSet;

public interface IData<T> extends IRecord {
    T createClass();
    boolean isEqualTo(T other);
}

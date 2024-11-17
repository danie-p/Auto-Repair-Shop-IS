package HeapFile;

public interface IData<T> extends IRecord {
    T createClass();
    boolean isEqualTo(T other);
}

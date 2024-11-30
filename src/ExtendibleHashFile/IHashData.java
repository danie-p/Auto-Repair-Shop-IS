package ExtendibleHashFile;

import FileDataStructure.IData;

import java.util.BitSet;

public interface IHashData <T> extends IData<T> {
    BitSet getHash();
}

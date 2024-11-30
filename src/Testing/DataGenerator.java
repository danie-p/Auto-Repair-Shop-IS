package Testing;

import FileDataStructure.IData;

@FunctionalInterface
public interface DataGenerator<T extends IData<T>> {
    T generateData();
}

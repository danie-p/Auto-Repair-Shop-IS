package Testing;

import FileDataStructure.IData;

import java.util.HashSet;
import java.util.Random;

public abstract class OperationsGenerator<T extends IData<T>> {
    protected int operationsCount;
    protected final Random random;
    protected final DataGenerator<T> dataGenerator;
    protected final HashSet<T> externalDataSet;

    protected int operationsCounter;

    public OperationsGenerator(int operationsCount, DataGenerator<T> dataGenerator, Random random) {
        this.operationsCount = operationsCount;
        this.random = random;
        this.dataGenerator = dataGenerator;
        this.externalDataSet = new HashSet<>();
        this.operationsCounter = 0;
    }

    protected boolean dataIsNotConsistent(HashSet<T> externalDataStructure, HashSet<T> allDataInExtHashFile) {
        if (externalDataStructure.size() != allDataInExtHashFile.size()) {
            System.out.println("Sizes of external data set and extendible hash file differ! Some data got lost!");
            return true;
        }

        for (T t : externalDataStructure) {
            if (!allDataInExtHashFile.contains(t)) {
                System.out.println("Extendible hash file does not contain all items from external data set! Some data got lost!");
                return true;
            }
        }

        for (T t : allDataInExtHashFile) {
            if (!externalDataStructure.contains(t)) {
                System.out.println("External data set does not contain all items from extendible hash file! Some data got lost!");
                return true;
            }
        }

        return false;
    }
}

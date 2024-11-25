package Testing;

import HeapFile.HeapFile;
import HeapFile.IData;
import HeapFile.RecordWithBlockAddress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class OperationsGenerator<T extends IData<T>> {
    private final int operationsCount = 10000;
    private final Random random;
    private final HeapFile<T> heapFile;
    private final DataGenerator<T> dataGenerator;
    private final DataWithAddressGenerator<T> dataWithAddressGenerator;
    private final HashSet<T> externalDataSet;
    private final ArrayList<RecordWithBlockAddress<T>> externalDataList;
    private int operationsCounter;

    public OperationsGenerator(HeapFile<T> heapFile, DataGenerator<T> dataGenerator, DataWithAddressGenerator<T> dataWithAddressGenerator, Random random) {
        this.random = random;
        this.heapFile = heapFile;
        this.dataGenerator = dataGenerator;
        this.dataWithAddressGenerator = dataWithAddressGenerator;
        this.externalDataSet = new HashSet<>();
        this.externalDataList = new ArrayList<>();
        this.operationsCounter = 0;
    }

    public void insertOnly(int insertCount, T[] data) throws IOException {
        for (int i = 0; i < insertCount; i++) {
            int blockAddress = this.heapFile.insert(data[i]);

            RecordWithBlockAddress<T> record = new RecordWithBlockAddress<T>(blockAddress, data[i]);

            this.externalDataSet.add(data[i]);
            this.externalDataList.add(record);

            this.operationsCounter++;
            System.out.println("Operation " + this.operationsCounter + ": insert; data: " + data[i].toString());
        }
    }

    public boolean insertGetDelete() throws IOException {
        return this.insertGetDelete(0.3, 0.5, 0.8);
    }

    public boolean insertGetDelete(double insertP, double getP, double existingP) throws IOException {
        for (int i = 0; i < this.operationsCount; i++) {
            double randOperation = this.random.nextDouble();

            if (randOperation < insertP) {
                this.insertTest();
            } else if (randOperation < insertP + getP) {
                if (!this.getTest(existingP)) return false;
            } else {
                if (!this.deleteTest(existingP)) return false;
            }

            HashSet<T> allDataInHeapFile = heapFile.getAllDataInHeapFile();
            if (!checkDataConsistency(this.externalDataSet, allDataInHeapFile))
                return false;
        }

        return true;
    }

    private void insertTest() throws IOException {
        T randomInsertedData = this.dataGenerator.generateData();

        int blockAddress = this.heapFile.insert(randomInsertedData);
        this.externalDataSet.add(randomInsertedData);
        this.externalDataList.add(new RecordWithBlockAddress<T>(blockAddress, randomInsertedData));

        this.operationsCounter++;
        System.out.println("Operation " + this.operationsCounter + ": insert; data: " + randomInsertedData.toString());
    }

    private boolean getTest(double existingP) throws IOException {
        double randExistingElement = this.random.nextDouble();
        RecordWithBlockAddress<T> searchedData;

        if (randExistingElement < existingP && !this.externalDataList.isEmpty()) {
            // vyhladaj existujuci (uz vlozeny) prvok
            int randIndex = this.random.nextInt(this.externalDataList.size());
            searchedData = this.externalDataList.get(randIndex);
        } else {
            // vyhladaj nahodny prvok
            searchedData = this.dataWithAddressGenerator.generateDataWithAddress();
        }
        T foundRecord = this.heapFile.get(searchedData.getBlockAddress(), searchedData.getRecord());

        this.operationsCounter++;
        System.out.println("Operation " + this.operationsCounter + ": search; data: " + searchedData.toString());

        if (foundRecord != null && !foundRecord.equals(searchedData.getRecord())) {
            System.out.println("Data was not found!");
            return false;
        }

        if (foundRecord != null && !this.externalDataSet.contains(foundRecord)) {
            System.out.println("Data was not found!");
            return false;
        }

        return true;
    }

    private boolean deleteTest(double existingP) throws IOException {
        double randExistingElement = random.nextDouble();
        RecordWithBlockAddress<T> deletedData;

        if (randExistingElement < existingP && !this.externalDataList.isEmpty()) {
            // vymaz existujuci prvok
            int randIndex = this.random.nextInt(this.externalDataList.size());
            // vymaz prvok aj z pomocnej struktury
            deletedData = this.externalDataList.remove(randIndex);
            if (!this.externalDataSet.remove(deletedData.getRecord())) {
                this.operationsCounter++;
                System.out.println("Operation " + this.operationsCounter + ": delete; data: " + deletedData.toString());
                System.out.println("Data to delete was not found!");
                return false;
            }
            this.heapFile.delete(deletedData.getBlockAddress(), deletedData.getRecord());
        } else {
            // vymaz nahodny prvok
            deletedData = this.dataWithAddressGenerator.generateDataWithAddress();
            this.externalDataList.remove(deletedData);
            this.externalDataSet.remove(deletedData.getRecord());
            this.heapFile.delete(deletedData.getBlockAddress(), deletedData.getRecord());
        }

        this.operationsCounter++;
        System.out.println("Operation " + this.operationsCounter + ": delete; data: " + deletedData.toString());

        return true;
    }

    private boolean checkDataConsistency(HashSet<T> externalDataStructure, HashSet<T> allDataInHeapFile) {
        if (externalDataStructure.size() != allDataInHeapFile.size()) {
            System.out.println("Sizes of external data set and heap file differ! Some data got lost!");
            return false;
        }

        for (T t : externalDataStructure) {
            if (!allDataInHeapFile.contains(t)) {
                System.out.println("Heap file does not contain all items from external data set! Some data got lost!");
                return false;
            }
        }

        for (T t : allDataInHeapFile) {
            if (!externalDataStructure.contains(t)) {
                System.out.println("External data set does not contain all items from heap file! Some data got lost!");
                return false;
            }
        }

        return true;
    }
}

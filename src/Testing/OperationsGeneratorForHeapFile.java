package Testing;

import HeapFile.HeapFile;
import FileDataStructure.IData;
import HeapFile.RecordWithBlockAddress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class OperationsGeneratorForHeapFile<T extends IData<T>> extends OperationsGenerator<T> {
    private final HeapFile<T> heapFile;
    private final DataWithAddressGenerator<T> dataWithAddressGenerator;
    private final ArrayList<RecordWithBlockAddress<T>> externalDataList;

    public OperationsGeneratorForHeapFile(int operationsCount, HeapFile<T> heapFile, DataGenerator<T> dataGenerator, DataWithAddressGenerator<T> dataWithAddressGenerator, Random random) {
        super(operationsCount, dataGenerator, random);
        this.heapFile = heapFile;
        this.dataWithAddressGenerator = dataWithAddressGenerator;
        this.externalDataList = new ArrayList<>();
    }

    public void insertOnly(int insertCount) throws IOException {
        for (int i = 0; i < insertCount; i++) {
            T randomInsertedData = this.dataGenerator.generateData();
            int blockAddress = this.heapFile.insert(randomInsertedData);

            RecordWithBlockAddress<T> record = new RecordWithBlockAddress<T>(blockAddress, randomInsertedData);

            this.externalDataSet.add(randomInsertedData);
            this.externalDataList.add(record);

            this.operationsCounter++;
            System.out.println("Operation " + this.operationsCounter + ": insert; data: " + randomInsertedData.toString());
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

            HashSet<T> allDataInHeapFile = heapFile.getAllDataInFileDataStructure();
            if (dataIsNotConsistent(this.externalDataSet, allDataInHeapFile))
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

    // TODO: pridat do testov
    private void updateTest(double existingP) throws IOException {
        double randExistingElement = random.nextDouble();
        RecordWithBlockAddress<T> oldData;
        RecordWithBlockAddress<T> newData = this.dataWithAddressGenerator.generateDataWithAddress();

        if (randExistingElement < existingP && !externalDataList.isEmpty()) {
            // vyhladaj existujuci (uz vlozeny) prvok
            int randIndex = random.nextInt(externalDataList.size());
            oldData = externalDataList.set(randIndex, newData);
            this.heapFile.update(oldData.getBlockAddress(), oldData.getRecord(), newData.getRecord());
        } else {
            // vyhladaj nahodny prvok
            oldData = this.dataWithAddressGenerator.generateDataWithAddress();
            if (externalDataList.contains(oldData)) {
                externalDataList.set(externalDataList.indexOf(oldData), newData);
            }
            this.heapFile.update(oldData.getBlockAddress(), oldData.getRecord(), newData.getRecord());
        }

        operationsCounter++;
        System.out.println("Operation " + operationsCounter + ": update; old data: " + oldData.toString() + ", new data: " + newData.toString());
    }
}

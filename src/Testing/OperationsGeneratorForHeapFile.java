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

    public boolean insertGetUpdateDelete() throws IOException {
        return this.insertGetUpdateDelete(0.2, 0.3, 0.3, 0.8);
    }

    /**
     * @param insertP pravdepodobnosť operácie vloženia
     * @param getP pravdepodobnosť operácie hľadania
     * @param updateP pravdepodonosť operácie editácie
     * pravdepodobnosť operácie mazania = 1 - (insertP + searchP + updateP)
     * @param existingP pravdepodobnosť hľadania/mazania/editácie existujúceho prvku vloženého v štruktúre
     */
    public boolean insertGetUpdateDelete(double insertP, double getP, double updateP, double existingP) throws IOException {
        for (int i = 0; i < this.operationsCount; i++) {
            double randOperation = random.nextDouble();

            if (randOperation < insertP) {
                insertTest();
            } else if (randOperation < insertP + getP) {
                if (!getTest(existingP)) return false;
            } else if (randOperation < insertP + getP + updateP) {
                if (!updateTest(existingP)) return false;
            } else {
                deleteTest(existingP);
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

    private boolean updateTest(double existingP) throws IOException {
        double randExistingElement = random.nextDouble();
        RecordWithBlockAddress<T> oldDataWithAddress;
        RecordWithBlockAddress<T> newDataWithAddress;
        T newData = this.dataGenerator.generateData();
        T oldData;

        T updatedData;
        if (randExistingElement < existingP && !externalDataList.isEmpty()) {
            // vyhladaj existujuci (uz vlozeny) prvok
            int randIndex = random.nextInt(externalDataList.size());
            oldDataWithAddress = externalDataList.get(randIndex);
            newDataWithAddress = new RecordWithBlockAddress<>(oldDataWithAddress.getBlockAddress(), newData);

            oldDataWithAddress = externalDataList.set(randIndex, newDataWithAddress);
            oldData = oldDataWithAddress.getRecord();

            externalDataSet.remove(oldData);
            externalDataSet.add(newData);

            updatedData = this.heapFile.update(oldDataWithAddress.getBlockAddress(), oldData, newData);

            operationsCounter++;
            System.out.println("Operation " + operationsCounter + ": update; old data: " + oldData + ", new data: " + newData);

        } else {
            // vyhladaj nahodny prvok
            oldDataWithAddress = this.dataWithAddressGenerator.generateDataWithAddress();
            newDataWithAddress = this.dataWithAddressGenerator.generateDataWithAddress();

            if (externalDataList.contains(oldDataWithAddress)) {
                oldDataWithAddress = externalDataList.set(externalDataList.indexOf(oldDataWithAddress), newDataWithAddress);
                oldData = oldDataWithAddress.getRecord();
                externalDataSet.remove(oldData);
                externalDataSet.add(newData);
            } else {
                oldData = null;
            }
            updatedData = this.heapFile.update(oldDataWithAddress.getBlockAddress(), oldData, newData);
            operationsCounter++;
            System.out.println("Operation " + operationsCounter + ": update; old data: " + oldData + ", new data: " + newData);
        }

        if (oldData != null && !oldData.equals(updatedData))
            return false;

        T foundNewData = this.heapFile.get(newDataWithAddress.getBlockAddress(), newData);
        return foundNewData == null || this.externalDataSet.contains(foundNewData);
    }
}

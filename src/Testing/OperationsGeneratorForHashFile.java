package Testing;

import ExtendibleHashFile.ExtendibleHashFile;
import ExtendibleHashFile.IHashData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class OperationsGeneratorForHashFile<T extends IHashData<T>> extends OperationsGenerator<T> {
    private final ExtendibleHashFile<T> extHashFile;
    private final ArrayList<T> externalDataList;

    public OperationsGeneratorForHashFile(int operationsCount, ExtendibleHashFile<T> hashFile, DataGenerator<T> dataGenerator, Random random) {
        super(operationsCount, dataGenerator, random);
        this.extHashFile = hashFile;
        this.externalDataList = new ArrayList<>();
    }

    public void insertOnly(int insertCount) throws IOException {
        for (int i = 0; i < insertCount; i++) {
            T randomInsertedData = this.dataGenerator.generateData();

            this.extHashFile.insert(randomInsertedData);
            this.externalDataSet.add(randomInsertedData);
            this.externalDataList.add(randomInsertedData);

            this.operationsCounter++;
            System.out.println("Operation " + this.operationsCounter + ": insert; data: " + randomInsertedData.toString());
        }
    }

    public boolean insertGetDelete() throws IOException {
        return this.insertGetDelete(0.4, 0.2, 0.8);
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

            HashSet<T> allDataInExtHashFile = extHashFile.getAllDataInFileDataStructure();
            if (dataIsNotConsistent(this.externalDataSet, allDataInExtHashFile))
                return false;
        }
        System.out.println(this.extHashFile.readEmptyBlocks());
        return true;
    }

    public boolean insertGetUpdateDelete() throws IOException {
        return this.insertGetUpdateDelete(0.2, 0.3, 0.3, 0.8);
    }

    /**
     * @param insertP   pravdepodobnosť operácie vloženia
     * @param getP      pravdepodobnosť operácie hľadania
     * @param updateP   pravdepodonosť operácie editácie
     *                  pravdepodobnosť operácie mazania = 1 - (insertP + searchP + updateP)
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

            HashSet<T> allDataInExtHashFile = extHashFile.getAllDataInFileDataStructure();
            if (dataIsNotConsistent(this.externalDataSet, allDataInExtHashFile))
                return false;
        }

        return true;
    }

    private void insertTest() throws IOException {
        T randomInsertedData = this.dataGenerator.generateData();

        this.extHashFile.insert(randomInsertedData);
        this.externalDataSet.add(randomInsertedData);
        this.externalDataList.add(randomInsertedData);

        this.operationsCounter++;
        System.out.println("Operation " + this.operationsCounter + ": insert; data: " + randomInsertedData.toString());
    }

    private boolean getTest(double existingP) throws IOException {
        double randExistingElement = this.random.nextDouble();
        T searchedData;

        if (randExistingElement < existingP && !this.externalDataList.isEmpty()) {
            // vyhladaj existujuci (uz vlozeny) prvok
            int randIndex = this.random.nextInt(this.externalDataList.size());
            searchedData = this.externalDataList.get(randIndex);
        } else {
            // vyhladaj nahodny prvok
            searchedData = this.dataGenerator.generateData();
        }
        T foundData = this.extHashFile.get(searchedData);

        this.operationsCounter++;
        System.out.println("Operation " + this.operationsCounter + ": search; data: " + searchedData.toString());

        if (foundData != null && !foundData.equals(searchedData)) {
            System.out.println("Data was not found!");
            return false;
        }

        if (foundData != null && !this.externalDataSet.contains(foundData)) {
            System.out.println("Data was not found!");
            return false;
        }

        return true;
    }

    private boolean deleteTest(double existingP) throws IOException {
        double randExistingElement = random.nextDouble();
        T deletedData;

        if (randExistingElement < existingP && !this.externalDataList.isEmpty()) {
            // vymaz existujuci prvok
            int randIndex = this.random.nextInt(this.externalDataList.size());
            // vymaz prvok aj z pomocnej struktury
            deletedData = this.externalDataList.remove(randIndex);
            if (!this.externalDataSet.remove(deletedData)) {
                this.operationsCounter++;
                System.out.println("Operation " + this.operationsCounter + ": delete; data: " + deletedData.toString());
                System.out.println("Data to delete was not found!");
                return false;
            }
            this.extHashFile.delete(deletedData);
        } else {
            // vymaz nahodny prvok
            deletedData = this.dataGenerator.generateData();
            this.externalDataList.remove(deletedData);
            this.externalDataSet.remove(deletedData);
            this.extHashFile.delete(deletedData);
        }

        this.operationsCounter++;
        System.out.println("Operation " + this.operationsCounter + ": delete; data: " + deletedData.toString());

        return true;
    }

    private boolean updateTest(double existingP) throws IOException {
        double randExistingElement = random.nextDouble();
        T oldData;
        T newData = this.dataGenerator.generateData();
        T updatedData;

        if (randExistingElement < existingP && !externalDataList.isEmpty()) {
            // vyhladaj existujuci (uz vlozeny) prvok
            int randIndex = random.nextInt(externalDataList.size());
            oldData = externalDataList.set(randIndex, newData);

            externalDataSet.remove(oldData);
            externalDataSet.add(newData);

            updatedData = this.extHashFile.update(oldData, newData);
        } else {
            // vyhladaj nahodny prvok
            oldData = this.dataGenerator.generateData();
            if (externalDataList.contains(oldData)) {
                externalDataList.set(externalDataList.indexOf(oldData), newData);
                externalDataSet.remove(oldData);
                externalDataSet.add(newData);
            }
            updatedData = this.extHashFile.update(oldData, newData);
        }

        operationsCounter++;
        System.out.println("Operation " + operationsCounter + ": update; old data: " + oldData + ", new data: " + newData);

        if (updatedData != null && !oldData.equals(updatedData))
            return false;

        T foundNewData = this.extHashFile.get(newData);
        if (foundNewData != null && !this.externalDataSet.contains(foundNewData))
            return false;

        return true;
    }
}

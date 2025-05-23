package FileDataStructure;

import java.io.*;
import java.util.ArrayList;

public class Block<T extends IData<T>> implements IRecord {
    private final ArrayList<T> records;
    private int validCount;
    private int next;
    private int previous;

    public Block(int clusterSize, T record) {
        // z velkosti clustra vyhrad 3x int pre atributy bloku, zvysne bajty clustra sa mozu pouzit na ukladanie zaznamov
        // recordsCount = blokovaci faktor = pocet zaznamov, ktore sa zmestia do 1 bloku
        int recordsCount = (clusterSize - 3 * Integer.BYTES) / record.getSize();

        if (recordsCount == 0)
            throw new IllegalStateException("Block is too small to contain any records!");

        this.records = new ArrayList<>(recordsCount);
        for (int i = 0; i < recordsCount; i++) {
            this.records.add(record.createClass());
        }
        this.validCount = 0;
        this.next = -1;
        this.previous = -1;
    }

    public ArrayList<T> getValidRecords() {
        ArrayList<T> validRecords = new ArrayList<>();
        for (int i = 0; i < this.validCount; i++) {
            validRecords.add(this.records.get(i));
        }
        return validRecords;
    }

    public int getBlockingFactor() {
        return this.records.size();
    }

    public boolean isPartiallyEmpty() {
        return this.validCount > 0 && this.validCount < this.records.size();
    }

    public boolean isFullyEmpty() {
        return this.validCount == 0;
    }

    public boolean isFull() {
        return this.validCount == this.records.size();
    }

    public boolean insertRecord(T recordToInsert) {
        if (this.isPartiallyEmpty() || this.isFullyEmpty()) {
            // zaznam sa vlozi na prvu neplatnu poziciu v zozname,
            // t.j. na index validCount (validCount zaroven oznacuje index prveho neplatneho zaznamu)
            this.records.set(this.validCount, recordToInsert);
            this.validCount++;
            return true;
        }
        return false;
    }

    /**
     * @param recordToGet dočasný záznam s nastaveným unikátnym kľúčom, podľa ktorého sa má v bloku vyhľadať záznam
     * @return nájdený záznam v bloku
     */
    public T getRecord(T recordToGet) {
        int recordWithEqualKeyIndex = this.findRecordWithEqualKey(recordToGet);

        // ak sa nenasiel v zozname ziadny zaznam s ID zhodnym s vyhladavanym zaznamom
        if (recordWithEqualKeyIndex == -1) {
            return null;
        }

        return this.records.get(recordWithEqualKeyIndex);
    }

    /**
     * @param oldRecord dočasný záznam s nastaveným unikátnym kľúčom, podľa ktorého sa má v bloku vyhľadať záznam na editáciu
     * @param newRecord záznam s úplne nastavenými hodnotami, ktoré sa majú editovať
     * @return pôvodný záznam, ktorý bol editovaný
     */
    public T updateRecord(T oldRecord, T newRecord) {
        int recordWithEqualKeyIndex = this.findRecordWithEqualKey(oldRecord);

        // ak sa nenasiel v zozname ziadny zaznam s ID zhodnym s vyhladavanym zaznamom
        if (recordWithEqualKeyIndex == -1) {
            return null;
        }

        T foundOldRecord = this.records.get(recordWithEqualKeyIndex);
        this.records.set(recordWithEqualKeyIndex, newRecord);

        return foundOldRecord;
    }

    /**
     * @param recordToDelete dočasný záznam s nastaveným unikátnym kľúčom, podľa ktorého sa má v bloku vyhľadať záznam na vymazanie
     * @return práve vymazaný záznam z bloku
     */
    public T deleteRecord(T recordToDelete) {
        int recordWithEqualKeyIndex = this.findRecordWithEqualKey(recordToDelete);

        // ak sa nenasiel v zozname ziadny zaznam s ID zhodnym s vyhladavanym zaznamom
        if (recordWithEqualKeyIndex == -1) {
            return null;
        }

        T deletedRecord = this.records.get(recordWithEqualKeyIndex);

        // zneplatni mazany zaznam
        // vymen mazany zaznam s poslednym platnym zaznamom (t.j. na indexe validCount - 1) v zozname
        T temp = this.records.get(this.validCount - 1);
        this.records.set(this.validCount - 1, deletedRecord);
        this.records.set(recordWithEqualKeyIndex, temp);
        this.validCount--;
        return deletedRecord;
    }

    /**
     * @param recordWithSetID dočasný záznam s nastaveným ID, podľa ktorého sa má vyhľadať záznam v bloku
     * @return index záznamu so zhodným ID; ak sa taký nenašiel, vracia sa -1
     */
    private int findRecordWithEqualKey(T recordWithSetID) {
        int recordWithEqualIDIndex = -1;
        for (int i = 0; i < this.records.size(); i++) {
            if (this.records.get(i).isEqualTo(recordWithSetID)) {
                recordWithEqualIDIndex = i;
                break;
            }
        }
        return recordWithEqualIDIndex;
    }

    @Override
    public int getSize() {
        // velkost atributov tejto triedy, t.j. validCount, next, previous + velkost zaznamov v bloku (= pocet zaznamov * velkost 1 zaznamu)
        return (1 + 1 + 1) * Integer.BYTES + this.records.size() * this.records.get(0).getSize();
    }

    @Override
    public byte[] getByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteArrayOutputStream);

        try {
            outStream.writeInt(this.validCount);
            outStream.writeInt(this.next);
            outStream.writeInt(this.previous);

            // pole bajtov zaznamov v tomto bloku
            // v cykle prejde zaznamy a nad kazdym zavola getByteArray
            for (IData<T> record : this.records) {
                outStream.write(record.getByteArray());
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array!");
        }
    }

    @Override
    public void fromByteArray(byte[] byteArray) {
        // prekonvertuje data ulozene v subore z pola bajtov na pouzitelne data

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        DataInputStream inStream = new DataInputStream(byteArrayInputStream);

        try {
            this.validCount = inStream.readInt();
            this.next = inStream.readInt();
            this.previous = inStream.readInt();

            // iba platne data (prvych validCount zaznamov z records)
            for (int i = 0; i < this.validCount; i++) {
                byte[] byteArrayRecord = new byte[this.records.get(0).getSize()];
                System.arraycopy(byteArray, 3 * Integer.BYTES + i * this.records.get(0).getSize(), byteArrayRecord, 0, this.records.get(0).getSize());
                this.records.get(i).fromByteArray(byteArrayRecord);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array!");
        }
    }

    public int getValidCount() {
        return validCount;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getPrevious() {
        return previous;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (T record : this.records) {
            sb.append("\n\t").append(record);
        }

        return "Block {" +
                "records=" + sb +
                ",\nrecordsCount=" + this.records.size() +
                ", validCount=" + validCount +
                ", next=" + next +
                ", previous=" + previous +
                '}';
    }
}

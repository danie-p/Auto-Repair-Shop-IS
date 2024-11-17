package HeapFile;

import java.io.*;
import java.util.ArrayList;

public class Block<T extends IData<T>> implements IRecord {
    private ArrayList<IData<T>> records;
    private int recordsCount;
    private int validCount;
    private int next;
    private int previous;

    public Block(int blockSize, IData<T> record) {
        this.recordsCount = blockSize / record.getSize();
        this.records = new ArrayList<>(this.recordsCount);
        this.validCount = 0;
        this.next = -1;
        this.previous = -1;
    }

    public boolean isPartiallyEmpty() {
        return this.validCount > 0 && this.validCount < this.recordsCount;
    }

    public boolean isFullyEmpty() {
        return this.validCount == 0;
    }

    public boolean insertRecord(IData<T> insertedRecord) {
        if (this.isPartiallyEmpty() || this.isFullyEmpty()) {
            this.records.set(validCount, insertedRecord);
            this.validCount++;
            return true;
        }
        return false;
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
                System.arraycopy(byteArray, 3 + i * this.records.get(0).getSize(), byteArrayRecord, 0, this.records.get(0).getSize());
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
}

package HeapFile;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class HeapFile<T extends IData<T>> {
    private RandomAccessFile file;
    private String fileName;
    // velkost bloku = velkost clustra
    private int blockSize;
    // velkost zaznamu dana instanciou prveho zaznamu pri vytvoreni
    private int recordSize;
    // adresa prveho ciastocne volneho bloku (zaciatok zretazenia ciastocne volnych blokov)
    private int partiallyFree = -1;
    // adresa prveho uplne volneho bloku (zaciatok zretazenia uplne volnych blokov)
    private int fullyFree = -1;

    // TODO: doplnit nahodne bajty do velkosti clustra
        // pocetZaznamovVBloku = blockSize // recordSize
        // pocetBajtovNaDoplnenie = blockSize % recordSize

    public HeapFile(String fileName, int blockSize, T record) {
        this.fileName = fileName;
        this.blockSize = blockSize;
        this.recordSize = record.getSize();

        try {
            this.file = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error during file opening!");
        }
    }

    // TODO: sekvencny vypis

    /**
     * @param data vkladané dáta
     * @return adresa bloku, do ktorého sa vložili dáta
     */
    public int insert(T data) {
        return 0;
    }

    /**
     * @param blockAddress
     * @param dataWithKey dočasný objekt s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @return nájdené dáta
     */
    public T get(int blockAddress, T dataWithKey) {
        return null;
    }

    /**
     * @param blockAddress
     * @param dataWithKey dočasný objekt s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @return adresa bloku, z ktorého sa vymazali dáta
     */
    public int delete(int blockAddress, T dataWithKey) {
        return 0;
    }
}

package HeapFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class HeapFile<T extends IData<T>> {
    private final RandomAccessFile file;
    // velkost bloku = velkost clustra
    private final int clusterSize;
    // velkost zaznamu dana instanciou prveho zaznamu pri vytvoreni
    private final int recordSize;
    // adresa prveho ciastocne volneho bloku (zaciatok zretazenia ciastocne volnych blokov)
    private int partiallyEmpty;
    // adresa prveho uplne volneho bloku (zaciatok zretazenia uplne volnych blokov)
    private int fullyEmpty;
    private int blocksCount;
    private final T exampleRecord;

    public HeapFile(String fileName, int clusterSize, T record) {
        this.exampleRecord = record;
        this.clusterSize = clusterSize;
        this.recordSize = record.getSize();
        this.blocksCount = 0;
        this.partiallyEmpty = -1;
        this.fullyEmpty = -1;

        try {
            this.file = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error during file opening!");
        }
    }

    public void readSequentially() throws IOException {
        int i = 0;
        while (i < this.file.length() / this.clusterSize) {
            Block<T> readBlock = this.readBlockFromFile(i);
            System.out.println("Block " + i + ":\n" + readBlock);
            i++;
        }
    }

    private void writeBlockIntoFile(int blockIndex, Block<T> block) throws IOException {
        // seek na adresu zapisovaneho bloku
        this.file.seek((long) blockIndex * this.clusterSize);

        // ak blok (jeho atributy validCount, next, prev a vsetky zaznamy) skutocne nevyplni celu velkost clustra,
        // umelo dopln bajty do plnej velkosti clustra ... defaultne hodnoty (0) zaplnaju zvysne nevyuzitelne miesto
        byte[] blockBytes = Arrays.copyOf(block.getByteArray(), this.clusterSize);

        // do suboru sa zapise zapisovany blok prekonvertovany na pole bajtov (serializovane data) + doplnene bajty
        this.file.write(blockBytes);
    }

    private Block<T> readBlockFromFile(int blockIndex) throws IOException {
        if (blockIndex >= this.blocksCount || blockIndex == -1)
            return null;

        long blockPosition = (long) blockIndex * this.clusterSize;

        // tato kontrola mozno uz ani nie je potrebna
        if (blockPosition >= this.file.length())
            return null;

        // seek na adresu citaneho bloku
        this.file.seek(blockPosition);
        // nove pole bajtov o velkosti clustra
        byte[] blockBytes = new byte[this.clusterSize];
        // do pola bajtov sa nacitaju data zo suboru (o rozsahu citaneho bloku)
        this.file.readFully(blockBytes);

        Block<T> readBlock = new Block<T>(this.clusterSize, this.exampleRecord);
        // do noveho bloku sa nacitaju data z pola bajtov = deserializacia
        readBlock.fromByteArray(blockBytes);

        // vrat blok s precitanymi datami
        return readBlock;
    }

    /**
     * @param record vkladané dáta
     * @return adresa bloku, do ktorého sa vložili dáta
     */
    public int insert(T record) throws IOException {
        if (record.getSize() != this.recordSize) {
            throw new IllegalArgumentException("Incorrect size of inserted record!");
        }

        int indexOfBlockToInsertInto;

        if (this.partiallyEmpty != -1) {
            // ak v subore je nejaky ciastocne volny blok
            indexOfBlockToInsertInto = this.partiallyEmpty;

            // precitaj existujuci ciastocne prazdny blok zo suboru
            Block<T> blockToInsertInto = this.readBlockFromFile(indexOfBlockToInsertInto);

            // pokus sa zapisat zaznam do precitaneho bloku
            if (blockToInsertInto != null && blockToInsertInto.insertRecord(record)) {
                // zapis blok s vlozenym zaznamom do suboru
                this.writeBlockIntoFile(indexOfBlockToInsertInto, blockToInsertInto);

                // po vlozeni zaznamu do ciastocne prazdneho bloku moze byt tento blok ciastocne prazdny alebo plny
                if (blockToInsertInto.isFull()) {
                    // ak je po vlozeni plny ... odstran ho zo zretazenia ciastocne prazdnych blokov a nahrad ho nasledovnikom
                    this.partiallyEmpty = blockToInsertInto.getNext();
                    blockToInsertInto.setNext(-1);
                    Block<T> nextBlock = this.readBlockFromFile(this.partiallyEmpty);
                    if (nextBlock != null)
                        nextBlock.setPrevious(-1);
                }
                // ak je aj po vlozeni stale ciastocne prazdny ... ostava ako prvy v zretazeni ciastocne prazdnych blokov, nic sa nemeni
            } else {
                throw new IllegalStateException("Error inserting a record into a partially empty block!");
            }
        } else if (this.fullyEmpty != -1) {
            // ak v subore je nejaky plne volny blok
            indexOfBlockToInsertInto = this.fullyEmpty;

            // precitaj existujuci plne prazdny blok zo suboru
            Block<T> blockToInsertInto = this.readBlockFromFile(indexOfBlockToInsertInto);

            // pokus sa zapisat zaznam do precitaneho bloku
            if (blockToInsertInto != null && blockToInsertInto.insertRecord(record)) {
                // zapis blok s vlozenym zaznamom do suboru
                this.writeBlockIntoFile(indexOfBlockToInsertInto, blockToInsertInto);

                // po vlozeni zaznamu do plne prazdneho bloku moze byt tento blok ciastocne prazdny alebo plny
                if (blockToInsertInto.isFull()) {
                    // ak je po vlozeni plny ... odstran ho zo zretazenia plne prazdnych blokov a nahrad ho nasledovnikom
                    this.fullyEmpty = blockToInsertInto.getNext();
                    blockToInsertInto.setNext(-1);
                    Block<T> nextBlock = this.readBlockFromFile(this.fullyEmpty);
                    if (nextBlock != null)
                        nextBlock.setPrevious(-1);
                } else if (blockToInsertInto.isPartiallyEmpty()) {
                    // ak je po vlozeni ciastocne prazdny ... odstran ho zo zretazenia plne prazdnych blokov
                    this.fullyEmpty = blockToInsertInto.getNext();
                    // a pripoj ho do zretazenia ciastocne prazdnych blokov na PRVE miesto
                    // zretazenie ciastocne prazdnych blokov je doteraz urcite prazdne, kedze blok bol vlozeny az do plne volneho bloku
                    this.partiallyEmpty = indexOfBlockToInsertInto;
                    blockToInsertInto.setNext(-1);
                    Block<T> nextBlock = this.readBlockFromFile(this.partiallyEmpty);
                    if (nextBlock != null)
                        nextBlock.setPrevious(-1);
                }
            } else {
                throw new IllegalStateException("Error inserting a record into a fully empty block!");
            }
        } else {
            // ak v subore nie je ziadny volny blok, pridaj novy blok na koniec suboru a don zapis data
            indexOfBlockToInsertInto = this.blocksCount;

            Block<T> blockToInsertInto = new Block<T>(this.clusterSize, record);
            if (blockToInsertInto.insertRecord(record)) {
                this.writeBlockIntoFile(indexOfBlockToInsertInto, blockToInsertInto);

                // po vlozeni zaznamu do noveho bloku moze byt tento blok ciastocne prazdny alebo plny
                if (blockToInsertInto.isPartiallyEmpty()) {
                    // ak je po vlozeni ciastocne prazdny, pripoj ho do zretazenia ciastocne prazdnych blokov na PRVE miesto
                    // zretazenie ciastocne prazdnych blokov je doteraz urcite prazdne, kedze blok bol vlozeny az do noveho bloku
                    this.partiallyEmpty = indexOfBlockToInsertInto;
                }
                // ak sa novy blok vlozenim jedneho zaznamu hned uplne zaplni, netreba robit ziadny manazment volnych blokov

                this.blocksCount++;
            } else {
                throw new IllegalStateException("Error inserting a record into a new empty block!");
            }
        }

        return indexOfBlockToInsertInto;
    }

    /**
     * @param blockAddress
     * @param dataWithKey dočasný objekt (záznam) s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @return nájdený záznam
     */
    public T get(int blockAddress, T dataWithKey) {
        return null;
    }

    /**
     * @param blockAddress
     * @param dataWithKey dočasný objekt (záznam) s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @return adresa bloku, z ktorého sa vymazal záznam
     */
    public int delete(int blockAddress, T dataWithKey) {
        return 0;
    }

    /**
     * Metóda na zatvorenie súboru.
     */
    public void close() {

    }
}

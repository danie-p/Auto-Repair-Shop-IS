package HeapFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class HeapFile<T extends IData<T>> {
    private final RandomAccessFile file;
    // velkost bloku = velkost clustra
    protected final int clusterSize;
    // velkost zaznamu dana instanciou prveho zaznamu pri vytvoreni
    private final int recordSize;
    // adresa prveho ciastocne volneho bloku (zaciatok zretazenia ciastocne volnych blokov)
    private int partiallyEmpty;
    // adresa prveho uplne volneho bloku (zaciatok zretazenia uplne volnych blokov)
    private int fullyEmpty;
    protected int blocksCount;
    protected final T exampleRecord;
    private final String fileName;

    public HeapFile(String fileName, int clusterSize, T record) {
        this.exampleRecord = record;
        this.clusterSize = clusterSize;
        this.recordSize = record.getSize();
        this.blocksCount = 0;
        this.partiallyEmpty = -1;
        this.fullyEmpty = -1;
        this.fileName = fileName;

        try {
            this.file = new RandomAccessFile(fileName + ".dat", "rw");
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

    /**
     * Metóda slúži len pre potreby testovania štruktúry HeapFile. Sprístupňuje všetky záznamy uložené v HeapFile.
     * @return HashSet všetkých záznamov uložených v HeapFile
     */
    public HashSet<T> getAllDataInHeapFile() throws IOException {
        HashSet<T> allDataSet = new HashSet<>();

        int i = 0;
        while (i < this.file.length() / this.clusterSize) {
            Block<T> readBlock = this.readBlockFromFile(i);
            if (readBlock != null) {
                ArrayList<T> validRecords = readBlock.getValidRecords();
                allDataSet.addAll(validRecords);
            }
            i++;
        }

        return allDataSet;
    }

    protected void writeBlockIntoFile(int blockAddress, Block<T> block) throws IOException {
        // seek na adresu zapisovaneho bloku
        this.file.seek((long) blockAddress * this.clusterSize);

        // ak blok (jeho atributy validCount, next, prev a vsetky zaznamy) skutocne nevyplni celu velkost clustra,
        // umelo dopln bajty do plnej velkosti clustra ... defaultne hodnoty (0) zaplnaju zvysne nevyuzitelne miesto
        byte[] blockBytes = Arrays.copyOf(block.getByteArray(), this.clusterSize);

        // do suboru sa zapise zapisovany blok prekonvertovany na pole bajtov (serializovane data) + doplnene bajty
        this.file.write(blockBytes);
    }

    protected Block<T> readBlockFromFile(int blockAddress) throws IOException {
        if (blockAddress >= this.blocksCount || blockAddress < 0)
            return null;

        long blockPosition = (long) blockAddress * this.clusterSize;

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
     * @param record vkladaný záznam
     * @return adresa bloku, do ktorého sa vložil záznam
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
                // po vlozeni zaznamu do ciastocne prazdneho bloku moze byt tento blok ciastocne prazdny alebo plny
                if (blockToInsertInto.isFull()) {
                    // ak je po vlozeni plny ... odstran ho zo zretazenia ciastocne prazdnych blokov a nahrad ho nasledovnikom
                    this.partiallyEmpty = blockToInsertInto.getNext();
                    blockToInsertInto.setNext(-1);

                    // aktualizuj nahradnika
                    Block<T> nextBlock = this.readBlockFromFile(this.partiallyEmpty);
                    if (nextBlock != null) {
                        nextBlock.setPrevious(-1);
                        this.writeBlockIntoFile(this.partiallyEmpty, nextBlock);
                    }
                }
                // inak je aj po vlozeni stale ciastocne prazdny ... ostava ako prvy v zretazeni ciastocne prazdnych blokov, nic sa nemeni

                // zapis blok s vlozenym zaznamom do suboru
                this.writeBlockIntoFile(indexOfBlockToInsertInto, blockToInsertInto);
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
                // po vlozeni zaznamu do plne prazdneho bloku moze byt tento blok ciastocne prazdny alebo plny
                if (blockToInsertInto.isFull()) {
                    // ak je po vlozeni plny ... odstran ho zo zretazenia plne prazdnych blokov a nahrad ho nasledovnikom
                    this.fullyEmpty = blockToInsertInto.getNext();
                    blockToInsertInto.setNext(-1);

                    // aktualizuj nahradnika
                    Block<T> nextBlock = this.readBlockFromFile(this.fullyEmpty);
                    if (nextBlock != null) {
                        nextBlock.setPrevious(-1);
                        this.writeBlockIntoFile(this.fullyEmpty, nextBlock);
                    }
                } else {
                    // ak je po vlozeni ciastocne prazdny ... odstran ho zo zretazenia plne prazdnych blokov
                    this.fullyEmpty = blockToInsertInto.getNext();
                    // a pripoj ho do zretazenia ciastocne prazdnych blokov na PRVE miesto
                    // zretazenie ciastocne prazdnych blokov je doteraz urcite prazdne, kedze blok bol vlozeny az do plne volneho bloku
                    this.partiallyEmpty = indexOfBlockToInsertInto;
                    blockToInsertInto.setNext(-1);

                    // aktualizuj nahradnika
                    Block<T> nextBlock = this.readBlockFromFile(this.partiallyEmpty);
                    if (nextBlock != null) {
                        nextBlock.setPrevious(-1);
                        this.writeBlockIntoFile(this.partiallyEmpty, nextBlock);
                    }
                }
                // zapis blok s vlozenym zaznamom do suboru
                this.writeBlockIntoFile(indexOfBlockToInsertInto, blockToInsertInto);
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
     * @param blockAddress adresa (index) bloku uchovávajúceho hľadaný záznam
     * @param recordWithKey dočasný objekt (záznam) s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @return nájdený záznam
     */
    public T get(int blockAddress, T recordWithKey) throws IOException {
        Block<T> foundBlock = this.getBlockWithRecord(blockAddress, recordWithKey);

        if (foundBlock == null)
            return null;

        // podla ID ziskaj konkretny hladany zaznam z najdeneho nacitaneho bloku
        return foundBlock.getRecord(recordWithKey);
    }

    private Block<T> getBlockWithRecord(int blockAddress, T recordWithKey) throws IOException {
        if (recordWithKey.getSize() != this.recordSize) {
            throw new IllegalArgumentException("Incorrect size of searched record!");
        }

        if (blockAddress >= this.blocksCount || blockAddress < 0) {
            return null;
        }

        return this.readBlockFromFile(blockAddress);
    }

    /**
     * @param blockAddress adresa (index) bloku uchovávajúceho hľadaný záznam na mazanie
     * @param recordWithKey dočasný objekt (záznam) s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @return vymazaný záznam
     */
    public T delete(int blockAddress, T recordWithKey) throws IOException {
        Block<T> foundBlock = this.getBlockWithRecord(blockAddress, recordWithKey);

        if (foundBlock == null || foundBlock.isFullyEmpty())
            return null;

        T deletedRecord;

        if (foundBlock.isFull()) {
            // ak bol blok pred vymazanim zaznamu plny
            deletedRecord = foundBlock.deleteRecord(recordWithKey);

            if (foundBlock.isPartiallyEmpty()) {
                // ak blok ostane po vymazani zaznamu ciastocne prazdny, pridaj ho do zretazenia ciastocne prazdnych blokov (na zaciatok)
                this.setFirstBlockInChain(blockAddress, foundBlock, true);
            } else {
                // ak blok ostane po vymazani zaznamu prazdny
                this.manageFullyEmptyBlock(blockAddress, foundBlock);
            }
        } else {
            // ak bol blok pred vymazanim zaznamu ciastocne prazdny
            deletedRecord = foundBlock.deleteRecord(recordWithKey);

            if (foundBlock.isFullyEmpty()) {
                // odstran zo zretazenia ciastocne prazdnych blokov
                this.removeEmptyBlockFromChain(blockAddress, foundBlock, true);
                this.manageFullyEmptyBlock(blockAddress, foundBlock);
            } else {
                // inak ak blok po vymazani zaznamu stale ostane ciastocne prazdny, netreba robit manazment volnych blokov
                // ale je potrebne aktualizovat obsah bloku, z ktoreho sa vymazal zaznam
                this.writeBlockIntoFile(blockAddress, foundBlock);
            }
        }

        return deletedRecord;
    }

    private void manageFullyEmptyBlock(int blockAddress, Block<T> block) throws IOException {
        // ak blok po vymazani zaznamu ostane uplne prazdny, skontroluj, ci nie je na konci suboru
        if (blockAddress == this.blocksCount - 1) {
            // prazdny blok je na konci suboru, blok uvolni skratenim suboru a cyklicky skontroluj aj predosle
            this.removeFullyEmptyBlocksFromEnd();
        } else {
            // ak nie je na konci suboru, pridaj ho do zretazenia uplne prazdnych blokov (na zaciatok)
            this.setFirstBlockInChain(blockAddress, block, false);
        }
    }

    private void setFirstBlockInChain(int blockAddress, Block<T> block, boolean partiallyEmptyChain) throws IOException {
        int currentFirst = partiallyEmptyChain ? this.partiallyEmpty : this.fullyEmpty;

        // 1. aktualizuj aktualne prvy blok
        Block<T> firstPartiallyEmptyBlock = this.readBlockFromFile(currentFirst);
        if (firstPartiallyEmptyBlock != null) {
            firstPartiallyEmptyBlock.setPrevious(blockAddress);
            this.writeBlockIntoFile(currentFirst, firstPartiallyEmptyBlock);
        }

        // 2. aktualizuj blok, z ktoreho sa mazal zaznam
        block.setNext(currentFirst);
        block.setPrevious(-1);
        this.writeBlockIntoFile(blockAddress, block);

        // 3. aktualizuj zaciatok zretazenia
        if (partiallyEmptyChain) {
            this.partiallyEmpty = blockAddress;
        } else {
            this.fullyEmpty = blockAddress;
        }
    }

    private void removeFullyEmptyBlocksFromEnd() throws IOException {
        // prazdny blok je na konci suboru, blok uvolni skratenim suboru
        this.file.setLength(this.file.length() - this.clusterSize);
        this.blocksCount--;

        // cyklicky skontroluj aj blok(y) pred vymazaným blokom
        Block<T> newLastBlock = this.readBlockFromFile(this.blocksCount - 1);
        while (newLastBlock != null && newLastBlock.isFullyEmpty()) {
            // odstran blok zo zretazenia uplne prazdnych blokov
            this.removeEmptyBlockFromChain(this.blocksCount - 1, newLastBlock, false);

            // uvolni prazdny blok na konci
            this.file.setLength(this.file.length() - this.clusterSize);
            this.blocksCount--;

            // pokracuj kontrolou noveho posledneho bloku
            newLastBlock = this.readBlockFromFile(this.blocksCount - 1);
        }
    }

    private void removeEmptyBlockFromChain(int blockAddress, Block<T> block, boolean partiallyEmptyChain) throws IOException {
        // 1. aktualizuj nasledovnika
        int next = block.getNext();
        Block<T> nextBlock = this.readBlockFromFile(next);
        if (nextBlock != null) {
            // zretaz ho s predchodcom
            nextBlock.setPrevious(block.getPrevious());
            this.writeBlockIntoFile(next, nextBlock);
        }

        // 2. aktualizuj predchodcu
        int previous = block.getPrevious();
        Block<T> previousBlock = this.readBlockFromFile(previous);
        if (previousBlock != null) {
            // zretaz ho s nasledovnikom
            previousBlock.setNext(block.getNext());
            this.writeBlockIntoFile(previous, previousBlock);
        }

        // ak bol prvy v zretazeni, nastav novy prvy
        if (!partiallyEmptyChain && this.fullyEmpty == blockAddress)
            this.fullyEmpty = next;

        if (partiallyEmptyChain && this.partiallyEmpty == blockAddress)
            this.partiallyEmpty = next;
    }

    /**
     * Metóda na zatvorenie súboru. Je potrebné ju zavolať pre korektné ukončenie práce so súborom.
     */
    public void close() {
        String controlInfoFileName = this.fileName + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(controlInfoFileName))) {
            writer.write("Cluster_size: " + this.clusterSize);
            writer.newLine();
            writer.write("Partially_empty: " + this.partiallyEmpty);
            writer.newLine();
            writer.write("Fully_empty: " + this.fullyEmpty);
            writer.newLine();
            writer.write("Blocks_count: " + this.blocksCount);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error during writing control information into a text file!");
        }

        try {
            this.file.close();
        } catch (IOException e) {
            throw new RuntimeException("Error during heap file closing!");
        }
    }
}

package FileDataStructure;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public abstract class FileDataStructure<T extends IData<T>> {
    protected final RandomAccessFile file;
    // velkost bloku = velkost clustra
    protected final int clusterSize;
    // velkost zaznamu dana instanciou prveho zaznamu pri vytvoreni
    protected final int recordSize;
    // adresa prveho uplne volneho bloku (zaciatok zretazenia uplne volnych blokov)
    protected int fullyEmpty;
    protected int blocksCount;
    protected final T exampleRecord;
    protected final String fileName;

    protected FileDataStructure(String fileName, int clusterSize, T record) {
        this.exampleRecord = record;
        this.clusterSize = clusterSize;
        this.recordSize = record.getSize();
        this.blocksCount = 0;
        this.fullyEmpty = -1;
        this.fileName = fileName;

        try {
            this.file = new RandomAccessFile(fileName + ".dat", "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error during file opening!");
        }
    }

    protected FileDataStructure(String fileName, int clusterSize, int fullyEmpty, int blocksCount, T record) {
        this.exampleRecord = record;
        this.clusterSize = clusterSize;
        this.recordSize = record.getSize();
        this.blocksCount = blocksCount;
        this.fullyEmpty = fullyEmpty;
        this.fileName = fileName;

        try {
            this.file = new RandomAccessFile(fileName + ".dat", "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error during file opening!");
        }
    }

    protected void clear() throws IOException {
        this.file.setLength(0);
        this.fullyEmpty = -1;
        this.blocksCount = 0;
    }

    public void writeBlockIntoFile(int blockAddress, Block<T> block) throws IOException {
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

    public String readSequentially() throws IOException {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (i < this.file.length() / this.clusterSize) {
            Block<T> readBlock = this.readBlockFromFile(i);
            sb.append("=== Block ").append(i).append(" ===\n").append(readBlock).append("\n\n");
            i++;
        }

        return sb.toString();
    }

    public String readEmptyBlocks() throws IOException {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (i < this.file.length() / this.clusterSize) {
            Block<T> readBlock = this.readBlockFromFile(i);
            if (readBlock != null && readBlock.isFullyEmpty()) {
                // kontrola prazdnych blokov mimo zretazenia ... nemali by sa vypisat ziadne
//                if (readBlock.getNext() == -1 && readBlock.getPrevious() == -1 && i != this.fullyEmpty)
                    sb.append("=== Block ").append(i).append(" ===\n").append(readBlock).append("\n\n");
            }
            i++;
        }

        return sb.toString();
    }

    /**
     * Pomocná metóda na odstránenie bloku zo začiatku zreťazenia plne prázdnych blokov.
     * Ak existuje jeho nasledovník, používajú sa 2 prístupy do súboru (1 na prečítanie a 1 na zápis nasledovníka).
     * @param blockToRemoveFromFullyEmptyChain blok, ktorý sa má zo zreťazenia odstrániť (doteraz bol v zreťazení prvý)
     */
    protected void removeFirstFullyEmptyBlockFromChain(Block<T> blockToRemoveFromFullyEmptyChain) throws IOException {
        // odstran blok zo zretazenia plne prazdnych blokov
        this.fullyEmpty = blockToRemoveFromFullyEmptyChain.getNext();
        blockToRemoveFromFullyEmptyChain.setNext(-1);

        // aktualizuj nahradnika
        this.updateNextBlock(this.fullyEmpty);
    }

    protected void updateNextBlock(int nextBlockAddress) throws IOException {
        Block<T> nextBlock = this.readBlockFromFile(nextBlockAddress);
        if (nextBlock != null) {
            nextBlock.setPrevious(-1);
            this.writeBlockIntoFile(nextBlockAddress, nextBlock);
        }
    }

    protected void manageFullyEmptyBlock(int blockAddress, Block<T> block) throws IOException {
        // ak blok po vymazani zaznamu ostane uplne prazdny, skontroluj, ci nie je na konci suboru
        if (blockAddress == this.blocksCount - 1) {
            // prazdny blok je na konci suboru, blok uvolni skratenim suboru a cyklicky skontroluj aj predosle
            this.removeFullyEmptyBlocksFromEnd();
        } else {
            // ak nie je na konci suboru, pridaj ho do zretazenia uplne prazdnych blokov (na zaciatok)
            this.setFirstBlockInChain(blockAddress, block, this.fullyEmpty);
            // aktualizuj zaciatok zretazenia plne prazdnych blokov
            this.fullyEmpty = blockAddress;
        }
    }

    protected void removeEmptyBlockFromChain(Block<T> block) throws IOException {
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
    }

    protected void setFirstBlockInChain(int blockAddress, Block<T> block, int currentFirst) throws IOException {
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
    }

    private void removeFullyEmptyBlocksFromEnd() throws IOException {
        // prazdny blok je na konci suboru, blok uvolni skratenim suboru
        this.file.setLength(this.file.length() - this.clusterSize);
        this.blocksCount--;

        // cyklicky skontroluj aj blok(y) pred vymazaným blokom
        Block<T> newLastBlock = this.readBlockFromFile(this.blocksCount - 1);
        while (newLastBlock != null && newLastBlock.isFullyEmpty()) {
            // odstran blok zo zretazenia uplne prazdnych blokov
            this.removeEmptyBlockFromChain(newLastBlock);

            // ak bol prvy v zretazeni plne prazdnych blokov, nastav novy prvy
            if (this.fullyEmpty == this.blocksCount - 1)
                this.fullyEmpty = newLastBlock.getNext();

            // uvolni prazdny blok na konci
            this.file.setLength(this.file.length() - this.clusterSize);
            this.blocksCount--;

            // pokracuj kontrolou noveho posledneho bloku
            newLastBlock = this.readBlockFromFile(this.blocksCount - 1);
        }
    }

    /**
     * Metóda slúži len pre potreby testovania štruktúry. Sprístupňuje všetky záznamy uložené v súbore.
     * @return HashSet všetkých záznamov uložených v súbore
     */
    public HashSet<T> getAllDataInFileDataStructure() throws IOException {
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

    /**
     * Metóda na zatvorenie súboru. Je potrebné ju zavolať pre korektné ukončenie práce so súborom.
     */
    protected PrintWriter writeControlInfo(String fileName) throws IOException {
        String controlInfoFileName = fileName + ".txt";
        PrintWriter writer = new PrintWriter(new FileWriter(controlInfoFileName));
        writer.println(this.fullyEmpty);
        writer.println(this.blocksCount);

        return writer;
    }

    public abstract void close(String fileName);
}

package FileDataStructure;

import java.io.*;
import java.util.Arrays;

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

    public void readSequentially() throws IOException {
        int i = 0;
        while (i < this.file.length() / this.clusterSize) {
            Block<T> readBlock = this.readBlockFromFile(i);
            System.out.println("Block " + i + ":\n" + readBlock);
            i++;
        }
    }

    /**
     * Metóda na zatvorenie súboru. Je potrebné ju zavolať pre korektné ukončenie práce so súborom.
     */
    protected BufferedWriter writeControlInfo() throws IOException {
        String controlInfoFileName = this.fileName + ".txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(controlInfoFileName));
        writer.write("Cluster_size: " + this.clusterSize);
        writer.newLine();
        writer.write("Fully_empty: " + this.fullyEmpty);
        writer.newLine();
        writer.write("Blocks_count: " + this.blocksCount);
        writer.newLine();

        return writer;
    }
}

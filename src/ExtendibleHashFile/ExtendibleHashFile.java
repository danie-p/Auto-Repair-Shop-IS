package ExtendibleHashFile;

import FileDataStructure.*;
import Tools.BitSetUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Scanner;

public class ExtendibleHashFile<T extends IHashData<T>> extends FileDataStructure<T> {
    private int fileDepth; // D
    // adresár sa nachádza v operačnej pamäti
    // je to jednorozmerné pole adries (celočíselných hodnôt)
    private ArrayList<DirectoryItem> directory;

    public ExtendibleHashFile(String fileName, int clusterSize, T record) {
        super(fileName, clusterSize, record);

        // v prazdnom subore D = 1
        this.fileDepth = 1;
        this.directory = new ArrayList<>();

        // na zaciatku adresar obsahuje 2 polozky
        // obe polozky ukazuju na zaciatok suboru (adresa 0)
        // oba bloky maju hlbku d = 1
        this.directory.add(new DirectoryItem(-1, 1, -1));
        this.directory.add(new DirectoryItem(-1, 1, -1));
    }

    public ExtendibleHashFile(String fileName, int clusterSize, int fullyEmpty, int blocksCount, T record, int fileDepth, ArrayList<DirectoryItem> directory) {
        super(fileName, clusterSize, fullyEmpty, blocksCount, record);
        this.fileDepth = fileDepth;
        this.directory = directory;
    }

    public static <T extends IHashData<T>> ExtendibleHashFile<T> fromFile(String controlHashFileName, String hashFileName, int clusterSize, T record) {
        File fileControlHash = new File(controlHashFileName + ".txt");

        if (fileControlHash.length() != 0) {
            try (Scanner scanner = new Scanner(fileControlHash)) {
                int fullyEmpty = scanner.nextInt();
                int blocksCount = scanner.nextInt();
                int fileDepth = scanner.nextInt();
                scanner.nextLine();

                ArrayList<DirectoryItem> directory = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    directory.add(DirectoryItem.fromCSV(line));
                }

                scanner.close();
                return new ExtendibleHashFile<T>(hashFileName, clusterSize, fullyEmpty, blocksCount, record, fileDepth, directory);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Error during data import extendible hash file opening!");
            }
        } else {
            return new ExtendibleHashFile<T>(hashFileName, clusterSize, record);
        }
    }

    public void clear() throws IOException {
        super.clear();
        this.fileDepth = 1;
        this.directory = new ArrayList<>();
        this.directory.add(new DirectoryItem(-1, 1, -1));
        this.directory.add(new DirectoryItem(-1, 1, -1));
    }

    private int getDirectoryIndex(T record) {
        // vysledkom je index v adresari, na ktorom je ulozena hladana adresa
        BitSet hash = record.getHash();

        // z vysledku hesovania vezmi prvych D bitov
        BitSet firstDBits = new BitSet(this.fileDepth);
        for (int i = 0; i < this.fileDepth; i++) {
            if (hash.get(i))
                firstDBits.set(i);
        }

        // tieto bity prekonvertuj BIN -> DEC

        return BitSetUtility.bitSetToInt(firstDBits, this.fileDepth);
    }

    private int getDirectoryIndexNeighbour(T record, int localDepth) {
        BitSet hash = record.getHash();

        // nastav iba prvych d - 1 (localDepth - 1) bitov,
        BitSet firstDBits = new BitSet(this.fileDepth);
        for (int i = 0; i < localDepth - 1; i++) {
            if (hash.get(i))
                firstDBits.set(i);
        }

        // posledny bit podla localDepth
        if (hash.get(localDepth - 1)) {
            // ak je na poslednom bit podla localDepth nastavena 1, sused tam ma 0
            firstDBits.clear(localDepth - 1);
        } else {
            // ak je na poslednom bit podla localDepth nastavena 0, sused tam ma 1
            firstDBits.set(localDepth - 1);
        }

        // ostatne bity nechaj na 0

        return BitSetUtility.bitSetToInt(firstDBits, this.fileDepth);
    }

    private int maskDirectoryIndexByLocalDepth(T record, int localDepth) {
        BitSet hash = record.getHash();

        // nastav iba prvych d (localDepth) bitov, ostatne nechaj na 0
        BitSet firstDBits = new BitSet(this.fileDepth);
        for (int i = 0; i < localDepth; i++) {
            if (hash.get(i))
                firstDBits.set(i);
        }

        return BitSetUtility.bitSetToInt(firstDBits, this.fileDepth);
    }

    /*
    while niejevlozene do {
        VypočítajHash // získame adresu bloku
        if BlokJePlný then {
            if HĺbkaBloku=HĺbkaSúboru then {
                ZdojnásobAdresár;
            }
            RozdelenieBloku; // Split – vytvorenie nového bloku
        } else {
            VložZáznam;
        }
    }
     */
    public void insert(T record) throws IOException {
        boolean hasBeenInserted = false;

        while (!hasBeenInserted) {
            // ziskaj index do adresara na zaklade hesovania kluca
            int directoryIndex = this.getDirectoryIndex(record);

            if (directoryIndex >= this.directory.size())
                throw new RuntimeException("Hash result out of directory index range!");

            DirectoryItem directoryItem = this.directory.get(directoryIndex);
            // ziskaj adresu bloku, do ktoreho sa ma zaznam vlozit
            int blockAddress = directoryItem.getBlockAddress();
            int localDepth = directoryItem.getLocalDepth();
            int validCount = directoryItem.getValidCount();

            // index do adresara na prvej pozicii, kde sa nachadza prvok s danou adresou bloku
            // na ziskanie indexu sa pouzije len prvych localDepth bitov, ostatne su vynulovane
            directoryIndex = this.maskDirectoryIndexByLocalDepth(record, localDepth);

            // precitaj blok v hesovacom subore na ziskanej adrese
            Block<T> blockToInsertInto = this.readBlockFromFile(blockAddress);

            // ak blok este neexistuje
            if (blockToInsertInto == null) {
                if (this.fullyEmpty != -1) {
                    // ak v subore je nejaky plne prazdny blok, vyuzi ho na vlozenie zaznamu
                    blockToInsertInto = this.readBlockFromFile(this.fullyEmpty);
                    blockAddress = this.fullyEmpty;

                    // kedze sa don vlozi zaznam, uz nebude plne prazdny ... odstran ho zo zretazenia plne prazdnych blokov
                    this.removeFirstFullyEmptyBlockFromChain(blockToInsertInto);
                } else {
                    blockToInsertInto = new Block<T>(super.clusterSize, super.exampleRecord);
                    blockAddress = this.blocksCount;
                    this.blocksCount++;
                }

                int numOfSameDirectoryItems = 1 << (this.fileDepth - localDepth);
                for (int i = 0; i < numOfSameDirectoryItems; i++) {
                    this.directory.set(directoryIndex + i, new DirectoryItem(blockAddress, localDepth, validCount));
                }
            }

            int numOfSameDirectoryItems = 1 << (this.fileDepth - localDepth);

            if (blockToInsertInto.isFull()) {
                for (int i = 0; i < numOfSameDirectoryItems; i++) {
                    this.directory.get(directoryIndex + i).incrementLocalDepth();
                }

                if (localDepth == this.fileDepth) {
                    // blok je uz plny a existuje nan jedina referencia v adresari
                    this.doubleDirectory();
                    // aktualizuj index, na ktory mal ist vkladany zaznam
                    directoryIndex *= 2;
                }
                localDepth++;
                // blok je uz plny a existuje nan viac ako jedna referencia v adresari, takze blok moze byt rozdeleny
                this.splitBlock(blockToInsertInto, blockAddress, localDepth, directoryIndex);
            } else {
                // vloz zaznam do bloku
                blockToInsertInto.insertRecord(record);
                // zapis blok do suboru
                this.writeBlockIntoFile(blockAddress, blockToInsertInto);

                // aktualizuj valid count v adresari
                for (int i = 0; i < numOfSameDirectoryItems; i++) {
                    this.directory.get(directoryIndex + i).setValidCount(blockToInsertInto.getValidCount());
                }

                hasBeenInserted = true;
                System.out.println("Inserted record bitset: " + this.bitSetToString(record.getHash()));
            }
        }
    }

    public String bitSetToString(BitSet bitSet) {
        StringBuilder binary = new StringBuilder();
        for (int i = this.fileDepth - 1; i >= 0; i--) { // Reverse order for MSB first
            binary.append(bitSet.get(i) ? "1" : "0");
        }
        return binary.toString();
    }

    private void doubleDirectory() {
        // zdvojnasob adresovy priestor (D = D + 1)
        this.fileDepth++;

        // zdvojnasob adresar
        ArrayList<DirectoryItem> doubledDirectory = new ArrayList<>(this.directory.size() * 2);
        for (DirectoryItem directoryItem : this.directory) {
            doubledDirectory.add(directoryItem);
            doubledDirectory.add(new DirectoryItem(directoryItem.getBlockAddress(), directoryItem.getLocalDepth(), directoryItem.getValidCount()));
        }
        this.directory = doubledDirectory;
    }

    private void splitBlock(Block<T> oldBlock, int oldBlockAddress, int oldBlockLocalDepth, int directoryIndex) throws IOException {
        Block<T> newBlock = new Block<T>(this.clusterSize, this.exampleRecord);

        ArrayList<T> records = oldBlock.getValidRecords();
        // zrus platnost vsetkych starych zaznamov
        oldBlock.setValidCount(0);

        // opakovane zahesuj zaznamy podla aktualnej hlbky suboru
        // rozdel stare zaznamy podla vybranych bitov z hesovania medzi stary a novy blok
        for (T record : records) {
            int updatedDirectoryIndex = this.getDirectoryIndex(record);
            updatedDirectoryIndex = this.maskDirectoryIndexByLocalDepth(record, oldBlockLocalDepth);
            if (updatedDirectoryIndex == directoryIndex) {
                oldBlock.insertRecord(record);
            } else {
                newBlock.insertRecord(record);
            }
        }

        int numOfSameDirectoryItems = 1 << (this.fileDepth - oldBlockLocalDepth);

        if (oldBlock.isFullyEmpty()) {
            // ak sa vsetky zaznamy presunuli zo stareho bloku do noveho ... stary blok ostal prazdny ... menezuj ho ako prazdny blok
            this.manageFullyEmptyBlock(oldBlockAddress, oldBlock);

            // zneplatni odkazy na prazdny blok v adresari
            for (int i = 0; i < numOfSameDirectoryItems; i++) {
                DirectoryItem item = this.directory.get(directoryIndex + i);
                item.setBlockAddress(-1);
                item.setValidCount(0);
            }
        } else {
            // ak je stary blok neprazdny, aktualizuj ho
            this.writeBlockIntoFile(oldBlockAddress, oldBlock);

            for (int i = 0; i < numOfSameDirectoryItems; i++) {
                this.directory.get(directoryIndex + i).setValidCount(oldBlock.getValidCount());
            }
        }

        int newDirectoryIndex = directoryIndex + numOfSameDirectoryItems;

        if (!newBlock.isFullyEmpty()) {
            int newBlockAddress;

            newBlockAddress = this.getNewBlockAddress();

            // novy blok sa zapise do suboru, len ak nie je prazdny
            this.writeBlockIntoFile(newBlockAddress, newBlock);

            for (int i = 0; i < numOfSameDirectoryItems; i++) {
                DirectoryItem item = this.directory.get(newDirectoryIndex + i);
                item.setBlockAddress(newBlockAddress);
                item.setValidCount(newBlock.getValidCount());
            }
        } else {
            for (int i = 0; i < numOfSameDirectoryItems; i++) {
                DirectoryItem item = this.directory.get(newDirectoryIndex + i);
                item.setBlockAddress(-1);
                item.setValidCount(0);
            }
        }
    }

    private int getNewBlockAddress() throws IOException {
        int newBlockAddress;
        if (this.fullyEmpty != -1) {
            // ak je v subore nejaky prazdny blok, zapis novy blok na jeho miesto
            newBlockAddress = this.fullyEmpty;
            Block<T> firstFullyEmptyBlock = this.readBlockFromFile(this.fullyEmpty);
            this.removeFirstFullyEmptyBlockFromChain(firstFullyEmptyBlock);
        } else {
            // inak zapis novy blok na koniec suboru
            newBlockAddress = this.blocksCount;
            this.blocksCount++;
        }
        return newBlockAddress;
    }

    /**
     * @param recordWithKey dočasný objekt (záznam) s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @return záznam nájdený v hešovacom súbore
     */
    public T get(T recordWithKey) throws IOException {
        Block<T> blockFoundByKey = this.readBlockWithRecord(recordWithKey);

        if (blockFoundByKey == null)
            return null;

        return blockFoundByKey.getRecord(recordWithKey);
    }

    private Block<T> readBlockWithRecord(T recordWithKey) throws IOException {
        if (recordWithKey == null)
            return null;

        if (recordWithKey.getSize() != this.recordSize) {
            throw new IllegalArgumentException("Incorrect size of searched record!");
        }

        int directoryIndex = this.getDirectoryIndex(recordWithKey);
        int blockAddress = this.directory.get(directoryIndex).getBlockAddress();

        return this.readBlockFromFile(blockAddress);
    }

    /**
     * @param recordWithKey záznam s nastaveným kľúčovým atribútom, podľa ktorého vyhľadávame a mažeme
     * @return záznam vymazaný z hešovacieho súboru
     */
    public T delete(T recordWithKey) throws IOException {
        int directoryIndex = this.getDirectoryIndex(recordWithKey);
        int blockAddress = this.directory.get(directoryIndex).getBlockAddress();
        int localDepth = this.directory.get(directoryIndex).getLocalDepth();

        directoryIndex = this.maskDirectoryIndexByLocalDepth(recordWithKey, localDepth);

        Block<T> blockFoundByKey = this.readBlockFromFile(blockAddress);

        if (blockFoundByKey == null)
            return null;

        int blockingFactor = blockFoundByKey.getBlockingFactor();

        // najdi blok a vymaz z neho zaznam
        T deletedRecord = blockFoundByKey.deleteRecord(recordWithKey);

        int numOfSameDirectoryItems = 1 << (this.fileDepth - localDepth);
        for (int i = 0; i < numOfSameDirectoryItems; i++) {
            this.directory.get(directoryIndex + i).setValidCount(blockFoundByKey.getValidCount());
        }

        int validCount = blockFoundByKey.getValidCount();

        boolean canMergeBlocks = true;
        while (canMergeBlocks) {
            int directoryIndexNeighbour = this.getDirectoryIndexNeighbour(recordWithKey, localDepth);

            int localDepthNeighbour = this.directory.get(directoryIndexNeighbour).getLocalDepth();
            if (localDepth != localDepthNeighbour || localDepth == 1) {
                // ak sused nema rovnaku localDepth, nie je to sused ... blok nema suseda
                // alebo ak ma blok hlbku 1, nezluc ho so susedom (vzdy ostanu aspon 2 polozky v adresari)

                if (blockFoundByKey.isFullyEmpty()) {
                    // blok bez suseda po vymazani zaznamu ostane prazdny => sprava prazdneho bloku
                    this.manageFullyEmptyBlock(blockAddress, blockFoundByKey);

                    // zneplatni odkazy na prazdny blok
                    numOfSameDirectoryItems = 1 << (this.fileDepth - localDepth);
                    for (int i = 0; i < numOfSameDirectoryItems; i++) {
                        DirectoryItem item = this.directory.get(directoryIndex + i);
                        item.setBlockAddress(-1);
                        item.setValidCount(0);
                    }
                } else {
                    // bez reorganizacie, aktualizuj obsah bloku
                    this.writeBlockIntoFile(blockAddress, blockFoundByKey);
                }
                canMergeBlocks = false;
            } else {
                // nasiel sa sused, skontroluj jeho pocet zaznamov
                int validCountNeighbour = this.directory.get(directoryIndexNeighbour).getValidCount();

                if (validCount + validCountNeighbour <= blockingFactor) {
                    // ak po zruseni zostane v susednych blokoch iba tolko zaznamov, ze sa zmestia do jedineho bloku
                    // presun zaznamy do jedineho bloku a bloky "spoj"
                    this.mergeBlocks(recordWithKey, blockFoundByKey, blockAddress, localDepth, directoryIndex, directoryIndexNeighbour);

                    boolean noMoreBlocksWithFileDepth = this.areNoMoreBlocksWithFileDepth();

                    // skontroluj, ci sa znizila hlbka celeho suboru a adresar sa moze zmensit
                    if (noMoreBlocksWithFileDepth) {
                        // ak uz ziadny blok nema localDepth == this.fileDepth
                        // zniz hlbku suboru, zmensi adresar
                        this.halveDirectory();

                        // ak bol zmenseny adresar, opakovane skontroluj, ci nie je mozne spojit bloky
                        directoryIndex = this.getDirectoryIndex(recordWithKey);
                        blockAddress = this.directory.get(directoryIndex).getBlockAddress();
                        localDepth = this.directory.get(directoryIndex).getLocalDepth();
                        directoryIndex = this.maskDirectoryIndexByLocalDepth(recordWithKey, localDepth);
                        blockFoundByKey = this.readBlockFromFile(blockAddress);
                        if (blockFoundByKey == null)
                            return null;
                        validCount = blockFoundByKey.getValidCount();
                    } else {
                        canMergeBlocks = false;
                    }

                } else {
                    // po zruseni zostane spolu so susedom viac zaznamov ako je blokovaci faktor
                    // bez reorganizacie
                    this.writeBlockIntoFile(blockAddress, blockFoundByKey);
                    canMergeBlocks = false;
                }
            }
        }

        return deletedRecord;
    }

    private void mergeBlocks(T recordWithKey, Block<T> blockFoundByKey, int blockAddress, int localDepth, int directoryIndex, int directoryIndexNeighbour) throws IOException {
        // zniz localDepth
        localDepth--;

        int directoryIndexMoveTo = this.maskDirectoryIndexByLocalDepth(recordWithKey, localDepth);
        int blockAddressMoveTo = this.directory.get(directoryIndexMoveTo).getBlockAddress();
        int blockAddressNeighbour = this.directory.get(directoryIndexNeighbour).getBlockAddress();

        // presun zaznamy do jedineho bloku
        int blockAddressMoveFrom;
        Block<T> moveFromBlock;
        Block<T> moveToBlock;
        if (directoryIndexMoveTo == directoryIndex) {
            blockAddressMoveFrom = blockAddressNeighbour;
            moveFromBlock = this.readBlockFromFile(blockAddressNeighbour);
            moveToBlock = blockFoundByKey;
        } else {
            blockAddressMoveFrom = blockAddress;
            moveFromBlock = blockFoundByKey;
            moveToBlock = this.readBlockFromFile(blockAddressMoveTo);
        }

        if (moveFromBlock == null) {
            moveFromBlock = new Block<T>(this.clusterSize, this.exampleRecord);
        }
        if (moveToBlock == null) {
            moveToBlock = new Block<T>(this.clusterSize, this.exampleRecord);
        }

        ArrayList<T> recordsToMove = moveFromBlock.getValidRecords();
        // zrus platnost vsetkych starych zaznamov
        moveFromBlock.setValidCount(0);

        // presun zaznamy
        for (T record : recordsToMove) {
            moveToBlock.insertRecord(record);
        }

        if (blockAddressMoveTo == -1) {
            blockAddressMoveTo = getNewBlockAddress();
        }
        // zapis zmeny
        this.writeBlockIntoFile(blockAddressMoveTo, moveToBlock);

        if (blockAddressMoveFrom != -1) {
            // uvolni prazdny blok
            this.manageFullyEmptyBlock(blockAddressMoveFrom, moveFromBlock);
        }   // inak prazdny blok moveFrom nie je zapisany v subore, ale je vytvoreny len v operacnej pamati a netreba ho menezovat

        int numOfSameDirectoryItems = 1 << (this.fileDepth - localDepth);
        for (int i = 0; i < numOfSameDirectoryItems; i++) {
            DirectoryItem item = this.directory.get(directoryIndexMoveTo + i);
            item.setBlockAddress(blockAddressMoveTo);
            item.setLocalDepth(localDepth);
            item.setValidCount(moveToBlock.getValidCount());
        }
    }

    private boolean areNoMoreBlocksWithFileDepth() {
        boolean noMoreBlocksWithFileDepth = true;
        for (DirectoryItem item : this.directory) {
            if (item.getLocalDepth() == this.fileDepth) {
                noMoreBlocksWithFileDepth = false;
                break;
            }
        }
        return noMoreBlocksWithFileDepth;
    }

    private void halveDirectory() {
        // zmensi adresovy priestor (D = D - 1)
        this.fileDepth--;

        // zmensi adresar na polovicu
        ArrayList<DirectoryItem> halvedDirectory = new ArrayList<>(this.directory.size() / 2);

        // prekopiruj iba kazdu druhu polozku v adresari
        for (int i = 0; i < this.directory.size(); i += 2) {
            halvedDirectory.add(this.directory.get(i));
        }

        this.directory = halvedDirectory;
    }

    public T update(T oldRecordWithKey, T newRecord) throws IOException {
        Block<T> foundBlockToUpdate = this.readBlockWithRecord(oldRecordWithKey);

        if (foundBlockToUpdate == null || foundBlockToUpdate.isFullyEmpty())
            return null;

        if (foundBlockToUpdate.getRecord(oldRecordWithKey) == null)
            return null;

        T oldRecord;
        if (oldRecordWithKey.isEqualTo(newRecord)) {
            // ak sa nezmenil klucovy atribut
            oldRecord = foundBlockToUpdate.updateRecord(oldRecordWithKey, newRecord);

            int directoryIndex = this.getDirectoryIndex(oldRecordWithKey);
            int blockAddress = this.directory.get(directoryIndex).getBlockAddress();

            // zapis blok s aktualizovanym obsahom
            this.writeBlockIntoFile(blockAddress, foundBlockToUpdate);
        } else {
            // ak sa zmenil klucovy atribut
            oldRecord = this.delete(oldRecordWithKey);
            this.insert(newRecord);
        }

        return oldRecord;
    }

    @Override
    public void close(String fileName) {
        try (PrintWriter writer = super.writeControlInfo(fileName)) {
            writer.println(this.fileDepth);

            for (DirectoryItem directoryItem : this.directory) {
                writer.println(directoryItem.toCSV());
            }

            writer.close();
            this.file.close();
        } catch (IOException e) {
            throw new RuntimeException("Error during extendible hash file closing!");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (DirectoryItem item : this.directory) {
            sb.append("\n\t").append(item);
        }

        return "ExtendibleHashFile{" +
                "fileDepth=" + fileDepth +
                ", clusterSize=" + clusterSize +
                ", recordSize=" + recordSize +
                ", fullyEmpty=" + fullyEmpty +
                ", blocksCount=" + blocksCount +
                ", fileName='" + fileName + '\'' +
                ",\ndirectory=" + sb +
                '}';
    }
}

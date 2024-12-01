package ExtendibleHashFile;

import FileDataStructure.*;
import Tools.BitSetUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;

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
        this.directory.add(new DirectoryItem(-1, 1));
        this.directory.add(new DirectoryItem(-1, 1));
    }

    public void clear() throws IOException {
        super.clear();
        this.fileDepth = 1;
        this.directory = new ArrayList<>();
        this.directory.add(new DirectoryItem(-1, 1));
        this.directory.add(new DirectoryItem(-1, 1));
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
            int blockAddress = directoryItem.getAddress();
            int localDepth = directoryItem.getLocalDepth();

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

                this.directory.set(directoryIndex, new DirectoryItem(blockAddress, localDepth));
            }

            if (blockToInsertInto.isFull()) {
                for (DirectoryItem item : this.directory) {
                    if (item.getAddress() == blockAddress) {
                        item.incrementLocalDepth();
                    }
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
            doubledDirectory.add(new DirectoryItem(directoryItem.getAddress(), directoryItem.getLocalDepth()));
        }
        this.directory = doubledDirectory;

        // aktualizuj adresy v adresari

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
            if (updatedDirectoryIndex == directoryIndex) {
                oldBlock.insertRecord(record);
            } else {
                newBlock.insertRecord(record);
            }
        }

        if (this.blocksCount != 0) {
            if (oldBlock.isFullyEmpty()) {
                // ak sa vsetky zaznamy presunuli zo stareho bloku do noveho ... stary blok ostal prazdny ... menezuj ho ako prazdny blok
                this.manageFullyEmptyBlock(oldBlockAddress, oldBlock);
            } else {
                // ak je stary blok neprazdny, aktualizuj ho
                this.writeBlockIntoFile(oldBlockAddress, oldBlock);
            }
        }

        int numOfSameDirectoryItems = 1 << (this.fileDepth - oldBlockLocalDepth);
        int newDirectoryIndex = directoryIndex + numOfSameDirectoryItems;

        if (!newBlock.isFullyEmpty()) {
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

            // novy blok sa zapise do suboru, len ak nie je prazdny
            this.writeBlockIntoFile(newBlockAddress, newBlock);

            for (int i = 0; i < numOfSameDirectoryItems; i++) {
                this.directory.get(newDirectoryIndex + i).setAddress(newBlockAddress);
            }
        } else {
            for (int i = 0; i < numOfSameDirectoryItems; i++) {
                this.directory.get(newDirectoryIndex + i).setAddress(-1);
            }
        }
    }

    /**
     * @param recordWithKey dočasný objekt (záznam) s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @return záznam nájdený v hešovacom súbore
     */
    public T get(T recordWithKey) throws IOException {
        int directoryIndex = this.getDirectoryIndex(recordWithKey);
        int blockAddress = this.directory.get(directoryIndex).getAddress();

        Block<T> blockFoundByKey = this.readBlockFromFile(blockAddress);

        if (blockFoundByKey == null)
            return null;

        return blockFoundByKey.getRecord(recordWithKey);
    }

    /**
     * @param recordWithKey záznam s nastaveným kľúčovým atribútom, podľa ktorého vyhľadávame a mažeme
     * @return záznam vymazaný z hešovacieho súboru
     */
    public T delete(T recordWithKey) {
        return null;
    }
}

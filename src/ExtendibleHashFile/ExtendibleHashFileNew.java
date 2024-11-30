package ExtendibleHashFile;

import FileDataStructure.*;
import Tools.BitSetUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

public class ExtendibleHashFileNew<T extends IHashData<T>> extends FileDataStructure<T> {
    private int fileDepth; // D
    // adresár sa nachádza v operačnej pamäti
    // je to jednorozmerné pole adries (celočíselných hodnôt)
    private ArrayList<DirectoryItem> directory;

    public ExtendibleHashFileNew(String fileName, int clusterSize, T record) {
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

    private int getDirectoryIndex(T record) {
        BitSet hash = record.getHash();

        // z vysledku hesovania vezmi prvych D bitov
        BitSet firstDBits = new BitSet(this.fileDepth);
        for (int i = 0; i < this.fileDepth; i++) {
            if (hash.get(i))
                firstDBits.set(i);
        }

        // tieto bity prekonvertuj BIN -> DEC
        // vysledkom je index v adresari, na ktorom je ulozena hladana adresa
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

            for (int i = 0; i < this.directory.size(); i++) {
                if (this.directory.get(i).getAddress() == blockAddress) {
                    directoryIndex = i;
                    break;
                }
            }

            // precitaj blok v hesovacom subore na ziskanej adrese
            Block<T> blockToInsertInto = this.readBlockFromFile(blockAddress);

            // ak blok este neexistuje
            if (blockToInsertInto == null) {
                blockToInsertInto = new Block<T>(super.clusterSize, super.exampleRecord);
                blockAddress = this.blocksCount;
                this.blocksCount++;

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
            }
        }
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

        this.writeBlockIntoFile(oldBlockAddress, oldBlock);

        int numOfSameDirectoryItems = 1 << (this.fileDepth - oldBlockLocalDepth);
        int newDirectoryIndex = directoryIndex + numOfSameDirectoryItems;

        if (!newBlock.isFullyEmpty()) {
            int newBlockAddress = this.blocksCount;
            this.blocksCount++;
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
     * @param recordWithKey záznam s nastaveným kľúčovým atribútom, podľa ktorého vyhľadávame
     * @return záznam nájdený v hešovacom súbore
     */
    public T get(T recordWithKey) {
        return null;
    }

    /**
     * @param recordWithKey záznam s nastaveným kľúčovým atribútom, podľa ktorého vyhľadávame a mažeme
     * @return záznam vymazaný z hešovacieho súboru
     */
    public T delete(T recordWithKey) {
        return null;
    }
}

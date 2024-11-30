//package ExtendibleHashFile;
//
//import FileDataStructure.Block;
//import FileDataStructure.IData;
//import Tools.BitSetUtility;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.BitSet;
//
//public class ExtendibleHashFile<T extends IData<T>> {
//    private int fileDepth; // D
//    private ArrayList<Integer> directory;
//    private ArrayList<Integer> blockDepths;
//
//    public ExtendibleHashFile(String fileName, int clusterSize, T record) {
//
//        // v prazdnom subore D = 1
//        this.fileDepth = 1;
//        this.directory = new ArrayList<>();
//        this.blockDepths = new ArrayList<>();
//
//        // na zaciatku adresar obsahuje 2 polozky
//            // obe polozky ukazuju na zaciatok suboru (adresa 0)
//        this.directory.add(0);
//        this.directory.add(0);
//
//        // oba bloky maju hlbku d = 1
//        this.blockDepths.add(1);
//        this.blockDepths.add(1);
//    }
//
//    /**
//     * @param record vkladané dáta, ktoré je možné na základe kľúča priamo hešovať
//     * @return adresa bloku, do ktorého bol záznam vložený
//     */
//    public int insert(T record) throws IOException {
//        boolean hasBeenInserted = false;
//
//        while (!hasBeenInserted) {
//            int directoryIndex = this.getDirectoryIndex(record);
//            // ziskaj adresu bloku, do ktoreho sa ma zaznam vlozit
//            int blockAddress = this.directory.get(directoryIndex);
//            int blockDepth = this.blockDepths.get(directoryIndex);
//
//            // precitaj blok v subore na ziskanej adrese
//            Block<T> blockToInsertInto = super.readBlockFromFile(blockAddress);
//
//            // ak blok este neexistuje
//            if (blockToInsertInto == null) {
//                blockToInsertInto = new Block<T>(super.clusterSize, super.exampleRecord);
//                blockToInsertInto.insertRecord(record);
//                super.insert(record);
//                break;
//            }
//
//            if (blockToInsertInto.isFull()) {
//                // ak je dany blok uz plny ... alokuj novy blok
//                if (blockDepth == this.fileDepth) {
//                    // zdvojnasob adresar
//                    this.doubleDirectory(directoryIndex);
//                    this.doubleBlockDepths(directoryIndex);
//                }
//
//                int newBlockDepth = blockDepth + 1;
//                // ak sa zdvojnasobil adresar, ziskany index v adresari bude vypocitany z navyseneho poctu bitov; inac zostane rovnaky
//                int newDirectoryIndex = this.getDirectoryIndex(record);
//                // rozdel blok pomocou split a uprav adresy blokov v adresari
//                this.splitBlock(blockAddress, newDirectoryIndex, newBlockDepth);
//            } else {
//                // ak dany blok este obsahuje prazdne miesto ... vloz don zaznam
//                blockToInsertInto.insertRecord(record);
//                super.insert(record);
//                hasBeenInserted = true;
//            }
//        }
//
//        return this.directory.get(this.getDirectoryIndex(record));
//    }
//
//    private void splitBlock(int oldBlockAddress, int newDirectoryIndex, int newBlockDepth) throws IOException {
//        Block<T> oldBlock = super.readBlockFromFile(oldBlockAddress);
//        Block<T> newBlock = new Block<T>(super.clusterSize, super.exampleRecord);
//
//        ArrayList<T> records = oldBlock.getValidRecords();
//        // zrus platnost vsetkych starych zaznamov
//        oldBlock.setValidCount(0);
//
//        // rozdel stare zaznamy podla vybranych bitov z hesovania medzi stary a novy blok
//        for (T record : records) {
//            int directoryIndex = this.getDirectoryIndex(record);
//            if (directoryIndex == newDirectoryIndex) {
//                newBlock.insertRecord(record);
//            } else {
//                oldBlock.insertRecord(record);
//            }
//        }
//
//        int newBlockAddress = super.blocksCount;
//        super.blocksCount++;
//
//        super.writeBlockIntoFile(oldBlockAddress, oldBlock);
//        super.writeBlockIntoFile(newBlockAddress, newBlock);
//
//        this.directory.set(newDirectoryIndex, newBlockAddress);
//        this.blockDepths.set(newDirectoryIndex, newBlockDepth);
//    }
//
//    private void doubleBlockDepths(int oldDirectoryIndex) {
//        ArrayList<Integer> doubledBlockDepths = new ArrayList<>(this.blockDepths.size() * 2);
//        for (int oldBlockDepth : this.blockDepths) {
//            doubledBlockDepths.add(oldBlockDepth);
//            doubledBlockDepths.add(oldBlockDepth);
//        }
//        this.blockDepths = doubledBlockDepths;
//    }
//
//    private void doubleDirectory(int directoryIndex) {
//        this.fileDepth++;
//
//        ArrayList<Integer> doubledDirectory = new ArrayList<>(this.directory.size() * 2);
//        for (Integer integer : this.directory) {
//            doubledDirectory.add(integer);
//            doubledDirectory.add(integer);
//        }
//        this.directory = doubledDirectory;
//    }
//
//    /**
//     * @param blockAddress parameter nemusí byť validný, v metóde sa jeho hodnota ignoruje
//     * @param recordWithKey dočasný objekt (záznam) s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
//     * @return nájdený záznam
//     */
//    public T get(T recordWithKey) throws IOException {
//        int directoryIndex = this.getDirectoryIndex(recordWithKey);
//        blockAddress = this.directory.get(directoryIndex);
//
//        Block<T> blockFoundByKey = super.readBlockFromFile(blockAddress);
//
//        if (blockFoundByKey == null)
//            return null;
//
//        return blockFoundByKey.getRecord(recordWithKey);
//    }
//}

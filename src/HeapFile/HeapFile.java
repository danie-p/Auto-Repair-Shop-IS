package HeapFile;

import FileDataStructure.*;

import java.io.*;

public class HeapFile<T extends IData<T>> extends FileDataStructure<T> {
    // adresa prveho ciastocne volneho bloku (zaciatok zretazenia ciastocne volnych blokov)
    private int partiallyEmpty;

    public HeapFile(String fileName, int clusterSize, T record) {
        super(fileName, clusterSize, record);
        this.partiallyEmpty = -1;
    }

    public void clear() throws IOException {
        super.clear();
        this.partiallyEmpty = -1;
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
                    this.removeFirstPartiallyEmptyBlockFromChain(blockToInsertInto);
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
                // v kazdom pripade ho odstran zo (zaciatku) zretazenia plne prazdnych blokov a nahrad ho nasledovnikom
                this.removeFirstFullyEmptyBlockFromChain(blockToInsertInto);

                if (blockToInsertInto.isPartiallyEmpty()) {
                    // ak je po vlozeni zaznamu ciastocne prazdny, pripoj ho do zretazenia ciastocne prazdnych blokov na PRVE miesto
                    // zretazenie ciastocne prazdnych blokov je doteraz urcite prazdne, kedze blok bol vlozeny az do plne volneho bloku
                    this.partiallyEmpty = indexOfBlockToInsertInto;
                    blockToInsertInto.setNext(-1);
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
        Block<T> foundBlock = this.readBlockWithRecord(blockAddress, recordWithKey);

        if (foundBlock == null)
            return null;

        // podla ID ziskaj konkretny hladany zaznam z najdeneho nacitaneho bloku
        return foundBlock.getRecord(recordWithKey);
    }

    public Block<T> readBlockWithRecord(int blockAddress, T recordWithKey) throws IOException {
        if (recordWithKey.getSize() != this.recordSize) {
            throw new IllegalArgumentException("Incorrect size of searched record!");
        }

        if (blockAddress >= this.blocksCount || blockAddress < 0) {
            return null;
        }

        return this.readBlockFromFile(blockAddress);
    }

    /**
     * @param blockAddress adresa (index) bloku uchovávajúceho hľadaný záznam na editáciu
     * @param oldRecordWithKey dočasný objekt (záznam) s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @param newRecord záznam s úplne nastavenými hodnotami, ktoré sa majú editovať
     * @return pôvodný záznam, ktorý bol editovaný
     */
    public T update(int blockAddress, T oldRecordWithKey, T newRecord) throws IOException {
        Block<T> foundBlockToUpdate = this.readBlockWithRecord(blockAddress, oldRecordWithKey);

        if (foundBlockToUpdate == null || foundBlockToUpdate.isFullyEmpty())
            return null;

        T oldRecord = foundBlockToUpdate.updateRecord(oldRecordWithKey, newRecord);

        // TODO: upravit, aby update spravne fungoval aj nad klucovymi atributmi (delete + insert)

        // ak bol editovany neklucovy atribut, zapis blok s aktualizovanym obsahom
        this.writeBlockIntoFile(blockAddress, foundBlockToUpdate);

        return oldRecord;
    }

    /**
     * @param blockAddress adresa (index) bloku uchovávajúceho hľadaný záznam na mazanie
     * @param recordWithKey dočasný objekt (záznam) s nastaveným unikátnym atribútom pre použitie v metóde isEqualTo
     * @return vymazaný záznam
     */
    public T delete(int blockAddress, T recordWithKey) throws IOException {
        Block<T> foundBlockToDelete = this.readBlockWithRecord(blockAddress, recordWithKey);

        if (foundBlockToDelete == null || foundBlockToDelete.isFullyEmpty())
            return null;

        T deletedRecord = foundBlockToDelete.deleteRecord(recordWithKey);

        if (foundBlockToDelete.isFull()) {
            // ak bol blok pred vymazanim zaznamu plny

            if (foundBlockToDelete.isPartiallyEmpty()) {
                // ak blok ostane po vymazani zaznamu ciastocne prazdny, pridaj ho do zretazenia ciastocne prazdnych blokov (na zaciatok)
                this.setFirstBlockInChain(blockAddress, foundBlockToDelete, this.partiallyEmpty);
                // aktualizuj zaciatok zretazenia ciastocne prazdnych blokov
                this.partiallyEmpty = blockAddress;
            } else {
                // ak blok ostane po vymazani zaznamu prazdny
                this.manageFullyEmptyBlock(blockAddress, foundBlockToDelete);
            }
        } else {
            // ak bol blok pred vymazanim zaznamu ciastocne prazdny

            if (foundBlockToDelete.isFullyEmpty()) {
                // odstran zo zretazenia ciastocne prazdnych blokov
                this.removeEmptyBlockFromChain(foundBlockToDelete);

                // ak bol prvy v zretazeni ciastocne prazdnych blokov, nastav novy prvy
                if (this.partiallyEmpty == blockAddress)
                    this.partiallyEmpty = foundBlockToDelete.getNext();

                this.manageFullyEmptyBlock(blockAddress, foundBlockToDelete);
            } else {
                // inak ak blok po vymazani zaznamu stale ostane ciastocne prazdny, netreba robit manazment volnych blokov
                // ale je potrebne aktualizovat obsah bloku, z ktoreho sa vymazal zaznam
                this.writeBlockIntoFile(blockAddress, foundBlockToDelete);
            }
        }

        return deletedRecord;
    }

    private void removeFirstPartiallyEmptyBlockFromChain(Block<T> blockToRemoveFromFullyEmptyChain) throws IOException {
        // odstran blok zo zretazenia ciastocne prazdnych blokov
        this.partiallyEmpty = blockToRemoveFromFullyEmptyChain.getNext();
        blockToRemoveFromFullyEmptyChain.setNext(-1);

        // aktualizuj nahradnika
        this.updateNextBlock(this.partiallyEmpty);
    }

    /**
     * Metóda na zatvorenie súboru. Je potrebné ju zavolať pre korektné ukončenie práce so súborom.
     */
    public void close() {
        try (BufferedWriter writer = super.writeControlInfo()) {
            writer.write("Partially_empty: " + this.partiallyEmpty);
            writer.newLine();

            this.file.close();
        } catch (IOException e) {
            throw new RuntimeException("Error during heap file closing!");
        }
    }
}

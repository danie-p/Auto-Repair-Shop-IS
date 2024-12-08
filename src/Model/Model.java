package Model;

import ExtendibleHashFile.*;
import FileDataStructure.Block;
import HeapFile.HeapFile;

import java.io.IOException;

public class Model {
    private final HeapFile<Vehicle> heapFileVehicles;
    private final ExtendibleHashFile<VehicleByCustomerID> extHashFileByID;
    private final ExtendibleHashFile<VehicleByLicensePlate> extHashFileByLP;
    private final String controlHeapFileName;
    private final String controlHashFileByIDName;
    private final String controlHashFileByLPName;

    public Model(int clusterSize,
                 String heapFileName, String extHashFileByIDName, String extHashFileByLPName,
                 String controlHeapFileName, String controlHashFileByIDName, String controlHashFileByLPName) {
        Vehicle exampleVehicle = new Vehicle("", "", -1, "", null);
        VehicleByCustomerID exampleVehicleByID = new VehicleByCustomerID(-1, -1, "");
        VehicleByLicensePlate exampleVehicleByLP = new VehicleByLicensePlate(-1, "", -1);

        this.controlHeapFileName = controlHeapFileName;
        this.controlHashFileByIDName = controlHashFileByIDName;
        this.controlHashFileByLPName = controlHashFileByLPName;

        this.heapFileVehicles = HeapFile.fromFile(this.controlHeapFileName, heapFileName, clusterSize, exampleVehicle);
        this.extHashFileByID = ExtendibleHashFile.fromFile(this.controlHashFileByIDName, extHashFileByIDName, clusterSize, exampleVehicleByID);
        this.extHashFileByLP = ExtendibleHashFile.fromFile(this.controlHashFileByLPName, extHashFileByLPName, clusterSize, exampleVehicleByLP);
    }

    public void clearData() throws IOException {
        this.heapFileVehicles.clear();
        this.extHashFileByID.clear();
        this.extHashFileByLP.clear();
    }

    // 1. Vyhľadanie všetkých evidovaných údajov o vozidle (vozidlo sa vyhľadá podľa id zákazníka
    // alebo podľa EČV – vyberie si užívateľ)
    public Vehicle getVehicleByID(int idToSearchBy) throws IOException {
        VehicleByCustomerID tempHashRecord = new VehicleByCustomerID(-1, idToSearchBy, "");
        VehicleByCustomerID foundHashRecord = this.extHashFileByID.get(tempHashRecord);

        if (foundHashRecord != null) {
            int heapBlockAddress = foundHashRecord.getBlockAddress();
            Vehicle tempHeapRecord = new Vehicle("", "", foundHashRecord.getKeyCustomerID(), "", null);

            return this.heapFileVehicles.get(heapBlockAddress, tempHeapRecord);
        }

        return null;
    }

    public Vehicle getVehicleByLP(String lpToSearchBy) throws IOException {
        VehicleByLicensePlate tempHashRecord = new VehicleByLicensePlate(-1, lpToSearchBy, -1);
        VehicleByLicensePlate foundHashRecord = this.extHashFileByLP.get(tempHashRecord);

        if (foundHashRecord != null) {
            int heapBlockAddress = foundHashRecord.getBlockAddress();
            Vehicle tempHeapRecord = new Vehicle("", "", -1, foundHashRecord.getKeyLicensePlateCode(), null);

            return this.heapFileVehicles.get(heapBlockAddress, tempHeapRecord);
        }

        return null;
    }

    // 2. Pridanie vozidla – na základe zadaných údajov zaradí vozidlo do evidencie (pozor na ošetrenie
    // unikátnosti niektorých údajov)
    public void insertVehicle(Vehicle vehicle) throws IOException {
        int heapBlockAddress = this.heapFileVehicles.insert(vehicle);

        VehicleByCustomerID vehicleByCustomerID = new VehicleByCustomerID(heapBlockAddress, vehicle.getCustomerID(), vehicle.getLicensePlateCode());
        this.extHashFileByID.insert(vehicleByCustomerID);

        VehicleByLicensePlate vehicleByLicensePlate = new VehicleByLicensePlate(heapBlockAddress, vehicle.getLicensePlateCode(), vehicle.getCustomerID());
        this.extHashFileByLP.insert(vehicleByLicensePlate);
    }

    // 3. Pridanie návštevy servisu - na základe zadaných údajov pridá návštevu servisu do evidencie
    // (vozidlo sa vyhľadá podľa id zákazníka alebo podľa EČV – vyberie si užívateľ)
    /**
     * @return vozidlo, ku ktorému bola pridaná návšteva servisu; vracia null, ak sa pridanie nepodarilo
     */
    public Vehicle insertServiceVisitByID(ServiceVisit serviceVisit, int idToSearchBy) throws IOException {
        VehicleByCustomerID tempHashRecord = new VehicleByCustomerID(-1, idToSearchBy, "");
        VehicleByCustomerID foundHashRecord = this.extHashFileByID.get(tempHashRecord);

        if (foundHashRecord != null) {
            int heapBlockAddress = foundHashRecord.getBlockAddress();
            Vehicle tempHeapRecord = new Vehicle("", "", foundHashRecord.getKeyCustomerID(), "", null);

            return this.insertServiceVisitHelper(serviceVisit, heapBlockAddress, tempHeapRecord);
        }

        return null;
    }

    /**
     * @return vozidlo, ku ktorému bola pridaná návšteva servisu; vracia null, ak sa pridanie nepodarilo
     */
    public Vehicle insertServiceVisitByLP(ServiceVisit serviceVisit, String lpToSearchBy) throws IOException {
        VehicleByLicensePlate tempHashRecord = new VehicleByLicensePlate(-1, lpToSearchBy, -1);
        VehicleByLicensePlate foundHashRecord = this.extHashFileByLP.get(tempHashRecord);

        if (foundHashRecord != null) {
            int heapBlockAddress = foundHashRecord.getBlockAddress();
            Vehicle tempHeapRecord = new Vehicle("", "", -1, foundHashRecord.getKeyLicensePlateCode(), null);

            return this.insertServiceVisitHelper(serviceVisit, heapBlockAddress, tempHeapRecord);
        }

        return null;
    }

    private Vehicle insertServiceVisitHelper(ServiceVisit serviceVisit, int heapBlockAddress, Vehicle tempHeapRecord) throws IOException {
        Block<Vehicle> blockWithVehicleToUpdate = this.heapFileVehicles.readBlockWithRecord(heapBlockAddress, tempHeapRecord);

        if (blockWithVehicleToUpdate == null)
            return null;

        Vehicle vehicleToUpdate = blockWithVehicleToUpdate.getRecord(tempHeapRecord);

        if (vehicleToUpdate == null)
            return null;

        if (!vehicleToUpdate.addServiceVisit(serviceVisit))
            // navstevu servisu nebolo mozne pridat, pretoze u daneho vozidla uz je zapisany max pocet navstev
            return null;

        // navstevu servisu sa podarilo pridat
        // aktualizuj obsah bloku v heap file
        this.heapFileVehicles.writeBlockIntoFile(heapBlockAddress, blockWithVehicleToUpdate);

        return vehicleToUpdate;
    }

    // 4. Zmena – umožní zmeniť akékoľvek evidované údaje (vozidlo sa vyhľadá podľa id zákazníka
    // alebo podľa EČV – vyberie si užívateľ), pozor na zmenu kľúčových a nekľúčových atribútov
    /**
     * @param oldVehicle dočasný záznam vozidla s nastavenými kľúčovými atribútmi (customer ID, license plate)
     * @param newVehicle záznam vozidla s úplne nastavenými hodnotami, ktoré sa majú aktualizovať
     * @return pôvodný záznam, ktorý bol editovaný
     */
    public Vehicle updateVehicle(Vehicle oldVehicle, Vehicle newVehicle) throws IOException {
        VehicleByCustomerID tempHashRecord = new VehicleByCustomerID(-1, oldVehicle.getCustomerID(), oldVehicle.getLicensePlateCode());
        VehicleByCustomerID foundHashRecord = this.extHashFileByID.get(tempHashRecord);

        if (foundHashRecord != null) {
            int heapBlockAddress = foundHashRecord.getBlockAddress();

            Vehicle updatedRecord = this.heapFileVehicles.update(heapBlockAddress, oldVehicle, newVehicle);

            VehicleByCustomerID newHashRecord = new VehicleByCustomerID(foundHashRecord.getBlockAddress(), newVehicle.getCustomerID(), newVehicle.getLicensePlateCode());
            this.extHashFileByID.update(tempHashRecord, newHashRecord);

            VehicleByLicensePlate tempHashRecordByLP = new VehicleByLicensePlate(-1, oldVehicle.getLicensePlateCode(), oldVehicle.getCustomerID());
            VehicleByLicensePlate newHashRecordByLP = new VehicleByLicensePlate(foundHashRecord.getBlockAddress(), newVehicle.getLicensePlateCode(), newVehicle.getCustomerID());
            this.extHashFileByLP.update(tempHashRecordByLP, newHashRecordByLP);

            return updatedRecord;
        }

        return null;
    }

    // 5. Zmazanie návštevy servisu – umožní zmazať akékoľvek evidované údaje (vozidlo sa vyhľadá
    // podľa id zákazníka alebo podľa EČV – vyberie si užívateľ)
    public Vehicle removeServiceVisitFromVehicle(Vehicle vehicle, int serviceVisitIndex) throws IOException {
        VehicleByCustomerID tempHashRecord = new VehicleByCustomerID(-1, vehicle.getCustomerID(), "");
        VehicleByCustomerID foundHashRecord = this.extHashFileByID.get(tempHashRecord);

        if (foundHashRecord != null) {
            int heapBlockAddress = foundHashRecord.getBlockAddress();

            Block<Vehicle> blockWithVehicleToUpdate = this.heapFileVehicles.readBlockWithRecord(heapBlockAddress, vehicle);

            if (blockWithVehicleToUpdate == null)
                return null;

            Vehicle vehicleToUpdate = blockWithVehicleToUpdate.getRecord(vehicle);

            if (vehicleToUpdate == null)
                return null;

            vehicleToUpdate.removeServiceVisit(serviceVisitIndex);

            // navstevu servisu sa podarilo odstranit
            // aktualizuj obsah bloku v heap file
            this.heapFileVehicles.writeBlockIntoFile(heapBlockAddress, blockWithVehicleToUpdate);

            return vehicleToUpdate;
        }

        return null;
    }

    // 6. Zmazanie vozidla – umožní zmazať všetky údaje o vozidle (vozidlo sa vyhľadá podľa id
    // zákazníka alebo podľa EČV – vyberie si užívateľ)
    public Vehicle deleteVehicleByID(int customerID) throws IOException {
        VehicleByCustomerID tempHashRecord = new VehicleByCustomerID(-1, customerID, "");
        VehicleByCustomerID deletedHashRecord = this.extHashFileByID.delete(tempHashRecord);

        if (deletedHashRecord != null) {
            VehicleByLicensePlate tempHashRecordByLP = new VehicleByLicensePlate(deletedHashRecord.getBlockAddress(), deletedHashRecord.getLicensePlateCode(), deletedHashRecord.getKeyCustomerID());
            this.extHashFileByLP.delete(tempHashRecordByLP);

            int heapBlockAddress = deletedHashRecord.getBlockAddress();
            Vehicle tempHeapRecord = new Vehicle("", "", deletedHashRecord.getKeyCustomerID(), "", null);

            return this.heapFileVehicles.delete(heapBlockAddress, tempHeapRecord);
        }

        return null;
    }

    public Vehicle deleteVehicleByLP(String licensePlate) throws IOException {
        VehicleByLicensePlate tempHashRecord = new VehicleByLicensePlate(-1, licensePlate, -1);
        VehicleByLicensePlate deletedHashRecord = this.extHashFileByLP.delete(tempHashRecord);

        if (deletedHashRecord != null) {
            VehicleByCustomerID tempHashRecordByID = new VehicleByCustomerID(deletedHashRecord.getBlockAddress(), deletedHashRecord.getCustomerID(), deletedHashRecord.getKeyLicensePlateCode());
            this.extHashFileByID.delete(tempHashRecordByID);

            int heapBlockAddress = deletedHashRecord.getBlockAddress();
            Vehicle tempHeapRecord = new Vehicle("", "", -1, deletedHashRecord.getKeyLicensePlateCode(), null);

            return this.heapFileVehicles.delete(heapBlockAddress, tempHeapRecord);
        }

        return null;
    }

    public String readHeapFileSequentially() throws IOException {
        return this.heapFileVehicles.readSequentially();
    }

    public String readExtHashFileByIDSequentially() throws IOException {
        return this.extHashFileByID.readSequentially();
    }

    public String readExtHashFileByLPSequentially() throws IOException {
        return this.extHashFileByLP.readSequentially();
    }

    public String getHeapFileControlInfo() {
        return this.heapFileVehicles.toString();
    }

    public String getHashFileByIDControlInfo() {
        return this.extHashFileByID.toString();
    }

    public String getHashFileByLPControlInfo() {
        return this.extHashFileByLP.toString();
    }

    public void close() {
        this.heapFileVehicles.close(this.controlHeapFileName);
        this.extHashFileByID.close(this.controlHashFileByIDName);
        this.extHashFileByLP.close(this.controlHashFileByLPName);
    }
}

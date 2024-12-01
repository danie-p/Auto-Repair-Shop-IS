package Model;

import ExtendibleHashFile.ExtendibleHashFile;
import FileDataStructure.Block;
import HeapFile.HeapFile;

import java.io.IOException;
import java.util.ArrayList;

public class Model {
    private final HeapFile<Vehicle> heapFileVehicles;
    private final ExtendibleHashFile<VehicleByCustomerID> extHashFileByID;
    private final ExtendibleHashFile<VehicleByLicensePlate> extHashFileByLP;

    public Model(int clusterSize, String heapFileName, String extHashFileByIDName, String extHashFileByLPName) {
        this.heapFileVehicles = new HeapFile<Vehicle>(heapFileName, clusterSize, new Vehicle("", "", -1, "", null));
        this.extHashFileByID = new ExtendibleHashFile<VehicleByCustomerID>(extHashFileByIDName, clusterSize, new VehicleByCustomerID(-1, -1));
        this.extHashFileByLP = new ExtendibleHashFile<VehicleByLicensePlate>(extHashFileByLPName, clusterSize, new VehicleByLicensePlate(-1, ""));
    }

    public void clearData() throws IOException {
        this.heapFileVehicles.clear();
        this.extHashFileByID.clear();
        this.extHashFileByLP.clear();
    }

    // 1. Vyhľadanie všetkých evidovaných údajov o vozidle (vozidlo sa vyhľadá podľa id zákazníka
    // alebo podľa EČV – vyberie si užívateľ)
    public Vehicle getVehicleByID(int idToSearchBy) throws IOException {
        VehicleByCustomerID tempHashRecord = new VehicleByCustomerID(-1, idToSearchBy);
        VehicleByCustomerID foundHashRecord = this.extHashFileByID.get(tempHashRecord);

        int heapBlockAddress = foundHashRecord.getBlockAddress();
        Vehicle tempHeapRecord = new Vehicle("", "", foundHashRecord.getCustomerID(), "", null);

        return this.heapFileVehicles.get(heapBlockAddress, tempHeapRecord);
    }

    public Vehicle getVehicleByLP(String lpToSearchBy) throws IOException {
        VehicleByLicensePlate tempHashRecord = new VehicleByLicensePlate(-1, lpToSearchBy);
        VehicleByLicensePlate foundHashRecord = this.extHashFileByLP.get(tempHashRecord);

        int heapBlockAddress = foundHashRecord.getBlockAddress();
        Vehicle tempHeapRecord = new Vehicle("", "", -1, foundHashRecord.getLicensePlateCode(), null);

        return this.heapFileVehicles.get(heapBlockAddress, tempHeapRecord);
    }

    // 2. Pridanie vozidla – na základe zadaných údajov zaradí vozidlo do evidencie (pozor na ošetrenie
    // unikátnosti niektorých údajov)
    public void insertVehicle(Vehicle vehicle) throws IOException {
        int heapBlockAddress = this.heapFileVehicles.insert(vehicle);

        VehicleByCustomerID vehicleByCustomerID = new VehicleByCustomerID(heapBlockAddress, vehicle.getCustomerID());
        this.extHashFileByID.insert(vehicleByCustomerID);

        VehicleByLicensePlate vehicleByLicensePlate = new VehicleByLicensePlate(heapBlockAddress, vehicle.getLicensePlateCode());
        this.extHashFileByLP.insert(vehicleByLicensePlate);
    }

    // 3. Pridanie návštevy servisu - na základe zadaných údajov pridá návštevu servisu do evidencie
    // (vozidlo sa vyhľadá podľa id zákazníka alebo podľa EČV – vyberie si užívateľ)
    /**
     * @return vozidlo, ku ktorému bola pridaná návšteva servisu; vracia null, ak sa pridanie nepodarilo
     */
    public Vehicle insertServiceVisitByID(ServiceVisit serviceVisit, int idToSearchBy) throws IOException {
        VehicleByCustomerID tempHashRecord = new VehicleByCustomerID(-1, idToSearchBy);
        VehicleByCustomerID foundHashRecord = this.extHashFileByID.get(tempHashRecord);

        int heapBlockAddress = foundHashRecord.getBlockAddress();
        Vehicle tempHeapRecord = new Vehicle("", "", foundHashRecord.getCustomerID(), "", null);

        return this.insertServiceVisitHelper(serviceVisit, heapBlockAddress, tempHeapRecord);
    }

    /**
     * @return vozidlo, ku ktorému bola pridaná návšteva servisu; vracia null, ak sa pridanie nepodarilo
     */
    public Vehicle insertServiceVisitByLP(ServiceVisit serviceVisit, String lpToSearchBy) throws IOException {
        VehicleByLicensePlate tempHashRecord = new VehicleByLicensePlate(-1, lpToSearchBy);
        VehicleByLicensePlate foundHashRecord = this.extHashFileByLP.get(tempHashRecord);

        int heapBlockAddress = foundHashRecord.getBlockAddress();
        Vehicle tempHeapRecord = new Vehicle("", "", -1, foundHashRecord.getLicensePlateCode(), null);

        return this.insertServiceVisitHelper(serviceVisit, heapBlockAddress, tempHeapRecord);
    }

    private Vehicle insertServiceVisitHelper(ServiceVisit serviceVisit, int heapBlockAddress, Vehicle tempHeapRecord) throws IOException {
        Block<Vehicle> blockWithVehicleToUpdate = this.heapFileVehicles.readBlockWithRecord(heapBlockAddress, tempHeapRecord);

        if (blockWithVehicleToUpdate == null)
            return null;

        Vehicle vehicleToUpdate = blockWithVehicleToUpdate.getRecord(tempHeapRecord);

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
     * @param oldVehicle dočasný záznam vozidla s nastaveným kľúčom customerID
     * @param newVehicle záznam vozidla s úplne nastavenými hodnotami, ktoré sa majú aktualizovať
     * @return pôvodný záznam, ktorý bol editovaný
     */
    public Vehicle updateVehicleByID(Vehicle oldVehicle, Vehicle newVehicle) throws IOException {
        VehicleByCustomerID tempHashRecord = new VehicleByCustomerID(-1, oldVehicle.getCustomerID());
        VehicleByCustomerID foundHashRecord = this.extHashFileByID.get(tempHashRecord);

        int heapBlockAddress = foundHashRecord.getBlockAddress();

        return this.heapFileVehicles.update(heapBlockAddress, oldVehicle, newVehicle);
    }

    public Vehicle updateVehicleByLP(Vehicle oldVehicle, Vehicle newVehicle) throws IOException {
        VehicleByLicensePlate tempHashRecord = new VehicleByLicensePlate(-1, oldVehicle.getLicensePlateCode());
        VehicleByLicensePlate foundHashRecord = this.extHashFileByLP.get(tempHashRecord);

        int heapBlockAddress = foundHashRecord.getBlockAddress();

        return this.heapFileVehicles.update(heapBlockAddress, oldVehicle, newVehicle);
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
}

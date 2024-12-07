package Controller;

import Model.*;
import Tools.Constants;
import Tools.StringGenerator;

import java.io.IOException;
import java.util.Random;

public class Controller {
    private final Model model;
    private int customerIDCounter;

    public Controller(Model model) {
        this.model = model;
        this.customerIDCounter = 0;
    }

    public int getCustomerIDCounter() {
        return customerIDCounter;
    }

    public void clearData() throws IOException {
        this.model.clearData();
    }

    public Vehicle getVehicleByIDAsObject(int customerID) throws IOException {
        return this.model.getVehicleByID(customerID);
    }

    public Vehicle getVehicleByLPAsObject(String licensePlate) throws IOException {
        return this.model.getVehicleByLP(licensePlate);
    }

    public String getVehicleByID(int customerID) throws IOException {
        Vehicle foundVehicle = this.model.getVehicleByID(customerID);

        if (foundVehicle != null)
            return "The following vehicle was found in the system by customer ID = [" + customerID + "] :\n" + foundVehicle.toStringAttributes();

        return "Vehicle search was unsuccessful!";
    }

    public String getVehicleByLP(String licensePlate) throws IOException {
        Vehicle foundVehicle = this.model.getVehicleByLP(licensePlate);

        if (foundVehicle != null)
            return "The following vehicle was found in the system by license plate code = [" + licensePlate + "] :\n" + foundVehicle.toStringAttributes();

        return "Vehicle search was unsuccessful!";
    }

    public String insertVehicle(String customerName, String customerSurname, int customerID, String licensePlateCode) throws IOException {
        // vozidlo zaradene do evidencie zatial nema evidovane ziadne navstevy servisu
        Vehicle insertedVehicle = new Vehicle(customerName, customerSurname, customerID, licensePlateCode, null);

        // vozidlo s danym ID uz v systeme existuje
        if (this.model.getVehicleByID(customerID) != null)
            return "Vehicle insertion was unsuccessful! A vehicle with Customer ID = [" + customerID + "] already exists!";

        // vozidlo s danym ECV uz v systeme existuje
        if (this.model.getVehicleByLP(licensePlateCode) != null)
            return "Vehicle insertion was unsuccessful! A vehicle with License Plate Code = [" + licensePlateCode + "] already exists!";

        this.model.insertVehicle(insertedVehicle);
        this.customerIDCounter++;

        return "The following vehicle was inserted into the system:\n" + insertedVehicle.toStringAttributes();
    }

    public String addServiceVisitByID(int customerID, int date, double price, String[] serviceDescs) throws IOException {
        ServiceVisit insertedServiceVisit = new ServiceVisit(date, price, serviceDescs);
        Vehicle vehicleWithAddedSV = this.model.insertServiceVisitByID(insertedServiceVisit, customerID);

        if (vehicleWithAddedSV != null)
            return "A new service visit was added to the following vehicle found by customer ID = [" + customerID + "] :\n" + vehicleWithAddedSV.toStringAttributes();

        return "Service visit insertion was unsuccessful!";
    }

    public String addServiceVisitByLP(String licensePlate, int date, double price, String[] serviceDescs) throws IOException {
        ServiceVisit insertedServiceVisit = new ServiceVisit(date, price, serviceDescs);
        Vehicle vehicleWithAddedSV = this.model.insertServiceVisitByLP(insertedServiceVisit, licensePlate);

        if (vehicleWithAddedSV != null)
            return "A new service visit was added to the following vehicle found by license plate = [" + licensePlate + "] :\n" + vehicleWithAddedSV.toStringAttributes();

        return "Service visit insertion was unsuccessful!";
    }

    public String updateVehicleByID(Vehicle oldVehicle,
                                    String customerName, String customerSurname, int customerID, String licensePlateCode,
                                    ServiceVisit[] serviceVisits) throws IOException {
        Vehicle newVehicle = new Vehicle(customerName, customerSurname, customerID, licensePlateCode, serviceVisits);
        Vehicle oldFoundVehicle = this.model.updateVehicle(oldVehicle, newVehicle);

        return this.updateVehicleHelper(oldFoundVehicle, newVehicle);
    }

    private String updateVehicleHelper(Vehicle oldFoundVehicle, Vehicle newVehicle) {
        if (oldFoundVehicle != null)
            return "The following vehicle was updated in the system\nfrom: " + oldFoundVehicle.toStringAttributes() +
                    "\nto: " + newVehicle.toStringAttributes();

        return "Vehicle update was unsuccessful!";
    }

    public String removeServiceVisit(Vehicle vehicle, int serviceVisitIndex) throws IOException {
        Vehicle vehicleWithRemovedSV = this.model.removeServiceVisitFromVehicle(vehicle, serviceVisitIndex);

        if (vehicleWithRemovedSV != null)
            return "The selected service visit was removed from the following vehicle:\n" + vehicleWithRemovedSV.toStringAttributes();

        return "Service visit removal was unsuccessful!";
    }

    public String readHeapFileSequentially() throws IOException {
        return this.model.readHeapFileSequentially();
    }

    public String readExtHashFileByIDSequentially() throws IOException {
        return this.model.readExtHashFileByIDSequentially();
    }

    public String readExtHashFileByLPSequentially() throws IOException {
        return this.model.readExtHashFileByLPSequentially();
    }
    
    public void generateInputData(int numberOfVehicles) throws IOException {
        Random random = new Random();

        for (int i = 0; i < numberOfVehicles; i++) {
            String customerName = StringGenerator.generateRandomString(3, Constants.maxCustomerNameLength + 1);
            String customerSurname = StringGenerator.generateRandomString(3, Constants.maxCustomerSurnameLength + 1);
            int customerID = this.customerIDCounter;
            this.customerIDCounter++;
            String licensePlateCode = StringGenerator.generateUniqueString(4);

            int serviceVisitsCount = random.nextInt(Constants.maxCustomerServiceVisitsCount + 1);
            ServiceVisit[] serviceVisits = new ServiceVisit[serviceVisitsCount];
            for (int j = 0; j < serviceVisitsCount; j++) {
                int date = random.nextInt();
                double price = Math.round(random.nextDouble(500) * 100.0) / 100.0;

                int serviceDescsCount = random.nextInt(Constants.maxServiceDescriptionsCount + 1);
                String[] serviceDescs = new String[serviceDescsCount];
                for (int k = 0; k < serviceDescsCount; k++) {
                    String desc = StringGenerator.generateRandomString(3, Constants.maxServiceVisitDescLength + 1);
                    serviceDescs[k] = desc;
                }

                ServiceVisit serviceVisit = new ServiceVisit(date, price, serviceDescs);
                serviceVisits[j] = serviceVisit;
            }

            Vehicle randomVehicle = new Vehicle(customerName, customerSurname, customerID, licensePlateCode, serviceVisits);
            this.model.insertVehicle(randomVehicle);
        }
    }

    public void close() {
        this.model.close();
    }
}

package Testing;

import HeapFile.HeapFile;
import HeapFile.RecordWithBlockAddress;
import Model.Vehicle;
import Tools.Constants;
import Tools.StringGenerator;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Tester {
    private final Random random = new Random();
    AtomicInteger IDCounter = new AtomicInteger(0);

    public void runTestOnVehicle(String fileName, int clusterSize) throws IOException {
        HeapFile<Vehicle> vehicleHeapFile = new HeapFile<Vehicle>(fileName, clusterSize, new Vehicle("", "", 0, "", null));

        System.out.println("Generating random data");
        int dataAmount = 100;

        Vehicle[] insertVehicles = new Vehicle[dataAmount];
        for (int i = 0; i < dataAmount; i++) {
            insertVehicles[i] = generateVehicle();
        }

        OperationsGenerator<Vehicle> operationsGenerator = new OperationsGenerator<Vehicle>(vehicleHeapFile, this::generateVehicle, this::generateVehicleWithAddress, this.random);
        operationsGenerator.insertOnly(dataAmount, insertVehicles);

        System.out.println("Starting all operations");
        boolean success = operationsGenerator.insertGetDelete();

        if (success) {
            System.out.println("Test run on vehicles was successful.");
        } else {
            System.out.println("Test run on vehicles failed.");
        }
    }

    private Vehicle generateVehicle() {
        String customerName = StringGenerator.generateRandomString(3, Constants.maxCustomerNameLength);
        String customerSurname = StringGenerator.generateRandomString(5, Constants.maxCustomerSurnameLength);
        int customerID = this.IDCounter.getAndIncrement();
        String licensePlateCode = StringGenerator.generateUniqueString(Constants.maxLicensePlateCodeLength);

        return new Vehicle(customerName, customerSurname, customerID, licensePlateCode, null);
    }

    private RecordWithBlockAddress<Vehicle> generateVehicleWithAddress() {
        return new RecordWithBlockAddress<Vehicle>(this.random.nextInt(), generateVehicle());
    }
}

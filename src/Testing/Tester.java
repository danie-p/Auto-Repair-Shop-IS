package Testing;

import ExtendibleHashFile.ExtendibleHashFile;
import HeapFile.HeapFile;
import HeapFile.RecordWithBlockAddress;
import Model.Customer;
import Model.ServiceVisit;
import Model.Vehicle;
import Tools.Constants;
import Tools.StringGenerator;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Tester {
    private final Random random = new Random();
    AtomicInteger IDCounter = new AtomicInteger(0);

    public void runHeapFileTestOnVehicles(String fileName, int clusterSize, int operationsCount, int initDataAmount) throws IOException {
        HeapFile<Vehicle> vehicleHeapFile = new HeapFile<Vehicle>(fileName, clusterSize, new Vehicle("", "", 0, "", null));

        OperationsGeneratorForHeapFile<Vehicle> operationsGeneratorForHeapFile = new OperationsGeneratorForHeapFile<Vehicle>(operationsCount, vehicleHeapFile, this::generateVehicle, this::generateVehicleWithAddress, this.random);
        operationsGeneratorForHeapFile.insertOnly(initDataAmount);

        System.out.println("Starting all operations");
        boolean success = operationsGeneratorForHeapFile.insertGetDelete();

        if (success) {
            System.out.println("Test run on vehicles was successful.");
        } else {
            System.out.println("Test run on vehicles failed.");
        }
    }

    public void runExtHashFileTestOnVehicles(String fileName, int clusterSize, int operationsCount, int initDataAmount) throws IOException {
        ExtendibleHashFile<Vehicle> vehicleHashFile = new ExtendibleHashFile<Vehicle>(fileName, clusterSize, new Vehicle("", "", 0, "", null));

        OperationsGeneratorForHashFile<Vehicle> operationsGeneratorForHashFile = new OperationsGeneratorForHashFile<Vehicle>(operationsCount, vehicleHashFile, this::generateVehicle, this.random);
        operationsGeneratorForHashFile.insertOnly(initDataAmount);

        System.out.println("Starting all operations");
        boolean success = operationsGeneratorForHashFile.insertGetDelete();

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
        return new RecordWithBlockAddress<>(this.random.nextInt(), generateVehicle());
    }

    public void runSmallExtHashFileTestOnTowns(int clusterSize) throws IOException {
        int[] ids = new int[]{0, 38, 169, 221, 165, 109, 5, 54, 128, 38, 151, 15, 237, 240, 60, 61, 189, 253};
        TestData data1 = new TestData("Zilina", 0);
        TestData data2 = new TestData("Kosice", 38);
        TestData data3 = new TestData("Martin", 169);
        TestData data4 = new TestData("Levice", 221);
        TestData data5 = new TestData("Trnava", 165);
        TestData data6 = new TestData("Snina", 109);
        TestData data7 = new TestData("Senica", 5);
        TestData data8 = new TestData("Nitra", 54);
        TestData data9 = new TestData("Poprad", 128);
        TestData data10 = new TestData("Lucenec", 38);
        TestData data11 = new TestData("Zvolen", 151);
        TestData data12 = new TestData("Presov", 15);
        TestData data13 = new TestData("Puchov", 237);
        TestData data14 = new TestData("Ilava", 240);
        TestData data15 = new TestData("Brezno", 60);
        TestData data16 = new TestData("Bratislava", 61);
        TestData data17 = new TestData("Skalica", 189);
        TestData data18 = new TestData("Bytca", 253);

        ExtendibleHashFile<TestData> extTest = new ExtendibleHashFile<TestData>("test_hash", clusterSize, data1);
        extTest.insert(data1);
        extTest.insert(data2);
        extTest.insert(data3);
        extTest.insert(data4);
        extTest.insert(data5);
        extTest.insert(data6);
        extTest.insert(data7);
        extTest.insert(data8);
        extTest.insert(data9);
        extTest.insert(data10);

        extTest.insert(data11);
        extTest.insert(data12);
        extTest.insert(data13);

        extTest.insert(data16);
        extTest.insert(data17);
        extTest.insert(data18);

        extTest.insert(data14);
        extTest.insert(data15);

        System.out.println(extTest.readSequentially());

        for (int id : ids) {
            System.out.println(extTest.get(new TestData("Mesto", id)));
        }
    }

    public void runSmallHeapFileTestOnCustomers(int clusterSize) throws IOException {
        ServiceVisit[] visits = new ServiceVisit[] {
                new ServiceVisit(100, 100.0, "servis abcdefghijklmnop"),
                new ServiceVisit(200, 200.0, "servis 2"),
                new ServiceVisit(300, 300.0, "servis 3"),
                new ServiceVisit(400, 400.0, "servis 4"),
                new ServiceVisit(500, 500.0, "servis 5"),
                new ServiceVisit(600, 600.0, "servis 6"),
        };
        Customer customer = new Customer(1234, "Jozef", "Skusobny", visits);

        ServiceVisit[] visits2 = new ServiceVisit[] {
                new ServiceVisit(900, 900.0, "toto je servis 123456"),
                new ServiceVisit(800, 800.0, "toto je servis 2"),
                new ServiceVisit(700, 700.0, "toto je servis 3")
        };
        Customer customer2 = new Customer(6789, "Mia", "Testova", visits2);
        Customer customer3 = new Customer(5678, "Amelia", "Nejaka", visits);
        Customer customer4 = new Customer(3456, "Jan", "Nejaky", visits2);

        HeapFile<Customer> heapFile = new HeapFile<Customer>("hf1", clusterSize, customer);

        heapFile.insert(customer);
        heapFile.insert(customer2);
        heapFile.insert(customer3);
        heapFile.insert(customer4);
        heapFile.delete(0, customer2);
        heapFile.delete(0, customer);
        heapFile.insert(customer);
        heapFile.insert(customer2);
        heapFile.insert(customer);
        heapFile.insert(customer2);
        heapFile.delete(0, customer);
        heapFile.delete(1, customer3);
        heapFile.insert(customer2);
        heapFile.delete(2, customer);
        heapFile.delete(1, customer4);
        heapFile.delete(1, customer2);
        heapFile.delete(0, customer2);
        heapFile.insert(customer);
        heapFile.insert(customer3);

        System.out.println(heapFile.readSequentially());

        heapFile.close();
    }
}

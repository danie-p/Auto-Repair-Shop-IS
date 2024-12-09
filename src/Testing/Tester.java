package Testing;

import ExtendibleHashFile.ExtendibleHashFile;
import HeapFile.HeapFile;
import HeapFile.RecordWithBlockAddress;
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
        boolean success = operationsGeneratorForHeapFile.insertGetUpdateDelete();

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
        boolean success = operationsGeneratorForHashFile.insertGetUpdateDelete();

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
        TestData data19 = new TestData("Kremnica", 192);

        TestData data20 = new TestData("Sobrance", 250);
        TestData data21 = new TestData("Medzilaborce", 249);

        ExtendibleHashFile<TestData> extTest = new ExtendibleHashFile<TestData>("test_hash", clusterSize, data1);
//        extTest.insert(data1);
//        extTest.insert(data2);
//        extTest.insert(data3);
//        extTest.insert(data4);
//        extTest.insert(data5);
//        extTest.insert(data6);
//        extTest.insert(data7);
//        extTest.insert(data8);
//        extTest.insert(data9);
//        extTest.insert(data10);

//        extTest.insert(data11);
//        extTest.insert(data12);
//        extTest.insert(data13);

//        extTest.insert(data16);
//        extTest.insert(data17);
//        extTest.insert(data18);

//        extTest.insert(data14);
//        extTest.insert(data15);

//        extTest.insert(data19);

//        extTest.delete(data9);  // Poprad
//        extTest.delete(data10); // Lucenec
//        extTest.delete(data11); // Zvolen
//        extTest.delete(data12); // Presov
//        extTest.delete(data4);  // Levice
//        extTest.delete(data2);  // Kosice
//        extTest.delete(data8);  // Nitra
//        extTest.delete(data3);  // Martin
//        extTest.delete(data1);  // Zilina

        extTest.insert(data20); // Sobrance
        extTest.insert(data10); // Lucenec
        extTest.insert(data3);  // Martin
        extTest.insert(data21); // Medzilaborce
        extTest.insert(data13); // Puchov
//
        extTest.delete(data13); // Puchov

        System.out.println(extTest.readSequentially());
        System.out.println(extTest);

        for (int id : ids) {
            System.out.println(extTest.get(new TestData("Mesto", id)));
        }
    }

    public void runSmallHeapFileTestOnVehicles(int clusterSize) throws IOException {
        String[][] descs = new String[][] {
                new String[6],
                new String[7],
                new String[8],
                new String[9],
                new String[10],
                new String[3]
        };

        for (int i = 0; i < descs.length; i++) {
            for (int j = 0; j < descs[i].length; j++) {
                descs[i][j] = "popis prac " + j + " pre " + (i + 1);
            }
        }

        String[] descs7 = new String[4];
        for (int i = 0; i < descs7.length; i++) {
            descs7[i] = "POPIS PRAC " + i + " pre 7";
        }

        String[] descs8 = new String[4];
        for (int i = 0; i < descs8.length; i++) {
            descs8[i] = "POPIS PRAC " + i + " pre 8";
        }

        String[] descs9 = new String[4];
        for (int i = 0; i < descs9.length; i++) {
            descs9[i] = "POPIS PRAC " + i + " pre 9";
        }

        ServiceVisit[] visits = new ServiceVisit[] {
                new ServiceVisit(100, 100.0, descs[0]),
                new ServiceVisit(200, 200.0, descs[1]),
                new ServiceVisit(300, 300.0, descs[2]),
                new ServiceVisit(400, 400.0, descs[3]),
                new ServiceVisit(500, 500.0, descs[4]),
                new ServiceVisit(600, 600.0, descs[5]),
        };
        Vehicle vehicle = new Vehicle("Jozef", "Skusobny", 1234, "MT12", visits);

        ServiceVisit[] visits2 = new ServiceVisit[] {
                new ServiceVisit(900, 900.0, descs7),
                new ServiceVisit(800, 800.0, descs8),
                new ServiceVisit(700, 700.0, descs9)
        };
        Vehicle vehicle2 = new Vehicle("Mia", "Testova", 6789, "ZA67", visits2);
        Vehicle vehicle3 = new Vehicle("Amelia", "Nejaka", 5678, "MT56", visits);
        Vehicle vehicle4 = new Vehicle("Jan", "Nejaky", 3456, "ZA34", visits2);

        HeapFile<Vehicle> heapFile = new HeapFile<Vehicle>("hf1", clusterSize, vehicle);

        heapFile.insert(vehicle);
        heapFile.insert(vehicle2);
        heapFile.insert(vehicle3);
        heapFile.insert(vehicle4);
        heapFile.delete(0, vehicle2);
        heapFile.delete(0, vehicle);
        heapFile.insert(vehicle);
        heapFile.insert(vehicle2);
        heapFile.insert(vehicle);
        heapFile.insert(vehicle2);
        heapFile.delete(0, vehicle);
        heapFile.delete(1, vehicle3);
        heapFile.insert(vehicle2);
        heapFile.delete(2, vehicle);
        heapFile.delete(1, vehicle4);
        heapFile.delete(1, vehicle2);
        heapFile.delete(0, vehicle2);
        heapFile.insert(vehicle);
        heapFile.insert(vehicle3);

        System.out.println(heapFile.readSequentially());

        heapFile.close("hf1");
    }
}

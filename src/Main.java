import ExtendibleHashFile.*;
import HeapFile.HeapFile;
import Model.*;
import Testing.TestData;
import Testing.Tester;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int clusterSize = 100;

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

        ExtendibleHashFileNew<TestData> extTest = new ExtendibleHashFileNew<>("test_hash.dat", clusterSize, data1);
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

        extTest.readSequentially();

        extTest.insert(data11);
        extTest.insert(data12);

        extTest.insert(data13);



        extTest.insert(data16);
        extTest.insert(data17);
        extTest.insert(data18);

        extTest.insert(data14);
        extTest.insert(data15);

        System.out.println("insert Zvolen <3");
        extTest.readSequentially();

        /*
        Vehicle vehicle1 = new Vehicle("Meno1", "Priezvisko1", 1, "ECV1", null);

        HeapFile<Vehicle> vehicleHeapFile = new HeapFile<Vehicle>("vehicles_heap.dat", clusterSize, vehicle1);
        int vehicleAddress1 = vehicleHeapFile.insert(vehicle1);

        VehicleByCustomerID vehicleID1 = new VehicleByCustomerID(vehicleAddress1, vehicle1.getCustomerID());
        ExtendibleHashFileNew<VehicleByCustomerID> idExtHashFile = new ExtendibleHashFileNew<VehicleByCustomerID>("id_hash.dat", clusterSize, vehicleID1);

        VehicleByLicensePlate vehicleLP1 = new VehicleByLicensePlate(vehicleAddress1, vehicle1.getLicensePlateCode());
        ExtendibleHashFileNew<VehicleByLicensePlate> lpExtHashFile = new ExtendibleHashFileNew<VehicleByLicensePlate>("lp_hash.dat", clusterSize, vehicleLP1);
        */


        /*
        Tester tester = new Tester();
        try {
            tester.runTestOnVehicle("test", 500);
        } catch (IOException e) {
            throw new RuntimeException("Error during test run!" + e);
        }
        */

        /*
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

        HeapFile<Customer> heapFile = new HeapFile<Customer>("hf1", 500, customer);
        try {
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

            heapFile.readSequentially();

            heapFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
    }
}
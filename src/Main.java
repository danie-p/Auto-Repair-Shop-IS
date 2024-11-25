import ExtendibleHashFile.ExtendibleHashFile;
import HeapFile.HeapFile;
import Model.Customer;
import Model.ServiceVisit;
import Testing.Tester;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Tester tester = new Tester();
        try {
            tester.runTestOnVehicle("test", 500);
        } catch (IOException e) {
            throw new RuntimeException("Error during test run!" + e);
        }


        /*
        ServiceVisit[] visits = new ServiceVisit[] {
                new ServiceVisit(100, 100.0, "servisttthervavehrehshtqtv 1"),
                new ServiceVisit(200, 200.0, "servis 2"),
                new ServiceVisit(300, 300.0, "servis 3"),
                new ServiceVisit(400, 400.0, "servis 4"),
                new ServiceVisit(500, 500.0, "servis 5"),
                new ServiceVisit(600, 600.0, "servis 6"),
        };
        Customer customer = new Customer(1234, "Jozef", "Skusobny", visits);

        ServiceVisit[] visits2 = new ServiceVisit[] {
                new ServiceVisit(900, 900.0, "TOTO JE ALE SERVISssss 1"),
                new ServiceVisit(800, 800.0, "TOTO JE ALE SERVIS 2"),
                new ServiceVisit(700, 700.0, "TOTO JE ALE SERVIS 3")
        };
        Customer customer2 = new Customer(6789, "Mia", "Testova", visits2);
        Customer customer3 = new Customer(5678, "Amelia", "Nejaka", visits);
        Customer customer4 = new Customer(3456, "Peter", "Nejaky", visits2);

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

        /*
        ExtendibleHashFile<Customer> extendibleHashFile = new ExtendibleHashFile<Customer>("a.dat", 500, customer);
        try {
            extendibleHashFile.insert(customer);
            extendibleHashFile.insert(customer2);
            extendibleHashFile.insert(customer3);
            extendibleHashFile.readSequentially();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
         */
    }
}
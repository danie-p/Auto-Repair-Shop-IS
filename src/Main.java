import ExtendibleHashFile.DirectoryItem;
import Testing.Tester;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        int clusterSizeSmall = 100;
        int clusterSize = 4000;
        int operationsCount = 1000;
        int initDataAmount = 20000;

        Tester tester = new Tester();

        // EXT HASH FILE TESTS
//        tester.runSmallExtHashFileTestOnTowns(clusterSizeSmall);
        tester.runExtHashFileTestOnVehicles("test", clusterSize, operationsCount, initDataAmount);

        // HEAP FILE TESTS
//        tester.runSmallHeapFileTestOnCustomers(clusterSizeSmall);
//        tester.runHeapFileTestOnVehicles("test", clusterSize, operationsCount, initDataAmount);
    }
}
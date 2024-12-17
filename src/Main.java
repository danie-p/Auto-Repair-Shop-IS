import Controller.Controller;
import ExtendibleHashFile.DirectoryItem;
import GUI.AutoRepairShopGUI;
import GUI.InitDialog;
import HeapFile.HeapFile;
import Model.Model;
import Testing.Tester;
import Tools.Constants;
import Tools.StringGenerator;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        int clusterSizeSmall = 4000;
        int operationsCount = 10000;
        int initDataAmount = 1000;

        Tester tester = new Tester();

        // EXT HASH FILE TESTS
//        tester.runSmallExtHashFileTestOnTowns(100);
//        tester.runExtHashFileTestOnVehicles("test", clusterSizeSmall, operationsCount, initDataAmount);

        // HEAP FILE TESTS
//        tester.runSmallHeapFileTestOnVehicles(clusterSizeSmall);
//        tester.runHeapFileTestOnVehicles("test", clusterSizeSmall, operationsCount, initDataAmount);

        FlatDarkLaf.setup();

        System.out.println(Constants.vehicleSize);
        System.out.println(Constants.serviceVisitSize);

        String[] fileNames = InitDialog.showInitDialog();
        if (fileNames == null) {
            System.exit(0);
        }

        int clusterSizeHeap = 5000;
        int clusterSizeHash = 100;
        Controller controller = getController(fileNames, clusterSizeHeap, clusterSizeHash);

        SwingUtilities.invokeLater(() -> {
            AutoRepairShopGUI gui = new AutoRepairShopGUI(controller, fileNames[6]);
            gui.setVisible(true);
        });
    }

    private static Controller getController(String[] fileNames, int clusterSizeHeap, int clusterSizeHash) {
        String heapFileName = fileNames[0];
        String hashFileByIDName = fileNames[1];
        String hashFileByLPName = fileNames[2];
        String controlHeapFileName = fileNames[3];
        String controlHashFileByIDName = fileNames[4];
        String controlHashFileByLPName = fileNames[5];

        Model model = new Model(clusterSizeHeap, clusterSizeHash,
                heapFileName, hashFileByIDName, hashFileByLPName,
                controlHeapFileName, controlHashFileByIDName, controlHashFileByLPName);

        return new Controller(model);
    }
}
import Controller.Controller;
import GUI.AutoRepairShopGUI;
import GUI.InitDialog;
import Model.Model;
import Testing.Tester;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int clusterSizeSmall = 4000;
        int clusterSize = 8000;
        int operationsCount = 1000;
        int initDataAmount = 100000;

        Tester tester = new Tester();

        // EXT HASH FILE TESTS
//        tester.runSmallExtHashFileTestOnTowns(100);
//        tester.runExtHashFileTestOnVehicles("test", clusterSize, operationsCount, initDataAmount);

        // HEAP FILE TESTS
//        tester.runSmallHeapFileTestOnVehicles(clusterSizeSmall);
//        tester.runHeapFileTestOnVehicles("test", clusterSize, operationsCount, initDataAmount);

        FlatDarkLaf.setup();

//        String[] fileNames = InitDialog.showInitDialog();
//        if (fileNames == null) {
//            System.exit(0);
//        }
//
//        String heapFileName = fileNames[0];
//        String hashFileByIDName = fileNames[1];
//        String hashFileByLPName = fileNames[2];

        String heapFileName = "heap";
        String hashFileByIDName = "hashID";
        String hashFileByLPName = "hashLP";

        Model model = new Model(clusterSize, heapFileName, hashFileByIDName, hashFileByLPName);
        Controller controller = new Controller(model);

        SwingUtilities.invokeLater(() -> {
            AutoRepairShopGUI gui = new AutoRepairShopGUI(controller);
            gui.setVisible(true);
        });
    }
}
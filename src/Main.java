import Controller.Controller;
import GUI.AutoRepairShopGUI;
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


        Model model = new Model(clusterSize, "heap", "hash_by_id", "hash_by_lp");
        Controller controller = new Controller(model);

        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            AutoRepairShopGUI gui = new AutoRepairShopGUI(controller);
            gui.setVisible(true);
        });
    }
}
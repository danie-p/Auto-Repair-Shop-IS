import Controller.Controller;
import GUI.AutoRepairShopGUI;
import Model.Model;
import Testing.Tester;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int clusterSizeSmall = 4000;
        int clusterSize = 8000;
        int operationsCount = 1000;
        int initDataAmount = 20000;

        Tester tester = new Tester();

        // EXT HASH FILE TESTS
//        tester.runSmallExtHashFileTestOnTowns(clusterSizeSmall);
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
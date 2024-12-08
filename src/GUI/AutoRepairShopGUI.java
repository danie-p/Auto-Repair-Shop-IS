package GUI;

import Controller.Controller;
import Tools.StringGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class AutoRepairShopGUI extends JFrame {
    private JPanel contentPane;
    private JPanel panelOfContents;
    private JComboBox<String> comboBoxMenu;
    private JLabel labelMenu;
    private CardLayout cardLayout;
    private MainOperations mainOperations;
    private UpdateVehicle updateVehicle;
    private Controller controller;

    public AutoRepairShopGUI(Controller controller, String generatorControlData) {
        initGeneratorData(generatorControlData);
        this.controller = controller;
        this.mainOperations = new MainOperations(this.controller);
        this.updateVehicle = new UpdateVehicle(this.controller);

        setTitle("Auto Repair Shop Information System");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setSize(screenSize);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setContentPane(this.contentPane);

        this.cardLayout = new CardLayout();
        this.panelOfContents.setLayout(this.cardLayout);

        this.panelOfContents.add(this.mainOperations.getPanel(), "Main Operations");
        this.panelOfContents.add(this.updateVehicle.getPanel(), "Update Vehicle");

        this.comboBoxMenu.addActionListener(e -> {
            String selectedOperation = (String) this.comboBoxMenu.getSelectedItem();
            this.cardLayout.show(this.panelOfContents, selectedOperation);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainOperations.displayTextInfo("Exporting stored data into files.\nClosing the application.");
                controller.close();
                closeGeneratorData(generatorControlData);
                System.exit(0);
            }
        });
    }

    private static void initGeneratorData(String generatorFileName) {
        File fileGeneratorData = new File(generatorFileName + ".txt");

        if (fileGeneratorData.length() != 0) {
            try (Scanner scanner = new Scanner(fileGeneratorData)) {
                int customerIDCounter = scanner.nextInt();
                int uniqueStringCounter = scanner.nextInt();

                Controller.setCustomerIDCounter(customerIDCounter);
                StringGenerator.setUniqueStringCounter(uniqueStringCounter);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Error during generator control data file opening!");
            }
        }
    }

    private static void closeGeneratorData(String generatorFileName) {
        try (PrintWriter writer = new PrintWriter(generatorFileName + ".txt")) {
            writer.println(Controller.getCustomerIDCounter());
            writer.println(StringGenerator.getUniqueStringCounter());
        } catch (IOException e) {
            throw new RuntimeException("Error during generator control data file closing!");
        }
    }
}

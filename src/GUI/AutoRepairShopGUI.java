package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;

public class AutoRepairShopGUI extends JFrame {
    private JPanel contentPane;
    private JPanel panelOfContents;
    private JComboBox<String> comboBoxMenu;
    private JLabel labelMenu;
    private CardLayout cardLayout;
    private MainOperations mainOperations;
    private UpdateVehicle updateVehicle;
    private Controller controller;

    public AutoRepairShopGUI(Controller controller) {
        this.controller = controller;
        this.mainOperations = new MainOperations(this.controller);
        this.updateVehicle = new UpdateVehicle(this.controller);

        setTitle("Auto Repair Shop Information System");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setSize(screenSize);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(this.contentPane);

        this.cardLayout = new CardLayout();
        this.panelOfContents.setLayout(this.cardLayout);

        this.panelOfContents.add(this.mainOperations.getPanel(), "Main Operations");
        this.panelOfContents.add(this.updateVehicle.getPanel(), "Update Vehicle");

        this.comboBoxMenu.addActionListener(e -> {
            String selectedOperation = (String) this.comboBoxMenu.getSelectedItem();
            this.cardLayout.show(this.panelOfContents, selectedOperation);
        });
    }
}

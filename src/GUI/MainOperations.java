package GUI;

import Controller.Controller;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;

public class MainOperations {
    private JPanel contentPane;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private JPanel panelFindVehicle;
    private JPanel panelAddVehicle;
    private JPanel panelAddServiceVisit;
    private JPanel panelOtherFunctionalities;
    private JTextField textFieldFindByID;
    private JButton buttonFindByID;
    private JTextField textFieldFindByLP;
    private JButton buttonFindByLP;
    private JTextField textFieldAddName;
    private JLabel labelFindByID;
    private JLabel labelFindByLP;
    private JLabel labelAddName;
    private JTextField textFieldAddSurname;
    private JTextField textFieldAddCustomerID;
    private JTextField textFieldAddLicensePlate;
    private JLabel labelAddSurname;
    private JLabel labelAddID;
    private JLabel labelAddLP;
    private JButton buttonAddVehicle;
    private JLabel labelGenerateData;
    private JTextField textFieldGenerateNum;
    private JLabel labelGenerateNum;
    private JLabel labelReadHeapFile;
    private JButton buttonGenerateData;
    private JButton buttonShowHeapFile;
    private JButton buttonShowHashFileByID;
    private JButton buttonShowHashFileByLP;
    private JButton buttonclearSystem;
    private JPanel panelDeleteVehicle;
    private JLabel labelDeleteByID;
    private JTextField textFieldDeleteByID;
    private JTextField textFieldDeleteByLP;
    private JLabel labelDeleteByLP;
    private JButton buttonDeleteByID;
    private JButton buttonDeleteByLP;
    private Controller controller;

    public MainOperations(Controller controller) {
        this.controller = controller;

        this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.initTextBorders();

        this.buttonAddVehicle.addActionListener(e -> {
            try {
                String name = textFieldAddName.getText();
                String surname = textFieldAddSurname.getText();
                int customerID = Integer.parseInt(textFieldAddCustomerID.getText());
                String licensePlate = textFieldAddLicensePlate.getText();

                String s = this.controller.insertVehicle(name, surname, customerID, licensePlate);
                textPane.setText(s);

                textFieldAddName.setText("");
                textFieldAddSurname.setText("");
                textFieldAddCustomerID.setText("");
                textFieldAddLicensePlate.setText("");
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation unsuccessful!" + ex);
            }
        });

        this.buttonFindByID.addActionListener(e -> {
            try {
                int customerID = Integer.parseInt(textFieldFindByID.getText());

                String s = this.controller.getVehicleByID(customerID);
                textPane.setText(s);

                textFieldFindByID.setText("");
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonFindByLP.addActionListener(e -> {
            try {
                String licensePlate = textFieldFindByLP.getText();

                if (licensePlate == null)
                    throw new IllegalArgumentException();

                String s = this.controller.getVehicleByLP(licensePlate);
                textPane.setText(s);

                textFieldFindByLP.setText("");
            } catch (IllegalArgumentException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonGenerateData.addActionListener(e -> {
            try {
                int numOfVehicles = Integer.parseInt(textFieldGenerateNum.getText());
                textPane.setText("Generating " + numOfVehicles + " random vehicles. This may take a while.");
                this.controller.generateInputData(numOfVehicles);
                textPane.setText("Random input data has been generated.");

                textFieldGenerateNum.setText("");
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonShowHeapFile.addActionListener(e -> {
            try {
                String s = this.controller.readHeapFileSequentially();
                textPane.setText("Sequential output of Heap File:\n" + s);
                SwingUtilities.invokeLater(() -> {
                    this.scrollPane.getViewport().setViewPosition(new Point(0, 0));
                });
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonShowHashFileByID.addActionListener(e -> {
            try {
                String s = this.controller.readExtHashFileByIDSequentially();
                textPane.setText("Sequential output of Extendible Hash File by Customer ID:\n" + s);
                SwingUtilities.invokeLater(() -> {
                    this.scrollPane.getViewport().setViewPosition(new Point(0, 0));
                });
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonShowHashFileByLP.addActionListener(e -> {
            try {
                String s = this.controller.readExtHashFileByLPSequentially();
                textPane.setText("Sequential output of Extendible Hash File by License Plate:\n" + s);
                SwingUtilities.invokeLater(() -> {
                    this.scrollPane.getViewport().setViewPosition(new Point(0, 0));
                });
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonclearSystem.addActionListener(e -> {
            try {
                this.controller.clearData();
                textPane.setText("The system has been cleared.");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });
    }

    private void initTextBorders() {
        TitledBorder titledBorderFind = BorderFactory.createTitledBorder("Find vehicle");
        titledBorderFind.setTitleFont(titledBorderFind.getTitleFont().deriveFont(Font.BOLD));
        this.panelFindVehicle.setBorder(titledBorderFind);

        TitledBorder titledBorderAdd = BorderFactory.createTitledBorder("Add vehicle");
        titledBorderAdd.setTitleFont(titledBorderAdd.getTitleFont().deriveFont(Font.BOLD));
        this.panelAddVehicle.setBorder(titledBorderAdd);

        TitledBorder titledBorderDelete = BorderFactory.createTitledBorder("Delete vehicle");
        titledBorderDelete.setTitleFont(titledBorderDelete.getTitleFont().deriveFont(Font.BOLD));
        this.panelDeleteVehicle.setBorder(titledBorderDelete);

        TitledBorder titledBorderOther = BorderFactory.createTitledBorder("Other functionalities");
        titledBorderOther.setTitleFont(titledBorderOther.getTitleFont().deriveFont(Font.BOLD));
        this.panelOtherFunctionalities.setBorder(titledBorderOther);
    }

    public JPanel getPanel() {
        return this.contentPane;
    }
}

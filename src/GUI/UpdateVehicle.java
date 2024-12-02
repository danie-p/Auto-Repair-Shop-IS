package GUI;

import Controller.Controller;
import Model.Vehicle;

import javax.swing.*;
import java.io.IOException;

public class UpdateVehicle {
    private JPanel contentPane;
    private JPanel panelFindVehicle;
    private JLabel labelFindVehicle;
    private JPanel panelUpdateVehicleAll;
    private JLabel labelUpdateVehicle;
    private JButton buttonFindByID;
    private JTextField textFieldFindByLP;
    private JTextField textFieldFindByID;
    private JButton buttonFindByLP;
    private JLabel labelFindByID;
    private JPanel panelUpdateVehicle;
    private JTextField textFieldUpdateName;
    private JLabel labelUpdateName;
    private JLabel labelUpdateSurname;
    private JTextField textFieldUpdateSurname;
    private JLabel labelUpdateID;
    private JTextField textFieldUpdateCustomerID;
    private JLabel labelUpdateLP;
    private JTextField textFieldUpdateLicensePlate;
    private JButton buttonUpdateVehicle;
    private JLabel labelFindByLP;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private JPanel panelUpdateServiceVisits;
    private JPanel panelUpdateServiceVisit;
    private JLabel labelUpdateDate;
    private JTextField textFieldUpdateDay;
    private JTextField textFieldUpdateYear;
    private JTextField textFieldUpdateMonth;
    private JLabel labelUpdateTime;
    private JTextField textFieldUpdateHour;
    private JTextField textFieldUpdateMinute;
    private JTextField textFieldUpdateSecond;
    private JLabel labelUpdatePrice;
    private JTextField textFieldUpdatePrice;
    private JScrollPane scrollPaneServiceDesc;
    private JPanel panelServiceDescs;
    private JButton buttonAddServiceVisitDesc;
    private JScrollPane scrollPaneUpdateSV;
    private JLabel labelUpdateSV;
    private Controller controller;
    private Vehicle oldVehicle;

    public UpdateVehicle(Controller controller) {
        this.controller = controller;

        this.buttonFindByID.addActionListener(e -> {
            try {
                int customerID = Integer.parseInt(textFieldFindByID.getText());

                Vehicle foundVehicle = this.controller.getVehicleByIDAsObject(customerID);
                this.oldVehicle = foundVehicle;

                if (foundVehicle != null) {
                    textFieldUpdateName.setText(foundVehicle.getCustomerName());
                    textFieldUpdateSurname.setText(foundVehicle.getCustomerSurname());
                    textFieldUpdateCustomerID.setText(String.valueOf(foundVehicle.getCustomerID()));
                    textFieldUpdateLicensePlate.setText(foundVehicle.getLicensePlateCode());
                    textPane.setText("Vehicle with Customer ID = [" + customerID + "] was successfully found and can be edited.");
                } else {
                    textPane.setText("Vehicle update was unsuccessful! A vehicle with Customer ID = [" + customerID + "] could not be found!");
                }


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

                Vehicle foundVehicle = this.controller.getVehicleByLPAsObject(licensePlate);
                this.oldVehicle = foundVehicle;

                if (foundVehicle != null) {
                    textFieldUpdateName.setText(foundVehicle.getCustomerName());
                    textFieldUpdateSurname.setText(foundVehicle.getCustomerSurname());
                    textFieldUpdateCustomerID.setText(String.valueOf(foundVehicle.getCustomerID()));
                    textFieldUpdateLicensePlate.setText(foundVehicle.getLicensePlateCode());

                    textPane.setText("Vehicle with License Plate Code = [" + licensePlate + "] was successfully found and can be edited.");
                } else {
                    textPane.setText("Vehicle update was unsuccessful! A vehicle with License Plate Code = [" + licensePlate + "] could not be found!");
                }


                textFieldFindByLP.setText("");
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonAddServiceVisitDesc.addActionListener(e -> {

        });

        this.buttonUpdateVehicle.addActionListener(e -> {
            try {
                String name = textFieldUpdateName.getText();
                String surname = textFieldUpdateSurname.getText();
                int customerID = Integer.parseInt(textFieldUpdateCustomerID.getText());
                String licensePlate = textFieldUpdateLicensePlate.getText();

                String s = this.controller.updateVehicleByID(this.oldVehicle, name, surname, customerID, licensePlate, this.oldVehicle.getServiceVisits());
                textPane.setText(s);

                textFieldUpdateName.setText("");
                textFieldUpdateSurname.setText("");
                textFieldUpdateCustomerID.setText("");
                textFieldUpdateLicensePlate.setText("");
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });
    }

    public JPanel getPanel() {
        return this.contentPane;
    }
}

package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class MainOperations {
    private JPanel contentPane;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private JPanel panelFindVehicle;
    private JPanel panelAddVehicle;
    private JPanel panelAddServiceVisit;
    private JPanel panelOtherFunctionalities;
    private JLabel labelFindVehicle;
    private JLabel labelAddVehicle;
    private JLabel labelAddServiceVisit;
    private JLabel labelOther;
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
    private JLabel labelAddDate;
    private JTextField textFieldAddDay;
    private JTextField textFieldAddYear;
    private JTextField textFieldAddMonth;
    private JLabel labelAddTime;
    private JTextField textFieldAddHour;
    private JTextField textFieldAddMinute;
    private JTextField textFieldAddSecond;
    private JLabel labelAddPrice;
    private JTextField textFieldAddPrice;
    private JScrollPane scrollPaneServiceDesc;
    private JButton buttonAddServiceVisitDesc;
    private JButton buttonAddServiceVisitByID;
    private JTextField textFieldAddSVByID;
    private JLabel labelAddSVByID;
    private JLabel labelAddSVByLP;
    private JTextField textFieldAddSVByLP;
    private JButton buttonAddServiceVisitByLP;
    private JLabel labelGenerateData;
    private JTextField textFieldGenerateNum;
    private JLabel labelGenerateNum;
    private JLabel labelReadHeapFile;
    private JButton buttonGenerateData;
    private JButton buttonShowHeapFile;
    private JButton buttonShowHashFileByID;
    private JButton buttonShowHashFileByLP;
    private JPanel panelServiceDescs;
    private JButton buttonclearSystem;
    private Controller controller;
    private int serviceDescs;
    private ArrayList<JTextField> serviceDescsTextFields;

    public MainOperations(Controller controller) {
        this.controller = controller;
        this.serviceDescs = 0;
        this.panelServiceDescs.setLayout(new BoxLayout(panelServiceDescs, BoxLayout.Y_AXIS));
        this.serviceDescsTextFields = new ArrayList<>();

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

                String s = this.controller.getVehicleByLP(licensePlate);
                textPane.setText(s);

                textFieldFindByLP.setText("");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonAddServiceVisitDesc.addActionListener(e -> {
            if (this.serviceDescs < 10) {
                this.serviceDescs++;
                JPanel newRow = new JPanel();
                newRow.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel label = new JLabel("Description " + this.serviceDescs);
                JTextField textField = new JTextField(10);

                newRow.add(label);
                newRow.add(textField);

                this.panelServiceDescs.add(newRow);
                this.panelServiceDescs.revalidate();
                this.panelServiceDescs.repaint();
                this.contentPane.revalidate();
                this.contentPane.repaint();

                this.serviceDescsTextFields.add(textField);
            }
        });

        this.buttonAddServiceVisitByID.addActionListener(e -> {
            try {
                int customerID = Integer.parseInt(textFieldAddSVByID.getText());
                int date = this.addServiceVisitHelper();
                double price = Double.parseDouble(textFieldAddPrice.getText());

                String[] serviceDescsArr = new String[this.serviceDescsTextFields.size()];
                for (int i = 0; i < serviceDescsArr.length; i++) {
                    serviceDescsArr[i] = this.serviceDescsTextFields.get(i).getText();
                }

                String s = this.controller.addServiceVisitByID(customerID, date, price, serviceDescsArr);
                textPane.setText(s);

                textFieldAddSVByID.setText("");

                this.addServiceVisitCleanup();
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonAddServiceVisitByLP.addActionListener(e -> {
            try {
                String licensePlate = textFieldAddSVByLP.getText();
                int date = this.addServiceVisitHelper();
                double price = Double.parseDouble(textFieldAddPrice.getText());

                String[] serviceDescsArr = new String[this.serviceDescsTextFields.size()];
                for (int i = 0; i < serviceDescsArr.length; i++) {
                    serviceDescsArr[i] = this.serviceDescsTextFields.get(i).getText();
                }

                String s = this.controller.addServiceVisitByLP(licensePlate, date, price, serviceDescsArr);
                textPane.setText(s);

                textFieldAddSVByID.setText("");

                this.addServiceVisitCleanup();
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonGenerateData.addActionListener(e -> {
            try {
                int numOfVehicles = Integer.parseInt(textFieldGenerateNum.getText());
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
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonShowHashFileByID.addActionListener(e -> {
            try {
                String s = this.controller.readExtHashFileByIDSequentially();
                textPane.setText("Sequential output of Extendible Hash File by Customer ID:\n" + s);
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonShowHashFileByLP.addActionListener(e -> {
            try {
                String s = this.controller.readExtHashFileByLPSequentially();
                textPane.setText("Sequential output of Extendible Hash File by License Plate:\n" + s);
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

    private void addServiceVisitCleanup() {
        textFieldAddYear.setText("");
        textFieldAddMonth.setText("");
        textFieldAddDay.setText("");
        textFieldAddHour.setText("");
        textFieldAddMinute.setText("");
        textFieldAddSecond.setText("");
        textFieldAddPrice.setText("");

        this.panelServiceDescs.removeAll();
        this.panelServiceDescs.revalidate();
        this.panelServiceDescs.repaint();
        this.contentPane.revalidate();
        this.contentPane.repaint();

        this.serviceDescsTextFields = new ArrayList<>();
        this.serviceDescs = 0;
    }

    private int addServiceVisitHelper() {
        int year = Integer.parseInt(textFieldAddYear.getText());
        int month = Integer.parseInt(textFieldAddMonth.getText());
        int day = Integer.parseInt(textFieldAddDay.getText());
        int hour = Integer.parseInt(textFieldAddHour.getText());
        int minute = Integer.parseInt(textFieldAddMinute.getText());
        int second = Integer.parseInt(textFieldAddSecond.getText());
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        return (int) dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public JPanel getPanel() {
        return this.contentPane;
    }
}

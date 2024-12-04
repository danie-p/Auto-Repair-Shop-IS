package GUI;

import Controller.Controller;
import Model.ServiceVisit;
import Model.Vehicle;
import Tools.Constants;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class UpdateVehicle {
    private JPanel contentPane;
    private JPanel panelFindVehicleToUpdate;
    private JPanel panelUpdateVehicleAll;
    private JButton buttonFindByIDToUpdate;
    private JTextField textFieldFindByLPToUpdate;
    private JTextField textFieldFindByIDToUpdate;
    private JButton buttonFindByLPToUpdate;
    private JLabel labelFindByIDToUpdate;
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
    private JLabel labelFindByLPToUpdate;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private JPanel panelUpdateServiceVisits;
    private JScrollPane scrollPaneUpdateSV;
    private JPanel panelInScrollPaneUpdateSV;
    private JLabel labelUpdateServiceVisits;
    private JPanel panelAddServiceVisit;
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
    private JPanel panelServiceDescs;
    private JButton buttonAddServiceVisitDesc;
    private JLabel labelAddSVByID;
    private JLabel labelAddSVByLP;
    private JButton buttonAddServiceVisitByID;
    private JButton buttonAddServiceVisitByLP;
    private JTextField textFieldAddSVByLP;
    private JTextField textFieldAddSVByID;
    private JPanel panelDeleteServiceVisitAll;
    private JPanel panelFindVehicleToDeleteSV;
    private JLabel labelFindByIDToDeleteSV;
    private JTextField textFieldFindByIDToDeleteSV;
    private JButton buttonFindByIDToDeleteSV;
    private JLabel labelFindByLPToDeleteSV;
    private JTextField textFieldFindByLPToDeleteSV;
    private JButton buttonFindByLPToDeleteSV;
    private JPanel panelDeleteServiceVisit;
    private JLabel labelDeleteServiceVisit;
    private JScrollPane scrollPaneDeleteSV;
    private JPanel panelInScrollPaneDeleteSV;
    private JPanel panelFindVehicleToAddSV;
    private Controller controller;
    private Vehicle oldVehicle;
    private ArrayList<PanelUpdateServiceVisits> panelsUpdateServiceVisits;
    private int serviceDescsCount;
    private ArrayList<JTextField> serviceDescsTextFields;

    public UpdateVehicle(Controller controller) {
        this.controller = controller;
        this.panelsUpdateServiceVisits = new ArrayList<>();

        this.panelServiceDescs.setLayout(new BoxLayout(this.panelServiceDescs, BoxLayout.Y_AXIS));
        this.panelServiceDescs.setAlignmentY(Component.TOP_ALIGNMENT);
        this.serviceDescsCount = 0;
        this.serviceDescsTextFields = new ArrayList<>();

        panelInScrollPaneUpdateSV.setLayout(new BoxLayout(panelInScrollPaneUpdateSV, BoxLayout.Y_AXIS));
        scrollPaneUpdateSV.setViewportView(panelInScrollPaneUpdateSV);

        this.initTextBorders();

        this.buttonFindByIDToUpdate.addActionListener(e -> {
            try {
                int customerID = Integer.parseInt(textFieldFindByIDToUpdate.getText());

                Vehicle foundVehicle = this.controller.getVehicleByIDAsObject(customerID);
                this.oldVehicle = foundVehicle;

                if (foundVehicle != null) {
                    textFieldUpdateName.setText(foundVehicle.getCustomerName());
                    textFieldUpdateSurname.setText(foundVehicle.getCustomerSurname());
                    textFieldUpdateCustomerID.setText(String.valueOf(foundVehicle.getCustomerID()));
                    textFieldUpdateLicensePlate.setText(foundVehicle.getLicensePlateCode());

                    this.updateLoadHelper(foundVehicle);

                    textPane.setText("Vehicle with Customer ID = [" + customerID + "] was successfully found and can be edited.");
                } else {
                    textPane.setText("Vehicle update was unsuccessful! A vehicle with Customer ID = [" + customerID + "] could not be found!");
                }

                textFieldFindByIDToUpdate.setText("");
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonFindByLPToUpdate.addActionListener(e -> {
            try {
                String licensePlate = textFieldFindByLPToUpdate.getText();

                Vehicle foundVehicle = this.controller.getVehicleByLPAsObject(licensePlate);
                this.oldVehicle = foundVehicle;

                if (foundVehicle != null) {
                    textFieldUpdateName.setText(foundVehicle.getCustomerName());
                    textFieldUpdateSurname.setText(foundVehicle.getCustomerSurname());
                    textFieldUpdateCustomerID.setText(String.valueOf(foundVehicle.getCustomerID()));
                    textFieldUpdateLicensePlate.setText(foundVehicle.getLicensePlateCode());

                    this.updateLoadHelper(foundVehicle);

                    textPane.setText("Vehicle with License Plate Code = [" + licensePlate + "] was successfully found and can be edited.");
                } else {
                    textPane.setText("Vehicle update was unsuccessful! A vehicle with License Plate Code = [" + licensePlate + "] could not be found!");
                }

                textFieldFindByLPToUpdate.setText("");
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonUpdateVehicle.addActionListener(e -> {
            try {
                String name = textFieldUpdateName.getText();
                String surname = textFieldUpdateSurname.getText();
                int customerID = Integer.parseInt(textFieldUpdateCustomerID.getText());
                String licensePlate = textFieldUpdateLicensePlate.getText();

                ServiceVisit[] serviceVisits = new ServiceVisit[this.panelsUpdateServiceVisits.size()];
                for (int i = 0; i < this.panelsUpdateServiceVisits.size(); i++) {
                    PanelUpdateServiceVisits panelsUpdateServiceVisit = this.panelsUpdateServiceVisits.get(i);

                    int year = Integer.parseInt(panelsUpdateServiceVisit.getTextFieldUpdateYear().getText());
                    int month = Integer.parseInt(panelsUpdateServiceVisit.getTextFieldUpdateMonth().getText());
                    int day = Integer.parseInt(panelsUpdateServiceVisit.getTextFieldUpdateDay().getText());
                    int hour = Integer.parseInt(panelsUpdateServiceVisit.getTextFieldUpdateHour().getText());
                    int minute = Integer.parseInt(panelsUpdateServiceVisit.getTextFieldUpdateMinute().getText());
                    int second = Integer.parseInt(panelsUpdateServiceVisit.getTextFieldUpdateSecond().getText());
                    LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
                    int date = (int) dateTime.toEpochSecond(ZoneOffset.UTC);

                    double price = Double.parseDouble(panelsUpdateServiceVisit.getTextFieldUpdatePrice().getText());

                    String[] serviceDescriptions = new String[this.oldVehicle.getServiceVisits()[i].getServiceDescriptionsCount()];
                    for (int j = 0; j < serviceDescriptions.length; j++) {
                        serviceDescriptions[j] = this.oldVehicle.getServiceVisits()[i].getServiceDescriptions()[j];
                    }

                    // TODO: service descriptions
                    ServiceVisit serviceVisit = new ServiceVisit(date, price, serviceDescriptions);
                    serviceVisits[i] = serviceVisit;
                }

                String s = this.controller.updateVehicleByID(this.oldVehicle, name, surname, customerID, licensePlate, serviceVisits);
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

        this.buttonAddServiceVisitDesc.addActionListener(e -> {
            if (this.serviceDescsCount < 10) {
                this.serviceDescsCount++;

                JPanel newRow = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();

                JLabel label = new JLabel("Description " + this.serviceDescsCount);
                JTextField textField = new JTextField(10);

                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 1;
                gbc.insets = new Insets(5, 5, 5, 5);
                newRow.add(label, gbc);

                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1.0;
                newRow.add(textField, gbc);

                this.panelServiceDescs.add(newRow);
                this.panelServiceDescs.revalidate();
                this.panelServiceDescs.repaint();
                this.contentPane.revalidate();
                this.contentPane.repaint();

                this.serviceDescsTextFields.add(textField);
            }
        });
    }

    public JPanel getPanel() {
        return this.contentPane;
    }

    private void updateLoadHelper(Vehicle foundVehicle) {
        int serviceVisitsCount = foundVehicle.getServiceVisitsCount();
        this.panelsUpdateServiceVisits = new ArrayList<>();
        this.panelInScrollPaneUpdateSV.removeAll();

        for (int i = 0; i < serviceVisitsCount; i++) {
            PanelUpdateServiceVisits panelUpdateServiceVisits = new PanelUpdateServiceVisits(i);

            JPanel panelUpdateServiceVisit = panelUpdateServiceVisits.getPanel();
            this.panelInScrollPaneUpdateSV.add(panelUpdateServiceVisit);
            this.panelInScrollPaneUpdateSV.revalidate();
            this.panelInScrollPaneUpdateSV.repaint();
            this.contentPane.revalidate();
            this.contentPane.repaint();

            ServiceVisit serviceVisit = foundVehicle.getServiceVisits()[i];
            int date = serviceVisit.getDate();
            LocalDateTime dateTime = Instant.ofEpochSecond(date).atZone(ZoneId.systemDefault()).toLocalDateTime();

            panelUpdateServiceVisits.getTextFieldUpdateDay().setText(String.valueOf(dateTime.getDayOfMonth()));
            panelUpdateServiceVisits.getTextFieldUpdateMonth().setText(String.valueOf(dateTime.getMonthValue()));
            panelUpdateServiceVisits.getTextFieldUpdateYear().setText(String.valueOf(dateTime.getYear()));
            panelUpdateServiceVisits.getTextFieldUpdateHour().setText(String.valueOf(dateTime.getHour()));
            panelUpdateServiceVisits.getTextFieldUpdateMinute().setText(String.valueOf(dateTime.getMinute()));
            panelUpdateServiceVisits.getTextFieldUpdateSecond().setText(String.valueOf(dateTime.getSecond()));

            panelUpdateServiceVisits.getTextFieldUpdatePrice().setText(String.valueOf(serviceVisit.getPrice()));

            this.panelsUpdateServiceVisits.add(panelUpdateServiceVisits);
        }
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
        this.serviceDescsCount = 0;
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

    private void initTextBorders() {
        TitledBorder titledBorderFind = BorderFactory.createTitledBorder("Find vehicle to update");
        titledBorderFind.setTitleFont(titledBorderFind.getTitleFont().deriveFont(Font.BOLD));
        this.panelFindVehicleToUpdate.setBorder(titledBorderFind);

        TitledBorder titledBorderUpdate = BorderFactory.createTitledBorder("Update vehicle");
        titledBorderUpdate.setTitleFont(titledBorderUpdate.getTitleFont().deriveFont(Font.BOLD));
        this.panelUpdateVehicleAll.setBorder(titledBorderUpdate);

        TitledBorder titledBorderAddSV = BorderFactory.createTitledBorder("Add service visit");
        titledBorderAddSV.setTitleFont(titledBorderAddSV.getTitleFont().deriveFont(Font.BOLD));
        this.panelAddServiceVisit.setBorder(titledBorderAddSV);

        TitledBorder titledBorderAdd = BorderFactory.createTitledBorder("Find vehicle to add a service visit to");
        titledBorderAdd.setTitleFont(titledBorderAdd.getTitleFont().deriveFont(Font.BOLD));
        this.panelFindVehicleToAddSV.setBorder(titledBorderAdd);

        TitledBorder titledBorderDeleteSV = BorderFactory.createTitledBorder("Delete service visit");
        titledBorderDeleteSV.setTitleFont(titledBorderDeleteSV.getTitleFont().deriveFont(Font.BOLD));
        this.panelDeleteServiceVisitAll.setBorder(titledBorderDeleteSV);

        TitledBorder titledBorderDelete = BorderFactory.createTitledBorder("Find vehicle to delete a service visit from");
        titledBorderDelete.setTitleFont(titledBorderDelete.getTitleFont().deriveFont(Font.BOLD));
        this.panelFindVehicleToDeleteSV.setBorder(titledBorderDelete);
    }
}

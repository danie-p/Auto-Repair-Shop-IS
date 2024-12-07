package GUI;

import Controller.Controller;
import Model.ServiceVisit;
import Model.Vehicle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
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
    private ArrayList<JTextField> serviceDescsTextFields;
    private ArrayList<JLabel> serviceDescsLabels;

    public UpdateVehicle(Controller controller) {
        this.controller = controller;
        this.panelsUpdateServiceVisits = new ArrayList<>();

        this.panelServiceDescs.setLayout(new BoxLayout(this.panelServiceDescs, BoxLayout.Y_AXIS));
        this.panelServiceDescs.setAlignmentY(Component.TOP_ALIGNMENT);
        this.serviceDescsTextFields = new ArrayList<>();
        this.serviceDescsLabels = new ArrayList<JLabel>();

        this.panelInScrollPaneUpdateSV.setLayout(new BoxLayout(this.panelInScrollPaneUpdateSV, BoxLayout.Y_AXIS));
        this.scrollPaneUpdateSV.setViewportView(this.panelInScrollPaneUpdateSV);

        this.panelInScrollPaneDeleteSV.setLayout(new BoxLayout(this.panelInScrollPaneDeleteSV, BoxLayout.Y_AXIS));
        this.scrollPaneDeleteSV.setViewportView(this.panelInScrollPaneDeleteSV);

        this.scrollPaneServiceDesc.getVerticalScrollBar().setUnitIncrement(16);
        this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.scrollPaneUpdateSV.getVerticalScrollBar().setUnitIncrement(16);
        this.scrollPaneDeleteSV.getVerticalScrollBar().setUnitIncrement(16);

        this.initTextBorders();

        this.buttonFindByIDToUpdate.addActionListener(e -> {
            try {
                int customerID = Integer.parseInt(this.textFieldFindByIDToUpdate.getText());

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
                    this.updateVehicleCleanup();
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

                if (licensePlate == null)
                    throw new IllegalArgumentException();

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
                    this.updateVehicleCleanup();
                    textPane.setText("Vehicle update was unsuccessful! A vehicle with License Plate Code = [" + licensePlate + "] could not be found!");
                }

                textFieldFindByLPToUpdate.setText("");
            } catch (IllegalArgumentException ex) {
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
                    serviceVisits[i] = panelsUpdateServiceVisit.getServiceVisit();
                }

                String s = this.controller.updateVehicleByID(this.oldVehicle, name, surname, customerID, licensePlate, serviceVisits);
                textPane.setText(s);

                this.updateVehicleCleanup();
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonAddServiceVisitByID.addActionListener(e -> {
            try {
                int customerID = Integer.parseInt(textFieldAddSVByID.getText());
                int date = this.parseDateTime();
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
                int date = this.parseDateTime();
                double price = Double.parseDouble(textFieldAddPrice.getText());

                String[] serviceDescsArr = new String[this.serviceDescsTextFields.size()];
                for (int i = 0; i < serviceDescsArr.length; i++) {
                    serviceDescsArr[i] = this.serviceDescsTextFields.get(i).getText();
                }

                String s = this.controller.addServiceVisitByLP(licensePlate, date, price, serviceDescsArr);
                textPane.setText(s);

                textFieldAddSVByLP.setText("");

                this.addServiceVisitCleanup();
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonAddServiceVisitDesc.addActionListener(e -> {
            if (this.serviceDescsTextFields.size() < 10) {
                JPanel newRow = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();

                JLabel label = new JLabel("Description " + (this.serviceDescsTextFields.size() + 1));
                JTextField textField = new JTextField(10);
                JButton button = removeItemButton(this.panelServiceDescs, this.serviceDescsTextFields, this.serviceDescsLabels, label, textField, newRow);

                initItemsToAddNewRow(newRow, gbc, label, textField, button);

                this.panelServiceDescs.add(newRow);
                this.panelServiceDescs.revalidate();
                this.panelServiceDescs.repaint();

                this.serviceDescsTextFields.add(textField);
                this.serviceDescsLabels.add(label);
            }
        });

        this.buttonFindByIDToDeleteSV.addActionListener(e -> {
            try {
                this.panelInScrollPaneDeleteSV.removeAll();

                int customerID = Integer.parseInt(this.textFieldFindByIDToDeleteSV.getText());
                Vehicle foundVehicle = this.controller.getVehicleByIDAsObject(customerID);

                if (foundVehicle == null) {
                    textPane.setText("Vehicle with Customer ID = [" + customerID + "] could not be found!");
                } else {
                    this.removeServiceVisitHelper(foundVehicle);

                    if (foundVehicle.getServiceVisitsCount() == 0) {
                        textPane.setText("Vehicle with Customer ID = [" + customerID + "] has no service visits to delete!");
                    } else {
                        textPane.setText("Vehicle with Customer ID = [" + customerID + "] was found and its service visits can be deleted.");
                    }
                }

                this.panelInScrollPaneDeleteSV.revalidate();
                this.panelInScrollPaneDeleteSV.repaint();
                this.textFieldFindByIDToDeleteSV.setText("");
            } catch (NumberFormatException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });

        this.buttonFindByLPToDeleteSV.addActionListener(e -> {
            try {
                this.panelInScrollPaneDeleteSV.removeAll();

                String licensePlate = this.textFieldFindByLPToDeleteSV.getText();

                if (licensePlate == null)
                    throw new IllegalArgumentException();

                Vehicle foundVehicle = this.controller.getVehicleByLPAsObject(licensePlate);

                if (foundVehicle == null) {
                    textPane.setText("Vehicle with License Plate Code = [" + licensePlate + "] could not be found!");
                } else {
                    this.removeServiceVisitHelper(foundVehicle);

                    if (foundVehicle.getServiceVisitsCount() == 0) {
                        textPane.setText("Vehicle with License Plate Code = [" + licensePlate + "] has no service visits to delete!");
                    } else {
                        textPane.setText("Vehicle with License Plate Code = [" + licensePlate + "] was found and its service visits can be deleted.");
                    }
                }

                this.panelInScrollPaneDeleteSV.revalidate();
                this.panelInScrollPaneDeleteSV.repaint();
                this.textFieldFindByLPToDeleteSV.setText("");
            } catch (IllegalArgumentException ex) {
                textPane.setText("Please enter valid inputs!");
            } catch (IOException ex) {
                textPane.setText("Input/Output operation was unsuccessful!" + ex);
            }
        });
    }

    private void removeServiceVisitHelper(Vehicle foundVehicle) {
        for (int i = 0; i < foundVehicle.getServiceVisitsCount(); i++) {
            ServiceVisit serviceVisit = foundVehicle.getServiceVisits()[i];

            PanelDeleteServiceVisit panelDeleteServiceVisit = new PanelDeleteServiceVisit(serviceVisit, i);
            JPanel panel = panelDeleteServiceVisit.getPanel();
            this.panelInScrollPaneDeleteSV.add(panel);

            int finalI = i;
            panelDeleteServiceVisit.getButton().addActionListener(e -> {
                try {
                    textPane.setText(this.controller.removeServiceVisit(foundVehicle, finalI));
                } catch (IOException ex) {
                    this.textPane.setText("Input/Output operation was unsuccessful!" + ex);
                }
                this.panelInScrollPaneDeleteSV.removeAll();
                this.panelInScrollPaneDeleteSV.revalidate();
                this.panelInScrollPaneDeleteSV.repaint();
            });
        }
    }

    private void updateVehicleCleanup() {
        textFieldUpdateName.setText("");
        textFieldUpdateSurname.setText("");
        textFieldUpdateCustomerID.setText("");
        textFieldUpdateLicensePlate.setText("");

        this.panelInScrollPaneUpdateSV.removeAll();
        this.panelInScrollPaneUpdateSV.revalidate();
        this.panelInScrollPaneUpdateSV.repaint();
    }

    public static JButton removeItemButton(JPanel panel, ArrayList<JTextField> textFields, ArrayList<JLabel> labels, JLabel label, JTextField textField, JPanel newRow) {
        JButton button = new JButton("×");
        button.setPreferredSize(new Dimension(20, 20));
        button.setBackground(new Color(48, 6, 4));

        button.addActionListener(eInner -> {
            textFields.remove(textField);
            labels.remove(label);

            for (int i = 0; i < labels.size(); i++) {
                labels.get(i).setText("Description " + (i + 1));
            }

            panel.remove(newRow);
            panel.revalidate();
            panel.repaint();
        });
        return button;
    }

    public JPanel getPanel() {
        return this.contentPane;
    }

    private void updateLoadHelper(Vehicle foundVehicle) {
        int serviceVisitsCount = foundVehicle.getServiceVisitsCount();
        this.panelsUpdateServiceVisits = new ArrayList<>();
        this.panelInScrollPaneUpdateSV.removeAll();
        this.panelInScrollPaneUpdateSV.revalidate();
        this.panelInScrollPaneUpdateSV.repaint();

        if (serviceVisitsCount == 0) {
            JLabel label = new JLabel("No service visits to update.");
            label.setBorder(new EmptyBorder(10, 10, 10, 10));
            this.panelInScrollPaneUpdateSV.add(label, BorderLayout.NORTH);
        }

        for (int i = 0; i < serviceVisitsCount; i++) {
            PanelUpdateServiceVisits panelUpdateServiceVisits = new PanelUpdateServiceVisits(i);

            JPanel panelUpdateServiceVisit = panelUpdateServiceVisits.getPanel();
            this.panelInScrollPaneUpdateSV.add(panelUpdateServiceVisit);
            this.panelInScrollPaneUpdateSV.revalidate();
            this.panelInScrollPaneUpdateSV.repaint();

            ServiceVisit serviceVisit = foundVehicle.getServiceVisits()[i];

            panelUpdateServiceVisits.updateServiceVisit(serviceVisit);

            this.panelsUpdateServiceVisits.add(panelUpdateServiceVisits);
        }

        for (PanelUpdateServiceVisits panel : this.panelsUpdateServiceVisits) {
            JButton buttonAddServiceVisitDesc = panel.getButtonAddServiceVisitDesc();

            JPanel serviceDescsPanel = panel.getPanelServiceDescs();
            ArrayList<JTextField> serviceDescsTextFields = panel.getServiceDescsTextFields();
            ArrayList<JLabel> serviceDescsLabels = panel.getServiceDescsLabels();

            buttonAddServiceVisitDesc.addActionListener(e -> {
                if (serviceDescsTextFields.size() < 10) {
                    JPanel newRow = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();

                    JLabel label = new JLabel("Description " + (serviceDescsTextFields.size() + 1));
                    JTextField textField = new JTextField(10);
                    addNewRowServiceDescs(newRow, gbc, label, textField, serviceDescsPanel, serviceDescsTextFields, serviceDescsLabels);
                }
            });
        }
    }

    public static void initItemsToAddNewRow(JPanel newRow, GridBagConstraints gbc, JLabel label, JTextField textField, JButton button) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        newRow.add(label, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        newRow.add(button, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        newRow.add(textField, gbc);
    }

    public static void addNewRowServiceDescs(JPanel newRow, GridBagConstraints gbc, JLabel label, JTextField textField, JPanel panelServiceDescs, ArrayList<JTextField> serviceDescsTextFields, ArrayList<JLabel> serviceDescsLabels) {
        JButton button = removeItemButton(panelServiceDescs,
                serviceDescsTextFields,
                serviceDescsLabels,
                label, textField, newRow);

        initItemsToAddNewRow(newRow, gbc, label, textField, button);

        panelServiceDescs.add(newRow);
        panelServiceDescs.revalidate();
        panelServiceDescs.repaint();

        serviceDescsTextFields.add(textField);
        serviceDescsLabels.add(label);
    }

    private int parseDateTime() {
        LocalDateTime dateTime = parseDateTime(textFieldAddYear, textFieldAddMonth, textFieldAddDay, textFieldAddHour, textFieldAddMinute, textFieldAddSecond);
        return (int) dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static LocalDateTime parseDateTime(JTextField textFieldAddYear, JTextField textFieldAddMonth, JTextField textFieldAddDay, JTextField textFieldAddHour, JTextField textFieldAddMinute, JTextField textFieldAddSecond) {
        int year = Integer.parseInt(textFieldAddYear.getText());
        int month = Integer.parseInt(textFieldAddMonth.getText());
        int day = Integer.parseInt(textFieldAddDay.getText());
        int hour = Integer.parseInt(textFieldAddHour.getText());
        int minute = Integer.parseInt(textFieldAddMinute.getText());
        int second = Integer.parseInt(textFieldAddSecond.getText());

        return LocalDateTime.of(year, month, day, hour, minute, second);
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

        this.serviceDescsTextFields = new ArrayList<>();
    }
}

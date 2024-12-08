package GUI;

import Model.ServiceVisit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;

import static GUI.UpdateVehicle.*;

public class PanelUpdateServiceVisits {
    private JPanel contentPane;
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
    private JButton buttonAddServiceVisitDesc;
    private JScrollPane scrollPaneServiceDesc;
    private JPanel panelServiceDescs;
    private JLabel labelUpdateSV;
    private ArrayList<JTextField> serviceDescsTextFields;
    private ArrayList<JLabel> serviceDescsLabels;


    public PanelUpdateServiceVisits(int i) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Service Visit #" + (i + 1));
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD));
        this.panelUpdateServiceVisit.setBorder(titledBorder);

        this.panelServiceDescs.setLayout(new BoxLayout(this.panelServiceDescs, BoxLayout.Y_AXIS));
        this.panelServiceDescs.setAlignmentY(Component.TOP_ALIGNMENT);
        this.scrollPaneServiceDesc.setViewportView(this.panelServiceDescs);

        this.serviceDescsTextFields = new ArrayList<>();
        this.serviceDescsLabels = new ArrayList<>();

        this.scrollPaneServiceDesc.getVerticalScrollBar().setUnitIncrement(16);

        this.contentPane.setBorder(new EmptyBorder(10, 0, 10, 0));
    }

    public ServiceVisit getServiceVisit() {
        LocalDateTime dateTime = parseDateTime(textFieldUpdateYear, textFieldUpdateMonth, textFieldUpdateDay, textFieldUpdateHour, textFieldUpdateMinute, textFieldUpdateSecond);
        int date = (int) dateTime.toEpochSecond(ZoneOffset.UTC);

        double price = Double.parseDouble(textFieldUpdatePrice.getText());

        String[] serviceDescriptions = new String[serviceDescsTextFields.size()];
        for (int j = 0; j < serviceDescsTextFields.size(); j++) {
            serviceDescriptions[j] = serviceDescsTextFields.get(j).getText();
        }

        return new ServiceVisit(date, price, serviceDescriptions);
    }

    public void updateServiceVisit(ServiceVisit serviceVisit) {
        int date = serviceVisit.getDate();
        LocalDateTime dateTime = Instant.ofEpochSecond(date).atZone(ZoneOffset.UTC).toLocalDateTime();

        textFieldUpdateDay.setText(String.valueOf(dateTime.getDayOfMonth()));
        textFieldUpdateMonth.setText(String.valueOf(dateTime.getMonthValue()));
        textFieldUpdateYear.setText(String.valueOf(dateTime.getYear()));
        textFieldUpdateHour.setText(String.valueOf(dateTime.getHour()));
        textFieldUpdateMinute.setText(String.valueOf(dateTime.getMinute()));
        textFieldUpdateSecond.setText(String.valueOf(dateTime.getSecond()));

        textFieldUpdatePrice.setText(String.valueOf(serviceVisit.getPrice()));

        int updateServiceDescsCount = serviceVisit.getServiceDescriptionsCount();

        for (int j = 0; j < updateServiceDescsCount; j++) {
            JPanel newRow = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JLabel label = new JLabel("Description " + (j + 1));
            JTextField textField = new JTextField(10);
            textField.setText(serviceVisit.getServiceDescriptions()[j]);
            addNewRowServiceDescs(newRow, gbc, label, textField, panelServiceDescs, serviceDescsTextFields, serviceDescsLabels);
        }
    }

    public JPanel getPanel() {
        return this.contentPane;
    }


    public JButton getButtonAddServiceVisitDesc() {
        return buttonAddServiceVisitDesc;
    }

    public JPanel getPanelServiceDescs() {
        return this.panelServiceDescs;
    }

    public ArrayList<JTextField> getServiceDescsTextFields() {
        return serviceDescsTextFields;
    }

    public ArrayList<JLabel> getServiceDescsLabels() {
        return serviceDescsLabels;
    }
}

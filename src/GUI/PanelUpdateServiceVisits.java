package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

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

    public JPanel getPanel() {
        return this.contentPane;
    }

    public JTextField getTextFieldUpdateDay() {
        return textFieldUpdateDay;
    }

    public JTextField getTextFieldUpdateYear() {
        return textFieldUpdateYear;
    }

    public JTextField getTextFieldUpdateMonth() {
        return textFieldUpdateMonth;
    }

    public JTextField getTextFieldUpdateHour() {
        return textFieldUpdateHour;
    }

    public JTextField getTextFieldUpdateMinute() {
        return textFieldUpdateMinute;
    }

    public JTextField getTextFieldUpdateSecond() {
        return textFieldUpdateSecond;
    }

    public JTextField getTextFieldUpdatePrice() {
        return textFieldUpdatePrice;
    }

    public JButton getButtonAddServiceVisitDesc() {
        return buttonAddServiceVisitDesc;
    }

    public JScrollPane getScrollPaneServiceDesc() {
        return scrollPaneServiceDesc;
    }

    public JLabel getLabelUpdateSV() {
        return labelUpdateSV;
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

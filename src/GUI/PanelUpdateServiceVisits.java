package GUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

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

    public PanelUpdateServiceVisits(int i) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Service Visit #" + (i + 1));
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD));
        this.panelUpdateServiceVisit.setBorder(titledBorder);
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

    public JPanel getPanelServiceDescs() {
        return panelServiceDescs;
    }

    public JLabel getLabelUpdateSV() {
        return labelUpdateSV;
    }
}

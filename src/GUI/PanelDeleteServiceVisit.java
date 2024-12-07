package GUI;

import Model.ServiceVisit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class PanelDeleteServiceVisit {
    private JPanel contentPane;
    private JPanel panelDeleteServiceVisit;
    private JButton button;
    private JTextPane textPane;
    private JScrollPane scrollPane;

    public PanelDeleteServiceVisit(ServiceVisit serviceVisit, int i)  {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Service visit #" + (i + 1));
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD));
        this.panelDeleteServiceVisit.setBorder(titledBorder);

        this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.textPane.setText(serviceVisit.toStringAttributes());
        SwingUtilities.invokeLater(() -> {
            this.scrollPane.getViewport().setViewPosition(new Point(0, 0));
        });

        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    public JPanel getPanel() {
        return this.contentPane;
    }

    public JButton getButton() {
        return button;
    }
}

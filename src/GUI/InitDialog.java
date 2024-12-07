package GUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class InitDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldHeapFileName;
    private JTextField textFieldHashFileByIDName;
    private JTextField textFieldHashFileByLPName;
    private JTextPane textPane;
    private JTextField controlTextFieldHeapFileName;
    private JTextField controlTextFieldHashFileByIDName;
    private JTextField controlTextFieldHashFileByLPName;
    private JTextField textFieldGeneratorFileName;
    private JPanel panelData;
    private JPanel panelControlData;
    private JPanel panelGeneratorControlData;
    private static String heapFileName;
    private static String hashByIDFileName;
    private static String hashByLPFileName;
    private static String controlHeapFileName;
    private static String controlHashByIDFileName;
    private static String controlHashByLPFileName;
    private static String generatorFileName;

    public InitDialog() {
        setTitle("Init Auto Repair Shop Information System");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.initTextBorders();

        this.buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        this.buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        this.updateTextFields(this.textFieldHeapFileName, this.controlTextFieldHeapFileName);
        this.updateTextFields(this.textFieldHashFileByIDName, this.controlTextFieldHashFileByIDName);
        this.updateTextFields(this.textFieldHashFileByLPName, this.controlTextFieldHashFileByLPName);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        heapFileName = this.textFieldHeapFileName.getText();
        hashByIDFileName = this.textFieldHashFileByIDName.getText();
        hashByLPFileName = this.textFieldHashFileByLPName.getText();

        controlHeapFileName = this.controlTextFieldHeapFileName.getText();
        controlHashByIDFileName = this.controlTextFieldHashFileByIDName.getText();
        controlHashByLPFileName = this.controlTextFieldHashFileByLPName.getText();

        generatorFileName = this.textFieldGeneratorFileName.getText();

        if (heapFileName.isBlank() || hashByIDFileName.isBlank() || hashByLPFileName.isBlank() ||
                controlHeapFileName.isBlank() || controlHashByIDFileName.isBlank() || controlHashByLPFileName.isBlank() ||
                generatorFileName.isBlank()) {
            this.textPane.setText("Please enter all file names!");
            return;
        }

        this.textPane.setText("");

        dispose();
    }

    private void onCancel() {
        heapFileName = null;
        hashByIDFileName = null;
        hashByLPFileName = null;
        controlHeapFileName = null;
        controlHashByIDFileName = null;
        controlHashByLPFileName = null;
        generatorFileName = null;

        dispose();
    }

    public static String[] showInitDialog() {
        InitDialog dialog = new InitDialog();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (heapFileName == null || hashByIDFileName == null || hashByLPFileName == null ||
                controlHeapFileName == null || controlHashByIDFileName == null || controlHashByLPFileName == null ||
                generatorFileName == null)
            return null;

        return new String[]{heapFileName, hashByIDFileName, hashByLPFileName, controlHeapFileName, controlHashByIDFileName, controlHashByLPFileName, generatorFileName};
    }

    private void updateTextFields(JTextField fromTextField, JTextField toTextField) {
        fromTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateControlTextField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateControlTextField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateControlTextField();
            }

            private void updateControlTextField() {
                toTextField.setText(fromTextField.getText());
            }
        });
    }

    private void initTextBorders() {
        TitledBorder titledBorderData= BorderFactory.createTitledBorder("Store data/Import stored data");
        titledBorderData.setTitleFont(titledBorderData.getTitleFont().deriveFont(Font.BOLD));
        this.panelData.setBorder(titledBorderData);

        TitledBorder titledBorderControlData= BorderFactory.createTitledBorder("Store control data/Import control data");
        titledBorderControlData.setTitleFont(titledBorderControlData.getTitleFont().deriveFont(Font.BOLD));
        this.panelControlData.setBorder(titledBorderControlData);

        TitledBorder titledBorderGeneratorControlData= BorderFactory.createTitledBorder("Store generator control data/Import generator control data");
        titledBorderGeneratorControlData.setTitleFont(titledBorderGeneratorControlData.getTitleFont().deriveFont(Font.BOLD));
        this.panelGeneratorControlData.setBorder(titledBorderGeneratorControlData);
    }
}

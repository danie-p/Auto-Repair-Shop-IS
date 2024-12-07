package GUI;

import javax.swing.*;
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
    private static String heapFileName;
    private static String hashByIDFileName;
    private static String hashByLPFileName;
    private static String controlHeapFileName;
    private static String controlHashByIDFileName;
    private static String controlHashByLPFileName;

    public InitDialog() {
        setTitle("Init Auto Repair Shop Information System");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

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

        if (heapFileName.isBlank() || hashByIDFileName.isBlank() || hashByLPFileName.isBlank() ||
                controlHeapFileName.isBlank() || controlHashByIDFileName.isBlank() || controlHashByLPFileName.isBlank()) {
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

        dispose();
    }

    public static String[] showInitDialog() {
        InitDialog dialog = new InitDialog();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (heapFileName == null || hashByIDFileName == null || hashByLPFileName == null ||
                controlHeapFileName == null || controlHashByIDFileName == null || controlHashByLPFileName == null)
            return null;

        return new String[]{heapFileName, hashByIDFileName, hashByLPFileName, controlHeapFileName, controlHashByIDFileName, controlHashByLPFileName};
    }
}

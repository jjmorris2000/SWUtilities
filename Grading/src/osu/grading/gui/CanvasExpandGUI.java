package osu.grading.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CanvasExpandGUI extends JFrame implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private CanvasExpandController controller;

    // Buttons
    private JButton archiveButton;
    private JButton outputButton;
    private JButton expandButton;

    // Text Fields
    private JTextField archiveField;
    private JTextField outputField;

    private JTextArea loggingArea;

    // File Chooser
    private final JFileChooser fc;

    public CanvasExpandGUI() {
        super("Canvas Expander");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {

            e.printStackTrace();
            System.exit(ABORT);
        }
        this.fc = new JFileChooser();

        this.setLayout(new GridBagLayout());

        // Create widgets
        JPanel inputFilePanel = new JPanel();
        inputFilePanel.add(new JLabel("Archive File:    "));
        this.archiveField = new JTextField(40);
        inputFilePanel.add(this.archiveField);
        this.archiveButton = new JButton("Choose");
        inputFilePanel.add(this.archiveButton);
        this.archiveButton.addActionListener(this);
        this.archiveField.addActionListener(this);

        JPanel outputDirPanel = new JPanel();
        outputDirPanel.add(new JLabel("Output Folder: "));
        this.outputField = new JTextField(40);
        outputDirPanel.add(this.outputField);
        this.outputButton = new JButton("Choose");
        outputDirPanel.add(this.outputButton);
        this.outputButton.addActionListener(this);
        this.outputField.addActionListener(this);

        JPanel expandPanel = new JPanel();
        this.expandButton = new JButton("Expand");
        this.expandButton.setEnabled(false);
        this.expandButton.addActionListener(this);
        expandPanel.add(this.expandButton);

        JPanel ioPanel = new JPanel(new GridLayout(3, 1));
        ioPanel.add(inputFilePanel);
        ioPanel.add(outputDirPanel);
        ioPanel.add(expandPanel);

        JPanel loggingPanel = new JPanel();
        this.loggingArea = new JTextArea(10, 60);
        this.loggingArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(this.loggingArea);
        loggingPanel.add(scrollPane);

        PrintStream outputStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                CanvasExpandGUI.this.loggingArea.append(String.valueOf((char) b));
            }
        });
        System.setOut(outputStream);
        System.setErr(outputStream);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        this.add(ioPanel, gbc);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridy = 1;
        gbc2.weighty = 1;
        gbc2.fill = GridBagConstraints.VERTICAL;
        this.add(loggingPanel, gbc2);

        // start the view
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void registerController(CanvasExpandController controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == this.archiveButton) {
            this.fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = this.fc.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = this.fc.getSelectedFile();
                this.archiveField.setText(file.toString());
            }
        }
        if (e.getSource() == this.outputButton) {
            this.fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = this.fc.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = this.fc.getSelectedFile();
                this.outputField.setText(file.toString());
            }
        }
        if (!this.outputField.getText().isBlank()
                && !this.archiveField.getText().isBlank()) {
            this.expandButton.setEnabled(true);
        } else {
            this.expandButton.setEnabled(false);
        }
        this.controller.updateModel(this.archiveField.getText(),
                this.outputField.getText());
        if (e.getSource() == this.expandButton) {
            this.controller.expand();
        }

    }

    public static void main(String[] args) {
        CanvasExpandGUI gui = new CanvasExpandGUI();
        CanvasExpandController controller = new CanvasExpandController(gui);
        gui.registerController(controller);
    }

}

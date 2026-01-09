package pcd.ass_single.part1;

import pcd.ass_single.part2.rmi.EventType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ExtractionView extends JFrame implements ActionListener, ModelObserver {
    private JTextField directoryPathField;
    private JTextField searchWordField;
    private JTextField countFiles;
    private JTextField countPdfFiles;
    private JTextField countPdfFilesWithWord;
    private final ExtractionController controller;

    public ExtractionView(ExtractionController controller) {
        this.controller = controller;

        setSize(400, 60);
        setResizable(false);

        // Input fields for directory and word
        directoryPathField = new JTextField(20);
        directoryPathField.setToolTipText("Enter directory path");
        searchWordField = new JTextField(15);
        searchWordField.setToolTipText("Enter word to search");

        // Start button
        JButton startButton = new JButton("Start Extraction");
        startButton.addActionListener(e -> handleExtraction(ExtractionEventType.START));

//        JButton startButton = new JButton("Start Extraction");
//        startButton.addActionListener(e -> handleExtraction(ExtractionEventType.START));
//
//        JButton startButton = new JButton("Start Extraction");
//        startButton.addActionListener(e -> handleExtraction(ExtractionEventType.START));
//
//
//        JButton startButton = new JButton("Start Extraction");
//        startButton.addActionListener(e -> handleExtraction(ExtractionEventType.START));

        countFiles = new JTextField(10);
        countPdfFiles = new JTextField(10);
        countPdfFilesWithWord = new JTextField(10);

        JPanel panel = new JPanel();
        panel.add(countFiles);
        panel.add(countPdfFiles);
        panel.add(countPdfFilesWithWord);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }
        });
    }

    private void handleExtraction(ExtractionEventType action) {
        String directoryPath = directoryPathField.getText();
        String searchWord = searchWordField.getText();
        if (directoryPath.isEmpty() || searchWord.isEmpty()) {
            log("Input Error", "please eneter valid inputs", JOptionPane.WARNING_MESSAGE);
        } else {
            controller.processEvent(new ExtractionEvent(ExtractionEventType.START, directoryPath, searchWord));
        }
    }

    @Override
    public void modelUpdated(ExtractionModel model) {
        SwingUtilities.invokeLater(() -> {
            countFiles.setText("num files: " + model.getCountFiles());
            countPdfFiles.setText("num pdf files: " + model.getCountPdfFiles());
            countPdfFilesWithWord.setText("num pdf files with word" + model.getCountPdfFilesWithWord());
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log("ok", "this is the action performed: " + e.toString(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void log(String title, String msg, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}

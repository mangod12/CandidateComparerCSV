import model.Candidate;
import service.CsvParser;
import service.CandidateComparator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Candidate Comparer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Modern flat look
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 250));
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel masterLabel = new JLabel("Master File:");
        JTextField masterPathField = new JTextField();
        JButton browseMasterButton = new JButton("Browse");

        JLabel changesLabel = new JLabel("Changes File:");
        JTextField changesPathField = new JTextField();
        JButton browseChangesButton = new JButton("Browse");

        JLabel outputDirLabel = new JLabel("Output Directory:");
        JTextField outputDirField = new JTextField("output");
        JButton runButton = new JButton("Run Comparison");

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField();

        // Table for displaying changes
        String[] columnNames = {"Candidate ID", "Field", "Master Value", "Changes Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable changesTable = new JTable(tableModel);
        changesTable.setFillsViewportHeight(true);
        changesTable.setRowHeight(28);
        changesTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        changesTable.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        changesTable.setShowGrid(false);
        changesTable.setIntercellSpacing(new Dimension(0, 0));
        changesTable.setSelectionBackground(new Color(220, 240, 255));
        changesTable.setSelectionForeground(Color.BLACK);
        changesTable.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane tableScroll = new JScrollPane(changesTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        changesTable.setRowSorter(sorter);

        JTextArea resultArea = new JTextArea(4, 20);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        resultArea.setBackground(new Color(245, 245, 250));
        resultArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Browse buttons
        browseMasterButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                masterPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        browseChangesButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                changesPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        // Search filter
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Run comparison
        runButton.addActionListener(e -> {
            String masterPath = masterPathField.getText();
            String changesPath = changesPathField.getText();
            String outputDir = outputDirField.getText();

            if (masterPath.isEmpty() || changesPath.isEmpty() || outputDir.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                File outputDirFile = new File(outputDir);
                if (!outputDirFile.exists() && !outputDirFile.mkdir()) {
                    throw new IOException("Failed to create output directory.");
                }

                String outputPath = outputDir + File.separator + "differences.txt";
                String logPath = outputDir + File.separator + "log.txt";

                Map<String, Candidate> masterData = CsvParser.parseCSV(masterPath);
                Map<String, Candidate> changesData = CsvParser.parseCSV(changesPath);
                // Get differences for table display
                List<String[]> differences = CandidateComparator.getDifferencesList(masterData, changesData);
                // Write differences to file as before
                CandidateComparator.compareAndWriteDifferences(masterData, changesData, outputPath, logPath);

                // Update table
                tableModel.setRowCount(0);
                for (String[] diff : differences) {
                    tableModel.addRow(diff);
                }
                resultArea.setText("Comparison completed.\nSee differences: " + outputPath + "\nSee log: " + logPath);
            } catch (Exception ex) {
                resultArea.setText("An error occurred: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Layout
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(masterLabel, 100, 100, 100)
                                .addComponent(masterPathField)
                                .addComponent(browseMasterButton, 90, 90, 90))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(changesLabel, 100, 100, 100)
                                .addComponent(changesPathField)
                                .addComponent(browseChangesButton, 90, 90, 90))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(outputDirLabel, 100, 100, 100)
                                .addComponent(outputDirField)
                                .addComponent(runButton, 120, 120, 120))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(searchLabel, 100, 100, 100)
                                .addComponent(searchField))
                        .addComponent(tableScroll)
                        .addComponent(resultScroll)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(masterLabel)
                                .addComponent(masterPathField)
                                .addComponent(browseMasterButton))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(changesLabel)
                                .addComponent(changesPathField)
                                .addComponent(browseChangesButton))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(outputDirLabel)
                                .addComponent(outputDirField)
                                .addComponent(runButton))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(searchLabel)
                                .addComponent(searchField))
                        .addComponent(tableScroll)
                        .addComponent(resultScroll)
        );

        // Button styles
        browseMasterButton.setBackground(new Color(60, 120, 216));
        browseMasterButton.setForeground(Color.WHITE);
        browseMasterButton.setFocusPainted(false);
        browseMasterButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        browseMasterButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        browseMasterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseMasterButton.setBorder(BorderFactory.createLineBorder(new Color(60, 120, 216), 2, true));
        browseMasterButton.setOpaque(true);

        browseChangesButton.setBackground(new Color(60, 120, 216));
        browseChangesButton.setForeground(Color.WHITE);
        browseChangesButton.setFocusPainted(false);
        browseChangesButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        browseChangesButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        browseChangesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseChangesButton.setBorder(BorderFactory.createLineBorder(new Color(60, 120, 216), 2, true));
        browseChangesButton.setOpaque(true);

        runButton.setBackground(new Color(40, 180, 99));
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);
        runButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        runButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        runButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        runButton.setBorder(BorderFactory.createLineBorder(new Color(40, 180, 99), 2, true));
        runButton.setOpaque(true);

        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        frame.add(panel);
        frame.setVisible(true);
    }
}

// ModernGUI.java
// A modern Swing GUI for flexible CSV comparison with live search, concise changes table, and output file management.
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class ModernGUI extends JFrame {
    // GUI fields for file paths, key, ignore columns, output dir, and search
    private JTextField masterFileField, changesFileField, keyField, ignoreField, outputDirField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    private java.util.List<String[]> allRows = new ArrayList<>(); // Stores all table rows for search

    public ModernGUI() {
        // Set up main window
        setTitle("Modern CSV Comparison Tool");
        setSize(900, 600); // Window size
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 255));

        // Top input panel for file selection and options
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(245, 247, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Input fields and buttons
        masterFileField = new JTextField("resources" + File.separator + "master.csv.csv", 30);
        changesFileField = new JTextField("resources" + File.separator + "changes.csv.csv", 30);
        keyField = new JTextField("SAIL_PERNO", 10);
        ignoreField = new JTextField("YYYYMM", 15);
        outputDirField = new JTextField("output", 10);
        JButton browseMasterBtn = new JButton("Browse");
        JButton browseChangesBtn = new JButton("Browse");
        JButton runBtn = new JButton("Run Comparison");
        // Layout for input panel
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Master CSV:"), gbc);
        gbc.gridx = 1; inputPanel.add(masterFileField, gbc);
        gbc.gridx = 2; inputPanel.add(browseMasterBtn, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Changes CSV:"), gbc);
        gbc.gridx = 1; inputPanel.add(changesFileField, gbc);
        gbc.gridx = 2; inputPanel.add(browseChangesBtn, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Key Column:"), gbc);
        gbc.gridx = 1; inputPanel.add(keyField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Ignore Columns (comma separated):"), gbc);
        gbc.gridx = 1; inputPanel.add(ignoreField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Output Dir:"), gbc);
        gbc.gridx = 1; inputPanel.add(outputDirField, gbc);
        gbc.gridx = 2; inputPanel.add(runBtn, gbc);
        add(inputPanel, BorderLayout.NORTH);

        // Search panel above concise changes table
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(245, 247, 255));
        searchField = new JTextField(30);
        JButton searchBtn = new JButton("Search");
        JButton clearSearchBtn = new JButton("Clear");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearSearchBtn);
        // Center panel holds search and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 247, 255));
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table for concise output: Key, Name, Column, Old Value, New Value, Change Type
        String[] columns = {"Key", "Name", "Column", "Old Value", "New Value", "Change Type"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel) {
            // Color rows based on change type
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    String changeType = getModel().getValueAt(row, 5).toString();
                    if ("New Joinee".equals(changeType)) {
                        c.setBackground(new Color(220, 255, 220));
                    } else if ("Changed".equals(changeType)) {
                        c.setBackground(new Color(255, 255, 200));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(new Color(180, 200, 255));
                }
                return c;
            }
        };
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Concise Changes (changenew.csv, new_joinees.csv)"));
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        // Message area below concise changes
        JLabel messageLabel = new JLabel();
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(new Color(60, 60, 120));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        centerPanel.add(messageLabel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Right panel for output viewing and clearing
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(245, 247, 255));
        JButton viewChangesBtn = new JButton("View Changes File");
        JButton viewCountsBtn = new JButton("View Counts File");
        JButton viewNewJoineesBtn = new JButton("View New Joinees File");
        JButton clearBtn = new JButton("Clear Output Files");
        rightPanel.add(viewChangesBtn);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(viewCountsBtn);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(viewNewJoineesBtn);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(clearBtn);
        add(rightPanel, BorderLayout.EAST);

        // File selection actions
        browseMasterBtn.addActionListener(e -> selectFile(masterFileField));
        browseChangesBtn.addActionListener(e -> selectFile(changesFileField));

        // Run comparison: triggers comparison, loads table, and shows output file info
        runBtn.addActionListener(e -> {
            runComparison();
            String outputDir = outputDirField.getText().trim();
            StringBuilder msg = new StringBuilder();
            msg.append("Output files generated in: ").append(new File(outputDir).getAbsolutePath()).append(" | ");
            msg.append("changenew.csv, count.csv, new_joinees.csv");
            messageLabel.setText(msg.toString());
        });

        // Live search: update table as user types
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchTable(); }
        });
        // Clear search
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            updateTable(allRows);
        });

        // View file actions for output files
        viewChangesBtn.addActionListener(e -> openFileInTable(new File(outputDirField.getText(), "changenew.csv")));
        viewCountsBtn.addActionListener(e -> openFileInText(new File(outputDirField.getText(), "count.csv")));
        viewNewJoineesBtn.addActionListener(e -> openFileInText(new File(outputDirField.getText(), "new_joinees.csv")));
        clearBtn.addActionListener(e -> clearGeneratedFiles());
    }

    // File chooser for selecting CSVs
    private void selectFile(JTextField field) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    // Run the flexible comparison and load concise changes table
    private void runComparison() {
        String master = masterFileField.getText();
        String changes = changesFileField.getText();
        String key = keyField.getText().trim();
        String ignoreCols = ignoreField.getText().trim();
        String outputDir = outputDirField.getText().trim();
        java.util.List<String> ignoreList = new ArrayList<>();
        if (!ignoreCols.isEmpty()) {
            for (String s : ignoreCols.split(",")) ignoreList.add(s.trim());
        }
        try {
            service.CandidateComparator.compareFlexible(master, changes, key, ignoreList, outputDir);
            loadTable(new File(outputDir, "changenew.csv"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load concise changes and new joinees into the table, including Name lookup
    private void loadTable(File file) {
        allRows.clear();
        tableModel.setRowCount(0);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length == 4) {
                    // Try to get name from master or changes file
                    String name = lookupName(parts[0]);
                    String[] row = new String[6];
                    row[0] = parts[0]; // Key
                    row[1] = name != null ? name : "";
                    row[2] = parts[1]; // Column
                    row[3] = parts[2]; // Old Value
                    row[4] = parts[3]; // New Value
                    row[5] = "Changed";
                    allRows.add(row);
                }
            }
        } catch (Exception ignored) {}
        // Also load new joinees if present
        File newJoineesFile = new File(outputDirField.getText(), "new_joinees.csv");
        if (newJoineesFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(newJoineesFile))) {
                String header = br.readLine();
                if (header != null) {
                    String[] headers = header.split(",");
                    int nameIdx = -1;
                    for (int i = 0; i < headers.length; i++) if (headers[i].equalsIgnoreCase("NAME")) nameIdx = i;
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] vals = line.split(",", -1);
                        if (vals.length == headers.length && vals.length > 0) {
                            String[] row = new String[6];
                            row[0] = vals[0]; // Key
                            row[1] = (nameIdx >= 0 && nameIdx < vals.length) ? vals[nameIdx] : "";
                            row[2] = "-";
                            row[3] = "-";
                            row[4] = "-";
                            row[5] = "New Joinee";
                            allRows.add(row);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        updateTable(allRows);
    }

    // Helper to look up name from master or changes file by key
    private String lookupName(String key) {
        // Try changes file first, then master
        String changesPath = changesFileField.getText();
        String masterPath = masterFileField.getText();
        for (String path : new String[]{changesPath, masterPath}) {
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                String header = br.readLine();
                if (header == null) continue;
                String[] headers = header.split(",");
                int keyIdx = -1, nameIdx = -1;
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].equalsIgnoreCase(keyField.getText().trim())) keyIdx = i;
                    if (headers[i].equalsIgnoreCase("NAME")) nameIdx = i;
                }
                if (keyIdx == -1 || nameIdx == -1) continue;
                String line;
                while ((line = br.readLine()) != null) {
                    String[] vals = line.split(",", -1);
                    if (vals.length > Math.max(keyIdx, nameIdx) && vals[keyIdx].equals(key)) {
                        return vals[nameIdx];
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    // Update the table with the given rows
    private void updateTable(java.util.List<String[]> rows) {
        tableModel.setRowCount(0);
        for (String[] row : rows) tableModel.addRow(row);
    }

    // Live search: filter table as user types
    private void searchTable() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            updateTable(allRows);
            return;
        }
        java.util.List<String[]> filtered = new ArrayList<>();
        for (String[] row : allRows) {
            boolean match = false;
            for (String val : row) if (val.toLowerCase().contains(keyword)) match = true;
            if (match) filtered.add(row);
        }
        updateTable(filtered);
    }

    // Show output file content in a dialog (for counts/new joinees)
    private void openFileInText(File file) {
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "File not found: " + file.getAbsolutePath(), "File Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to open file:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
        JTextArea textArea = new JTextArea();
        textArea.setText(sb.toString());
        textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        JOptionPane.showMessageDialog(this, scrollPane, "File Content", JOptionPane.INFORMATION_MESSAGE);
    }

    // Show output file content in the table (for concise changes)
    private void openFileInTable(File file) {
        allRows.clear();
        tableModel.setRowCount(0);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "File not found: " + file.getAbsolutePath(), "File Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // header
            if (line == null) return;
            String[] headers = line.split(",");
            tableModel.setColumnIdentifiers(headers);
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == headers.length) {
                    allRows.add(parts);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to open file:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateTable(allRows);
    }

    // Clear all generated output files
    private void clearGeneratedFiles() {
        String outputDir = outputDirField.getText();
        java.util.List<String> cleared = new ArrayList<>();
        for (String fname : new String[]{"changenew.csv", "count.csv", "new_joinees.csv"}) {
            File file = new File(outputDir, fname);
            if (file.exists()) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                    // clear file
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to clear " + fname + ":\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
                cleared.add(file.getAbsolutePath());
            }
        }
        if (!cleared.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cleared content of:\n" + String.join("\n", cleared), "Files Cleared", JOptionPane.INFORMATION_MESSAGE);
            tableModel.setRowCount(0);
        } else {
            JOptionPane.showMessageDialog(this, "No generated files found to clear.", "No Files", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Main entry point: launch the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModernGUI().setVisible(true));
    }
}

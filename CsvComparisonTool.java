import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class CsvComparisonTool extends JFrame {
    private JTextField masterFileField, changesFileField;
    private JTable table;
    private DefaultTableModel tableModel;
    private Map<String, String> generatedFiles = new HashMap<>();
    private java.util.List<String[]> currentTableData = new ArrayList<>();
    private final String KEY = "UNIT_PERNO";

    public CsvComparisonTool() {
        setTitle("CSV Comparison Tool");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(0, 1));
        masterFileField = new JTextField(60);
        changesFileField = new JTextField(60);
        JButton browseMasterBtn = new JButton("Browse...");
        JButton browseChangesBtn = new JButton("Browse...");
        JButton runBtn = new JButton("Run Comparison");
        JButton viewChangesBtn = new JButton("View Changes File");
        JButton viewCountsBtn = new JButton("View Counts File");
        JButton viewNewJoineesBtn = new JButton("View New Joinees File");
        JButton clearBtn = new JButton("Clear File Contents");

        JPanel masterPanel = new JPanel();
        masterPanel.add(new JLabel("Master CSV File:"));
        masterPanel.add(masterFileField);
        masterPanel.add(browseMasterBtn);
        topPanel.add(masterPanel);

        JPanel changesPanel = new JPanel();
        changesPanel.add(new JLabel("Changes CSV File:"));
        changesPanel.add(changesFileField);
        changesPanel.add(browseChangesBtn);
        topPanel.add(changesPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(runBtn);
        buttonPanel.add(viewChangesBtn);
        buttonPanel.add(viewCountsBtn);
        buttonPanel.add(viewNewJoineesBtn);
        buttonPanel.add(clearBtn);
        topPanel.add(buttonPanel);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(40);
        JButton searchBtn = new JButton("Search");
        JButton clearSearchBtn = new JButton("Clear");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearSearchBtn);
        add(searchPanel, BorderLayout.SOUTH);

        browseMasterBtn.addActionListener(e -> selectFile(masterFileField));
        browseChangesBtn.addActionListener(e -> selectFile(changesFileField));
        runBtn.addActionListener(e -> runComparison());
        viewChangesBtn.addActionListener(e -> openFileInTable("changes"));
        viewCountsBtn.addActionListener(e -> openFileInTable("counts"));
        viewNewJoineesBtn.addActionListener(e -> openFileInTable("new_joinees"));
        clearBtn.addActionListener(e -> clearGeneratedFiles());
        searchBtn.addActionListener(e -> searchTable(searchField.getText()));
        clearSearchBtn.addActionListener(e -> clearSearch());
    }

    private void selectFile(JTextField field) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void runComparison() {
        String master = masterFileField.getText();
        String changes = changesFileField.getText();
        if (master.isEmpty() || changes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select both CSV files.", "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String[] paths = compareFiles(master, changes);
            String message = "Comparison complete!\n\nChanges: " + paths[0] + "\nCounts: " + paths[1];
            if (paths[2] != null) message += "\nNew Joinees: " + paths[2];
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] compareFiles(String masterPath, String changesPath) throws IOException {
        Map<String, Map<String, String>> data = readFile(masterPath);
        Map<String, Map<String, String>> data1 = readFile(changesPath);
        java.util.List<String[]> changes = new ArrayList<>();
        Map<String, Integer> count = new HashMap<>();
        java.util.List<Map<String, String>> newJoinees = new ArrayList<>();
        java.util.List<String> headers = getHeaders(masterPath);
        java.util.List<String> filteredHeaders = new ArrayList<>();
        for (String col : headers) if (!col.equals("YYYYMM")) filteredHeaders.add(col);

        for (String key1 : data.keySet()) {
            if (data1.containsKey(key1)) {
                for (String column : filteredHeaders) {
                    if (column.equals(KEY)) continue;
                    String v1 = data.get(key1).get(column);
                    String v2 = data1.get(key1).get(column);
                    if (!Objects.equals(v1, v2)) {
                        changes.add(new String[]{key1, column, v1, v2});
                        count.put(column, count.getOrDefault(column, 0) + 1);
                    }
                }
            }
        }
        for (String key1 : data1.keySet()) {
            if (!data.containsKey(key1)) {
                Map<String, String> filteredRow = new HashMap<>();
                for (String k : filteredHeaders) filteredRow.put(k, data1.get(key1).get(k));
                newJoinees.add(filteredRow);
            }
        }
        File baseDir = new File(masterPath).getParentFile();
        String changesFile = new File(baseDir, "changenew.csv").getAbsolutePath();
        String countFile = new File(baseDir, "count.csv").getAbsolutePath();
        String newJoineesFile = new File(baseDir, "new_joinees.csv").getAbsolutePath();

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(changesFile), "UTF-8"))) {
            pw.println(KEY + ",column,old_value,new_value");
            for (String[] row : changes) {
                pw.println(String.join(",", row));
            }
        }
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(countFile), "UTF-8"))) {
            pw.println("column,count");
            for (String column : count.keySet()) {
                pw.println(column + "," + count.get(column));
            }
            if (!newJoinees.isEmpty()) {
                pw.println("new joinee," + newJoinees.size());
            }
        }
        if (!newJoinees.isEmpty()) {
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(newJoineesFile), "UTF-8"))) {
                pw.println(String.join(",", filteredHeaders));
                for (Map<String, String> row : newJoinees) {
                    java.util.List<String> vals = new ArrayList<>();
                    for (String h : filteredHeaders) vals.add(row.getOrDefault(h, ""));
                    pw.println(String.join(",", vals));
                }
            }
        } else {
            newJoineesFile = null;
        }
        generatedFiles.put("changes", changesFile);
        generatedFiles.put("counts", countFile);
        generatedFiles.put("new_joinees", newJoineesFile);
        return new String[]{changesFile, countFile, newJoineesFile};
    }

    private Map<String, Map<String, String>> readFile(String filepath) throws IOException {
        Map<String, Map<String, String>> data = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"))) {
            String headerLine = br.readLine();
            if (headerLine == null) return data;
            String[] headers = headerLine.split(",");
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i], values[i]);
                }
                String rowKey = row.get(KEY);
                if (rowKey != null && !rowKey.isEmpty()) {
                    data.put(rowKey, row);
                }
            }
        }
        return data;
    }

    private java.util.List<String> getHeaders(String filepath) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"))) {
            String headerLine = br.readLine();
            if (headerLine == null) return new ArrayList<>();
            return Arrays.asList(headerLine.split(","));
        }
    }

    private void openFileInTable(String pathKey) {
        String path = generatedFiles.get(pathKey);
        if (path == null || !(new File(path).exists())) {
            JOptionPane.showMessageDialog(this, "The " + pathKey + " file is not available or hasn't been created yet.", "File Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                JOptionPane.showMessageDialog(this, pathKey + " file is empty.", "Empty File", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] headers = headerLine.split(",");
            tableModel.setColumnIdentifiers(headers);
            currentTableData.clear();
            tableModel.setRowCount(0);
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",", -1);
                tableModel.addRow(row);
                currentTableData.add(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to open file:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTable(String keyword) {
        keyword = keyword.toLowerCase();
        tableModel.setRowCount(0);
        for (String[] row : currentTableData) {
            for (String cell : row) {
                if (cell != null && cell.toLowerCase().contains(keyword)) {
                    tableModel.addRow(row);
                    break;
                }
            }
        }
    }

    private void clearSearch() {
        tableModel.setRowCount(0);
        for (String[] row : currentTableData) {
            tableModel.addRow(row);
        }
    }

    private void clearGeneratedFiles() {
        java.util.List<String> cleared = new ArrayList<>();
        for (String key : Arrays.asList("changes", "counts", "new_joinees")) {
            String path = generatedFiles.get(key);
            if (path != null && new File(path).exists()) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
                    // clear file
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to clear " + key + " file:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
                cleared.add(path);
            }
        }
        if (!cleared.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cleared content of:\n" + String.join("\n", cleared), "Files Cleared", JOptionPane.INFORMATION_MESSAGE);
            tableModel.setRowCount(0);
        } else {
            JOptionPane.showMessageDialog(this, "No generated files found to clear.", "No Files", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CsvComparisonTool().setVisible(true));
    }
}

package service;

import model.Candidate;
import java.util.*;
import java.io.*;

public class CandidateComparator {
    public static void compareAndWriteDifferences(
        Map<String, Candidate> master,
        Map<String, Candidate> changes,
        String outputPath,
        String logPath
    ) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
        BufferedWriter logWriter = new BufferedWriter(new FileWriter(logPath));

        // Sort SAIL_PERNO keys (only those present in both master and changes)
        Set<String> commonSailPernos = new HashSet<>();
        for (Candidate c : changes.values()) {
            if (master.values().stream().anyMatch(m -> m.sailPerno.equals(c.sailPerno))) {
                commonSailPernos.add(c.sailPerno);
            }
        }
        List<String> sailPernos = new ArrayList<>(commonSailPernos);
        Collections.sort(sailPernos);

        // Add debug logging to verify the contents of master and changes maps
        System.out.println("Master Data:");
        master.values().forEach(System.out::println);
        System.out.println("Changes Data:");
        changes.values().forEach(System.out::println);

        // Add debug logging to verify commonSailPernos
        System.out.println("Common SAIL_PERNOs:");
        commonSailPernos.forEach(System.out::println);

        int totalChanges = 0;
        for (String sailPerno : sailPernos) {
            Candidate newC = changes.values().stream().filter(c -> c.sailPerno.equals(sailPerno)).findFirst().orElse(null);
            if (newC == null) continue;
            Candidate oldC = master.values().stream().filter(c -> c.sailPerno.equals(sailPerno)).findFirst().orElse(null);
            if (oldC == null) continue; // Only show changes for existing SAIL_PERNO
            // Compare all fields except SAIL_PERNO, name, and yyyymm
            List<String> diffs = new ArrayList<>();
            if (!Objects.equals(oldC.unitCd, newC.unitCd))
                diffs.add("unitCd: '" + oldC.unitCd + "' -> '" + newC.unitCd + "'");
            if (!Objects.equals(oldC.unitPerno, newC.unitPerno))
                diffs.add("unitPerno: '" + oldC.unitPerno + "' -> '" + newC.unitPerno + "'");
            // name and yyyymm changes are ignored
            if (!diffs.isEmpty()) {
                writer.write("SAIL_PERNO: " + sailPerno + "\n");
                for (String diff : diffs) {
                    writer.write("  " + diff + "\n");
                }
                logWriter.write("SAIL_PERNO: " + sailPerno + "\n");
                for (String diff : diffs) {
                    logWriter.write("  " + diff + "\n");
                }
                totalChanges++;
            }
        }
        writer.write("\nTotal SAIL_PERNO with changes: " + totalChanges + "\n");
        logWriter.write("\nTotal SAIL_PERNO with changes: " + totalChanges + "\n");
        writer.close();
        logWriter.close();
    }

    // Returns a list of differences for table display: [SAIL_PERNO, Field, Master Value, Changes Value]
    public static java.util.List<String[]> getDifferencesList(
            Map<String, Candidate> master,
            Map<String, Candidate> changes) {
        List<String[]> diffs = new ArrayList<>();
        // Only compare SAIL_PERNOs present in both master and changes
        Set<String> commonSailPernos = new HashSet<>();
        for (Candidate c : changes.values()) {
            if (master.values().stream().anyMatch(m -> m.sailPerno.equals(c.sailPerno))) {
                commonSailPernos.add(c.sailPerno);
            }
        }
        List<String> sailPernos = new ArrayList<>(commonSailPernos);
        Collections.sort(sailPernos);
        for (String sailPerno : sailPernos) {
            Candidate newC = changes.values().stream().filter(c -> c.sailPerno.equals(sailPerno)).findFirst().orElse(null);
            if (newC == null) continue;
            Candidate oldC = master.values().stream().filter(c -> c.sailPerno.equals(sailPerno)).findFirst().orElse(null);
            if (oldC == null) continue;
            if (!Objects.equals(oldC.unitCd, newC.unitCd))
                diffs.add(new String[]{sailPerno, "unitCd", oldC.unitCd, newC.unitCd});
            if (!Objects.equals(oldC.unitPerno, newC.unitPerno))
                diffs.add(new String[]{sailPerno, "unitPerno", oldC.unitPerno, newC.unitPerno});
            // name and yyyymm changes are ignored
        }
        return diffs;
    }

    /**
     * Flexible CSV comparison: allows any key column and columns to ignore.
     * Writes three files: changes, counts, new joinees (like CsvComparisonTool)
     */
    public static void compareFlexible(String masterPath, String changesPath, String keyColumn, List<String> columnsToIgnore, String outputDir) throws IOException {
        Map<String, Map<String, String>> master = readCsvToMap(masterPath, keyColumn);
        Map<String, Map<String, String>> changes = readCsvToMap(changesPath, keyColumn);
        List<String[]> changesList = new ArrayList<>();
        Map<String, Integer> count = new HashMap<>();
        List<Map<String, String>> newJoinees = new ArrayList<>();
        List<String> headers = getHeaders(masterPath);
        List<String> filteredHeaders = new ArrayList<>();
        for (String col : headers) if (!columnsToIgnore.contains(col)) filteredHeaders.add(col);

        for (String key : master.keySet()) {
            if (changes.containsKey(key)) {
                for (String column : filteredHeaders) {
                    if (column.equals(keyColumn)) continue;
                    String v1 = master.get(key).get(column);
                    String v2 = changes.get(key).get(column);
                    if (!Objects.equals(v1, v2)) {
                        changesList.add(new String[]{key, column, v1, v2});
                        count.put(column, count.getOrDefault(column, 0) + 1);
                    }
                }
            }
        }
        for (String key : changes.keySet()) {
            if (!master.containsKey(key)) {
                Map<String, String> filteredRow = new HashMap<>();
                for (String k : filteredHeaders) filteredRow.put(k, changes.get(key).get(k));
                newJoinees.add(filteredRow);
            }
        }
        File baseDir = new File(outputDir);
        if (!baseDir.exists()) baseDir.mkdirs();
        String changesFile = new File(baseDir, "changenew.csv").getAbsolutePath();
        String countFile = new File(baseDir, "count.csv").getAbsolutePath();
        String newJoineesFile = new File(baseDir, "new_joinees.csv").getAbsolutePath();

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(changesFile), "UTF-8"))) {
            pw.println(keyColumn + ",column,old_value,new_value");
            for (String[] row : changesList) {
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
                    List<String> vals = new ArrayList<>();
                    for (String h : filteredHeaders) vals.add(row.getOrDefault(h, ""));
                    pw.println(String.join(",", vals));
                }
            }
        }
    }

    private static Map<String, Map<String, String>> readCsvToMap(String filepath, String keyColumn) throws IOException {
        Map<String, Map<String, String>> data = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"))) {
            String headerLine = br.readLine();
            if (headerLine == null) return data;
            String[] headers = headerLine.split(",");
            int keyIdx = -1;
            for (int i = 0; i < headers.length; i++) if (headers[i].equals(keyColumn)) keyIdx = i;
            if (keyIdx == -1) throw new IllegalArgumentException("Key column not found: " + keyColumn);
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i], values[i]);
                }
                String rowKey = row.get(keyColumn);
                if (rowKey != null && !rowKey.isEmpty()) {
                    data.put(rowKey, row);
                }
            }
        }
        return data;
    }

    private static List<String> getHeaders(String filepath) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"))) {
            String headerLine = br.readLine();
            if (headerLine == null) return new ArrayList<>();
            return Arrays.asList(headerLine.split(","));
        }
    }
}

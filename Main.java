// Main.java
// Entry point for the CSV comparison tool. Runs both the classic and flexible comparison logic.
import model.Candidate;
import service.CsvParser;
import service.CandidateComparator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Set up file paths for master, changes, and output
        String masterPath = "resources" + File.separator + "master.csv.csv";
        String changesPath = "resources" + File.separator + "changes.csv.csv";
        String outputPath = "output" + File.separator + "differences.txt";
        String logPath = "output" + File.separator + "log.txt";

        try {
            // Ensure output directory exists
            File outputDir = new File("output");
            if (!outputDir.exists() && !outputDir.mkdir()) {
                throw new IOException("Failed to create output directory.");
            }

            // Parse both CSVs into Candidate maps (classic logic)
            Map<String, Candidate> masterData = CsvParser.parseCSV(masterPath);
            Map<String, Candidate> changesData = CsvParser.parseCSV(changesPath);
            // Compare and write differences for SAIL_PERNO (classic logic)
            CandidateComparator.compareAndWriteDifferences(masterData, changesData, outputPath, logPath);
            System.out.println("Comparison completed. See " + outputPath + " and " + logPath);

            // Flexible comparison usage example (can use any key and ignore columns)
            String keyColumn = "UNIT_PERNO"; // or "SAIL_PERNO" as needed
            List<String> columnsToIgnore = List.of("YYYYMM"); // add more columns to ignore if needed
            service.CandidateComparator.compareFlexible(masterPath, changesPath, keyColumn, columnsToIgnore, "output");
        } catch (Exception e) {
            System.err.println("An error occurred during comparison: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
import model.Candidate;
import service.CsvParser;
import service.CandidateComparator;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String masterPath = "C:\\Users\\ansha\\Downloads\\CandidateComparerCSV\\resources\\master.csv.csv";
        String changesPath = "C:\\Users\\ansha\\Downloads\\CandidateComparerCSV\\resources\\changes.csv.csv";
        String outputPath = "output" + File.separator + "differences.txt";
        String logPath = "output" + File.separator + "log.txt";

        try {
            // Ensure output directory exists
            File outputDir = new File("output");
            if (!outputDir.exists() && !outputDir.mkdir()) {
                throw new IOException("Failed to create output directory.");
            }

            Map<String, Candidate> masterData = CsvParser.parseCSV(masterPath);
            Map<String, Candidate> changesData = CsvParser.parseCSV(changesPath);
            CandidateComparator.compareAndWriteDifferences(masterData, changesData, outputPath, logPath);
            System.out.println("Comparison completed. See " + outputPath + " and " + logPath);
        } catch (Exception e) {
            System.err.println("An error occurred during comparison: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
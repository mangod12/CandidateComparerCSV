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

        int totalChanges = 0;
        Map<String, Integer> fieldChangeCount = new HashMap<>();
        fieldChangeCount.put("Name", 0);

        for (Candidate newC : changes.values()) {
            // Find by SAIL_PERNO, not just unitPerno
            Candidate oldC = null;
            for (Candidate c : master.values()) {
                if (c.sailPerno.equals(newC.sailPerno)) {
                    oldC = c;
                    break;
                }
            }
            if (oldC == null) {
                writer.write("NEW ENTRY: " + newC.name + " (" + newC.sailPerno + ")\n");
            }
            // Ignore all name changes and do not output them
        }

        logWriter.write("\nSummary of Changes:\n");
        logWriter.write("Total Changes: " + totalChanges + "\n");
        for (Map.Entry<String, Integer> entry : fieldChangeCount.entrySet()) {
            logWriter.write(entry.getKey() + " Changes: " + entry.getValue() + "\n");
        }
        writer.close();
        logWriter.close();
    }

    // Returns a list of differences for table display: [Candidate ID, Field, Master Value, Changes Value]
    public static java.util.List<String[]> getDifferencesList(
            Map<String, Candidate> master,
            Map<String, Candidate> changes) {
        java.util.List<String[]> diffs = new java.util.ArrayList<>();
        for (String id : changes.keySet()) {
            Candidate newC = changes.get(id);
            // Find by SAIL_PERNO, not just unitPerno
            Candidate oldC = null;
            for (Candidate c : master.values()) {
                if (c.sailPerno.equals(newC.sailPerno)) {
                    oldC = c;
                    break;
                }
            }
            if (oldC == null) {
                // New entry
                diffs.add(new String[]{newC.sailPerno, "NEW ENTRY", "", newC.name});
            }
            // Ignore all name changes and do not output them
        }
        return diffs;
    }
}


package service;

import model.Candidate;
import java.io.*;
import java.util.*;

public class CsvParser {
    public static Map<String, Candidate> parseCSV(String filePath) throws IOException {
        Map<String, Candidate> data = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine(); // skip header
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",", 5);
            if (parts.length == 5) {
                Candidate c = new Candidate(parts[0].trim(), parts[1].trim(),
                    parts[2].trim(), parts[3].trim(), parts[4].trim());
                data.put(c.uniqueId(), c);
            }
        }
        reader.close();
        return data;
    }
}

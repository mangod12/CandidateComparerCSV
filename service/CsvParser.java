package service;

import model.Candidate;
import java.io.*;
import java.util.*;

public class CsvParser {
    public static Map<String, Candidate> parseCSV(String filePath) throws IOException {
        Map<String, Candidate> data = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine(); // skip header
        System.out.println("Parsing file: " + filePath);
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",", -1); // Parse all columns
            if (parts.length >= 5) {
                Candidate c = new Candidate(parts[0].trim(), parts[1].trim(),
                    parts[2].trim(), parts[3].trim(), parts[4].trim());
                data.put(c.uniqueId(), c); // uniqueId is now sailPerno
            }
        }
        reader.close();
        data.forEach((key, value) -> System.out.println(key + " -> " + value));
        return data;
    }

    public static List<String> getHeaders(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            if (headerLine == null) return new ArrayList<>();
            return Arrays.asList(headerLine.split(","));
        }
    }

    public static Map<String, Map<String, String>> parseCSVToMap(String filePath, String keyColumn) throws IOException {
        Map<String, Map<String, String>> data = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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
                String rowKey = row.get(keyColumn);
                if (rowKey != null && !rowKey.isEmpty()) {
                    data.put(rowKey, row);
                }
            }
        }
        return data;
    }
}

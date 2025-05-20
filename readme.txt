This program is designed to compare two CSV files (master.csv.csv and changes.csv.csv) and output the differences to a file (differences.txt). Here's how it works step by step:

## Features
- Compare a master CSV file with a changes CSV file.
- Identify new entries and updated fields.
- Log differences and changes in separate output files.
- User-friendly GUI built with Java Swing.
- Allows users to browse and select files and directories.

## Requirements
- Java 8 or higher
- A CSV file format with the following columns:
  - `ID` (unique identifier)
  - `Name`
  - `YYYYMM` (date in `YYYYMM` format)

## How to Compile and Run

### 1. Compile the Project
Open a terminal or command prompt and navigate to the project directory:
```bash
cd c:\Users\ansha\Downloads\CandidateComparerCSV
```

Compile all `.java` files:
```bash
javac -d . model\Candidate.java service\CsvParser.java service\CandidateComparator.java MainGUI.java
```

### 2. Run the Application
Run the GUI application:
```bash
java MainGUI
```

### 3. Using the Application
1. Launch the application.
2. Use the "Browse Master File" button to select the master CSV file.
3. Use the "Browse Changes File" button to select the changes CSV file.
4. Specify the output directory (default is `output`).
5. Click "Run Comparison" to process the files.
6. View the results in the text area or check the output files:
   - `differences.txt`: Contains the differences between the files.
   - `log.txt`: Contains a summary of changes.

## Example CSV Format
### Master CSV (`master.csv`)
```csv
ID,Name,YYYYMM
1,John Doe,202301
2,Jane Smith,202302
```

### Changes CSV (`changes.csv`)
```csv
ID,Name,YYYYMM
1,John Doe,202304
3,Emily Davis,202305
```

## Output Files
### `differences.txt`
```
UPDATED: 1
 - YYYYMM: 202301 -> 202304
NEW ENTRY: Emily Davis (3)
```

### `log.txt`
```
Summary of Changes:
Total Changes: 2
Name Changes: 0
YYYYMM Changes: 1
```

## Troubleshooting
- **Error: Could not find or load main class MainGUI**
  - Ensure the directory structure matches the package names.
  - Compile the files with the `javac` command as shown above.
- **Output directory not created**
  - Ensure you have write permissions in the specified directory.

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

## Author
Created by [Your Name].


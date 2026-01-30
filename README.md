# CandidateComparerCSV 📊

A Java-based CSV comparison tool with a modern GUI that identifies differences, changes, and new entries between two CSV files. Perfect for tracking changes in candidate records, employee data, or any structured CSV datasets.

## 🌟 Features

- **Flexible CSV Comparison**: Compare CSV files using any column as a unique key identifier
- **Modern GUI Interface**: User-friendly Swing-based interface with real-time search and color-coded results
- **Change Detection**: Automatically identifies:
  - Updated field values
  - New entries (joinees)
  - Changed records with detailed field-by-field differences
- **Column Filtering**: Specify columns to ignore during comparison (e.g., timestamps)
- **Multiple Output Formats**:
  - `changenew.csv`: Detailed changes with old and new values
  - `count.csv`: Summary of changes per field
  - `new_joinees.csv`: List of new entries
- **Real-time Search**: Filter results instantly as you type
- **File Management**: View and clear output files directly from the GUI
- **Command-Line Support**: Run comparisons via CLI for automation

## 📋 Requirements

- **Java 8 or higher** (Java Development Kit)
- CSV files with consistent structure
- Recommended: A unique identifier column (e.g., ID, Employee Number)

## 🚀 Getting Started

### Installation

1. Clone the repository:
```bash
git clone https://github.com/mangod12/CandidateComparerCSV.git
cd CandidateComparerCSV
```

2. Compile the project:
```bash
javac -d . model/Candidate.java service/CsvParser.java service/CandidateComparator.java ModernGUI.java Main.java
```

### Running the Application

#### Option 1: GUI Application (Recommended)

Launch the graphical interface:
```bash
java ModernGUI
```

#### Option 2: Command-Line Interface

Run comparison directly from terminal:
```bash
java Main
```

## 💻 Usage

### Using the GUI

1. **Launch the Application**
   ```bash
   java ModernGUI
   ```

2. **Select Files**
   - Click "Browse" next to "Master CSV" to select your baseline CSV file
   - Click "Browse" next to "Changes CSV" to select the updated CSV file

3. **Configure Comparison**
   - **Key Column**: Enter the column name to use as unique identifier (e.g., `SAIL_PERNO`, `ID`)
   - **Ignore Columns**: Enter comma-separated column names to exclude from comparison (e.g., `YYYYMM,LastModified`)
   - **Output Dir**: Specify where to save results (default: `output`)

4. **Run Comparison**
   - Click "Run Comparison"
   - View results in the color-coded table:
     - 🟢 **Green rows**: New entries
     - 🟡 **Yellow rows**: Changed values
   - Use the search box to filter results

5. **View Output Files**
   - Click "View Changes File" to see detailed changes
   - Click "View Counts File" to see summary statistics
   - Click "View New Joinees File" to see new entries
   - Click "Clear Output Files" to reset output directory

### Using the Command Line

Modify `Main.java` to set your file paths and comparison parameters:

```java
String masterPath = "path/to/master.csv";
String changesPath = "path/to/changes.csv";
String keyColumn = "ID";
List<String> columnsToIgnore = List.of("YYYYMM", "LastModified");
```

Then run:
```bash
java Main
```

## 📁 CSV Format

### Expected Structure

Your CSV files should have a header row with column names:

```csv
SAIL_PERNO,NAME,UNIT_PERNO,YYYYMM
12345,John Doe,67890,202401
23456,Jane Smith,78901,202401
```

### Example Files

**Master CSV** (`master.csv`):
```csv
ID,Name,Department,YYYYMM
1,John Doe,Engineering,202301
2,Jane Smith,Marketing,202302
3,Bob Johnson,Sales,202303
```

**Changes CSV** (`changes.csv`):
```csv
ID,Name,Department,YYYYMM
1,John Doe,Engineering,202305
2,Jane Smith,Product,202305
4,Alice Williams,HR,202305
```

**Generated Output** (`changenew.csv`):
```csv
Key,Column,Old Value,New Value
1,YYYYMM,202301,202305
2,Department,Marketing,Product
2,YYYYMM,202302,202305
```

**New Entries** (`new_joinees.csv`):
```csv
ID,Name,Department,YYYYMM
4,Alice Williams,HR,202305
```

**Summary** (`count.csv`):
```csv
Column,Change Count
Department,1
YYYYMM,2
Total Changes,3
New Entries,1
```

## 🎨 GUI Features

### Color-Coded Results
- **Green Background**: New entries (joinees)
- **Yellow Background**: Modified records
- **White Background**: Default/unchanged
- **Blue Highlight**: Selected row

### Search Functionality
- Type in the search box to filter results in real-time
- Search across all columns (Key, Name, Column, Values, Change Type)
- Click "Clear" to reset search

### File Viewing
- View output files without leaving the application
- Large files displayed in scrollable dialogs
- Easy navigation between different output types

## ⚙️ Configuration

### Customizing the Comparison

The tool offers flexible configuration:

1. **Key Column Selection**: Use any column as the unique identifier
2. **Ignore Columns**: Exclude columns that shouldn't trigger change detection
3. **Output Directory**: Choose where to save results
4. **File Paths**: Use absolute or relative paths

### Default Settings

- Master File: `resources/master.csv.csv`
- Changes File: `resources/changes.csv.csv`
- Key Column: `SAIL_PERNO`
- Ignore Columns: `YYYYMM`
- Output Directory: `output`

## 🐛 Troubleshooting

### Common Issues

**Error: Could not find or load main class**
- Ensure you're in the correct directory
- Verify compilation was successful
- Check that `.class` files were generated

**CSV Parsing Errors**
- Ensure both CSV files have the same column structure
- Check for proper CSV formatting (comma-separated, no extra delimiters)
- Verify the key column exists in both files

**Output Directory Not Created**
- Check write permissions in the target directory
- Ensure the path is valid and accessible
- Try using an absolute path

**GUI Doesn't Display Results**
- Verify output files were created in the specified directory
- Check for error messages in the console
- Ensure CSV files are not empty

## 🏗️ Project Structure

```
CandidateComparerCSV/
├── model/
│   └── Candidate.java          # Data model for candidate records
├── service/
│   ├── CsvParser.java          # CSV parsing utilities
│   └── CandidateComparator.java # Comparison logic
├── resources/
│   ├── master.csv.csv          # Sample master file
│   └── changes.csv.csv         # Sample changes file
├── output/                     # Generated output files
│   ├── changenew.csv
│   ├── count.csv
│   └── new_joinees.csv
├── Main.java                   # CLI entry point
├── ModernGUI.java             # GUI application
└── README.md                   # This file
```

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

**Mangod12**
- GitHub: [@mangod12](https://github.com/mangod12)

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/mangod12/CandidateComparerCSV/issues).

## 📊 Use Cases

- **HR Management**: Track employee changes, new hires, and updates
- **Candidate Tracking**: Monitor candidate status changes in recruitment
- **Data Auditing**: Compare dataset versions and identify modifications
- **Change Monitoring**: Detect and log changes in regularly updated CSV exports
- **Data Migration**: Verify data transfers and identify discrepancies

## 🔮 Future Enhancements

- Export results to Excel format
- Support for additional file formats (TSV, pipe-delimited)
- Advanced filtering and grouping options
- Change history tracking over multiple comparisons
- Automated email notifications for changes
- Configuration file support for preset comparisons

---

**Note**: This tool is designed for CSV comparison and change detection. It does not modify your original CSV files - all outputs are generated separately.

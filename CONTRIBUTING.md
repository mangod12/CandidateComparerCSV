# Contributing to CandidateComparerCSV

First off, thank you for considering contributing to CandidateComparerCSV! It's people like you that make this tool better for everyone.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you are creating a bug report, please include as many details as possible:

* **Use a clear and descriptive title** for the issue
* **Describe the exact steps to reproduce the problem**
* **Provide specific examples** (sample CSV files if possible)
* **Describe the behavior you observed** and what you expected to see
* **Include screenshots** if relevant
* **Provide your Java version** and operating system

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* **A clear and descriptive title**
* **A detailed description of the proposed functionality**
* **Explain why this enhancement would be useful**
* **List any similar features in other tools** if applicable

### Pull Requests

* Fill in the required template
* Follow the Java coding style used in the project
* Include comments in your code where necessary
* Update the README.md if you change functionality
* Write meaningful commit messages
* Test your changes thoroughly before submitting

## Development Setup

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR-USERNAME/CandidateComparerCSV.git
   ```
3. Create a branch for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. Make your changes and test them
5. Compile and test:
   ```bash
   javac -d . model/Candidate.java service/CsvParser.java service/CandidateComparator.java ModernGUI.java Main.java
   java ModernGUI
   ```
6. Commit your changes:
   ```bash
   git commit -m "Add your descriptive commit message"
   ```
7. Push to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```
8. Open a Pull Request

## Code Style

* Use meaningful variable and method names
* Keep methods focused and concise
* Add comments for complex logic
* Follow existing code formatting conventions
* Use proper indentation (4 spaces)

## Testing

* Test your changes with various CSV file formats
* Verify GUI changes work correctly
* Test edge cases (empty files, malformed data, etc.)
* Ensure existing functionality still works

## Questions?

Feel free to open an issue with your question or reach out to the project maintainer.

Thank you for your contributions! 🎉

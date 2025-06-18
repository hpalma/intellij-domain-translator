# IntelliJ Domain Translator

[![Version](https://img.shields.io/jetbrains/plugin/v/21320.svg)](https://plugins.jetbrains.com/plugin/21320)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/21320.svg)](https://plugins.jetbrains.com/plugin/21320)

A powerful IntelliJ IDEA plugin that provides domain-specific translation functionality for developers working on codebases with foreign language domain terms. The plugin displays inline translation hints above code elements without modifying your source code.

## Features

- **Inline Translation Hints**: Displays translations directly above code elements containing translatable terms
- **Multi-language Support**: Works with Java, Kotlin, JavaScript, TypeScript, and Vue.js files
- **Smart Text Processing**: 
  - Preserves camelCase, snake_case, and other naming conventions
  - Handles compound words and multi-word translations
  - Supports German umlauts (ä, ö, ü, ß)
- **Custom Dictionary**: Uses CSV-based dictionary files for domain-specific translations
- **Configurable Display**: Option to hide specific translations
- **Real-time Updates**: Dictionary file is refreshed automatically every 10 seconds
- **Module-based**: Works seamlessly with IntelliJ's multi-module projects

## Installation

1. Go to `File > Settings > Plugins`
2. Click `Marketplace` tab
3. Search for "Domain Translator"
4. Click `Install` and restart IntelliJ IDEA

Alternatively, download from the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/21320).

## Configuration

1. Go to `File > Settings > Tools > Domain Translator`
2. Set the path to your dictionary CSV file (default: `dictionary.csv` in project root)
3. Configure which words to hide from translation display if needed

## Dictionary Format

Create a CSV file with semicolon-separated values:

```csv
estudiante;student
primer nombre;first name
```

### Supported Features

- **Multi-word translations**: `jahresabschluss;annual financial statement`
- **Compound words**: Automatically decomposes German compound words
- **Case preservation**: `StückZahl` → `Quantity`, `stück_zahl` → `quantity`
- **Multiple translations**: Multiple entries for the same term are supported

## Usage

Once configured, the plugin automatically:

1. Scans your code for terms found in the dictionary
2. Displays translation hints above matching code elements
3. Updates translations when the dictionary file changes
4. Only shows translations that differ from the original term

## Supported File Types

- Java (`.java`)
- Kotlin (`.kt`, `.kts`)
- JavaScript (`.js`)
- TypeScript (`.ts`)
- Vue.js (`.vue`)

## Requirements

- IntelliJ IDEA Ultimate 2024.3 or later
- JVM 11 or higher

## Development

This plugin is built with:

- **Language**: Kotlin
- **Build System**: Gradle with Kotlin DSL
- **Target Platform**: IntelliJ Platform SDK 2025.1
- **Testing**: JUnit 4

### Building from Source

```bash
git clone https://github.com/hpalma/intellij-domain-translator.git
cd intellij-domain-translator
./gradlew buildPlugin
```

## Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- [GitHub Issues](https://github.com/hpalma/intellij-domain-translator/issues)
- [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/21320)
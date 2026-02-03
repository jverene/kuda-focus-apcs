# KUDA FOCUS

**Stay focused. Track progress. Build streaks.**

A minimalist desktop application helping students maintain focus through smart process monitoring, distraction tracking, and an accountability-focused scoring system.

## Features

- **Circular Timer Interface** - Intuitive iPhone-style timer with drag-to-select duration (0-3 hours)
- **Smart Process Monitoring** - Detects when you open distracting applications during focus sessions
- **Focus Score System** - Quantifies session quality on a 0-100 scale based on distraction behavior
- **Streak Tracking** - Build momentum with consecutive days of productive focus sessions
- **Analytics Dashboard** - Visualize progress with calendar views, graphs, and personal bests
- **Local Data Storage** - All data stored locally for complete privacy

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- macOS (primary platform, Windows support planned)

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/kudafocus.git
   cd kudafocus
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

## Usage

Run the application:
```bash
mvn javafx:run
```

Or build an executable JAR:
```bash
mvn package
java -jar target/kudafocus-1.0.0.jar
```

## How It Works

1. **Set Your Timer** - Drag around the circular interface to select your focus duration
2. **Choose Apps to Block** - Select which applications should be blocked during your session
3. **Start Focusing** - Begin your session and work without distractions
4. **Stay Accountable** - If you open a blocked app, a gentle overlay reminds you to stay focused
5. **Review Your Score** - At session end, see your focus score and distraction analysis
6. **Build Streaks** - Achieve scores of 80+ to count toward your daily streak

## Focus Score Calculation

Your focus score (0-100) is calculated as:
- **Base Score**: 100 points
- **Violation Penalty**: -5 points per distraction occurrence
- **Dismissal Penalty**: -2 points per overlay dismissal
- **Time Penalty**: -1 point per minute spent on blocked apps

**Example**: Opening Discord twice (2 violations), dismissing the overlay 11 times (11 dismissals), and spending 2.75 minutes distracted results in a score of 60.

## Streak System

A session qualifies for your streak if it meets all of these criteria:
- Duration of 30 minutes or more
- Focus score of 80 or higher
- Completed successfully (not abandoned)

Your streak counter shows consecutive days with at least one qualifying session.

## Project Structure

```
src/main/java/focus/kudafocus/
├── ui/              # JavaFX user interface components
├── core/            # Session management and timer logic
├── monitoring/      # Process detection and violation tracking
├── analytics/       # Statistics, streaks, and personal bests
└── data/            # Data models and JSON persistence
```

## OOP Design Principles

This project demonstrates key object-oriented programming concepts required for APCS:

- **Encapsulation**: FocusSession class hides internal session data and score calculation
- **Abstraction**: AppMonitor abstract class defines common interface for platform-specific implementations
- **Inheritance**: BasePanel class provides shared styling to all UI panel subclasses
- **Polymorphism**: Different OS monitors (macOS, Windows) implement the same abstract interface

## Technologies

- **JavaFX 17** - Modern UI framework
- **Gson 2.10** - JSON serialization
- **JUnit 5** - Unit testing
- **Maven** - Build automation

## For APCS Students

This project demonstrates:
- File I/O with JSON
- Data structures (Lists, Maps)
- Abstract classes and inheritance
- Encapsulation principles
- Algorithm design (scoring, streak calculation)
- Event-driven programming

## Development

Run tests:
```bash
mvn test
```

Clean build:
```bash
mvn clean compile
```

## License

MIT License - See LICENSE file for details

## Author

Created as an APCS project to help students stay focused and accountable while studying.

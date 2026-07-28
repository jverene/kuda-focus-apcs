# Kuda Focus

A desktop application that helps students stay focused during study sessions. It monitors running processes, tracks distractions, and calculates a focus score.

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- macOS (primary platform)

## Install

```bash
git clone https://github.com/jverene/kuda-focus-apcs.git
cd kuda-focus-apcs
mvn clean install
```

## Run

```bash
mvn javafx:run
```

Create an executable JAR:

```bash
mvn package
java -jar target/kudafocus-1.0.0.jar
```

## How it works

1. Set a focus duration using the circular timer (0 to 3 hours).
2. Select applications to block during the session.
3. Start the session. The app monitors running processes.
4. When you open a blocked app, an overlay reminds you to refocus.
5. At the end of the session, view your focus score and distraction report.

## Focus score

The score ranges from 0 to 100. The calculation starts at 100 and subtracts penalties.

| Event | Penalty |
|-------|---------|
| Distraction occurrence | -5 points |
| Overlay dismissal | -2 points |
| Minute on blocked app | -1 point |

## Streak system

A session counts toward your streak when all conditions are true:

- Duration is 30 minutes or more
- Focus score is 80 or higher
- Session completes (not abandoned)

Data saves to `~/.kudafocus/streak_data.json`.

## Tests

```bash
mvn test
```

## Project structure

```
src/main/java/focus/kudafocus/
├── ui/              JavaFX components
├── core/            Session and timer logic
├── monitoring/      Process detection
├── analytics/       Statistics and streaks
└── data/            Models and persistence
```

## Technologies

- JavaFX 17
- Gson 2.10
- JUnit 5
- Maven

## License

MIT

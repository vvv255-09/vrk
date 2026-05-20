# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands
- Run the application: `mvn javafx:run`
- Build and package: `mvn package`

## Architecture
The project is a JavaFX application following a Model-View-Controller (MVC) pattern.

- **Models**: Found in `src/main/java`, representing core entities (e.g., `Product`, `User`, `Order`, `Expense`, `Receipt`).
- **Views**: Defined in `src/main/resources` using FXML files for layout and `styles.css` for styling.
- **Controllers**: Found in `src/main/java`, handling user interaction and bridging views with models (e.g., `MainController`, `LoginController`, `ProductsPanelController`).
- **Data Access**: `Database.java` handles MySQL connectivity and queries.
- **Entry Point**: `MainApp.java` is the main JavaFX application class.

# SmartCalendar

_A local-first Java desktop calendar with a realistic month grid, event management, color suggestions, and a small weather helper._

---

## Overview

SmartCalendar is a **local-only** JavaFX application. It renders a proper monthly calendar (28/29/30/31 days), supports creating, editing, and deleting events, and provides two optional helpers in the sidebar:

- **Color suggestions** based on a chosen base color.
- **Weather information** for a user-typed city and country.

All logic is organised using **Clean Architecture**: entities and use cases live in the `core` module, while the JavaFX UI is in the `app-fx` module.

---

## Features

### 1. Authentication (Login & Signup)

- JavaFX **login** and **signup** screens.
- User credentials stored locally via a SQL-based data access in `core`.
- Basic validation for:
    - Empty username/password.
    - Duplicate usernames on signup.
    - Incorrect credentials on login.
- Only authenticated users can open the main calendar view.

---

### 2. Calendar & Event Management

#### Monthly calendar view

- Realistic month grid (28–31 days) with weekday headers **Mon–Sun**.
- Navigation with **Previous** and **Next** month buttons.
- The currently selected date is highlighted.
- Clicking a day:
    - Selects the date.
    - Shows all events for that date in the **Day details** list on the right.

#### Event pills in day cells

- Each day cell displays:
    - The day number.
    - Up to **3 event pills** (small labels) for that day.
- If there are more than 3 events on a date:
    - A `+n` label appears.
    - Hovering shows a tooltip listing all event titles.

#### Add event

- **Double-click** a day cell to open the **New Event** dialog.
- Dialog fields:
    - Title
    - Location
    - Date
    - Start time (HH:mm)
    - End time (HH:mm)
    - Category (`Event.CategoryType`)
- On **Create**, an `Event` entity is constructed and passed to the event use case.
- UI automatically refreshes the month grid and **Day details**.

#### Edit event

- In the **Day details** list:
    - Select an event.
    - Click **Edit**.
- An **Edit Event** dialog opens, pre-filled with the existing event values:
    - Title, location, date, start/end time, category.
- On **Save**:
    - Input is validated (e.g., end time cannot be before start time).
    - An updated `Event` is created and passed to the edit-event use case.
    - Calendar and **Day details** are refreshed.

#### Delete event

- In the **Day details** list:
    - Select an event.
    - Click **Delete**.
- The event is removed via the event use case.
- The day’s events and the month grid are refreshed.

---

### 3. Color Suggestions

- A **ColorPicker** in the sidebar lets the user choose a base color.
- On color selection:
    - The UI calls the **get color scheme** use case in `core`.
    - A data access object (DAO) queries an external color API.
- The sidebar displays four groups of color suggestions:
    - **Monochromatic**
    - **Analogous**
    - **Complementary**
    - **Neutral**
- Each group is shown as small colored boxes.
- Hovering over a box shows the hex code as a tooltip.

---

### 4. Weather Assistant

- Text inputs for:
    - **City** (e.g., `Toronto`)
    - **Country or code** (e.g., `Canada` or `CA`)
- On **Get weather**:
    - The UI calls a **weather UI service**, which wraps the core weather use case.
    - The weather use case uses a data access layer to fetch data from a public weather API.
- Displayed information:
    - Location label (e.g., `Toronto, Canada`)
    - Current temperature
    - Today’s minimum–maximum temperature range
    - Short description (e.g., `Partly cloudy`)
- Errors (invalid input, network failure, etc.) are shown in a dedicated error label.
- **No automatic location detection** or OS-level permission flow is used; the user only types city and country manually.

---

## Architecture

The project follows a Clean Architecture style:

- **Entities (`core/entity`)**
    - `Event` and other domain objects.
- **Use cases (`core/use_case`)**
    - `event_changes` – create/delete/list events.
    - `edit_events` – edit-event interactor and data transfer objects.
    - `login`, `signup`, etc.
    - `get_color_scheme` – color suggestion logic.
    - `get_current_weather` – weather use case.
- **Data access (`core/data_access`)**
    - Implementations of persistence and HTTP gateways:
        - User storage (SQL-based).
        - Color API client.
        - Weather API client.
- **Interface adapters (`core/interface_adapter`)**
    - Controllers, presenters, and view models bridging use cases and UI.
- **Framework / UI (`app-fx`)**
    - JavaFX application (`App`).
    - FXML views (`MainView.fxml`, `LoginView.fxml`, `SignupView.fxml`, …).
    - Controllers (`MainController`, `LoginFxController`, `SignupFxController`).
    - Styling (`style.css`).

Flow (outside → inside):

> JavaFX Controller → Controller (interface_adapter) → Interactor (use_case)  
> → DataAccess & Entities (core) → Presenter/ViewModel → JavaFX Controller → UI

---

## Tech Stack

- **Language:** Java (JDK 21)
- **UI:** JavaFX (FXML + controllers)
- **Build:** Maven (multi-module: `core`, `app-fx`)
- **Testing:** JUnit
- **HTTP client:** `java.net.http.HttpClient`
- **Database:** SQL-based storage for users (local); in-memory storage for events
- **Architecture:** Clean Architecture / hexagonal style

---

## Project Structure

```text
SmartCalendar/
├─ core/
│  ├─ src/main/java/
│  │  ├─ entity/
│  │  ├─ use_case/
│  │  ├─ data_access/
│  │  └─ interface_adapter/
│  └─ pom.xml
├─ app-fx/
│  ├─ src/main/java/com/smartcalendar/fx/
│  │  ├─ App.java
│  │  └─ MainController.java (and other controllers)
│  ├─ src/main/resources/com/smartcalendar/fx/
│  │  ├─ MainView.fxml
│  │  ├─ LoginView.fxml
│  │  ├─ SignupView.fxml
│  │  └─ style.css
│  └─ pom.xml
├─ pom.xml          # Parent POM (aggregates modules)
└─ README.md
```

---

## Build & Run

### Prerequisites

- **JDK 21** installed.
- Maven installed or integrated in your IDE (IntelliJ IDEA recommended).
- JavaFX available via the **javafx-maven-plugin** (configured in `app-fx/pom.xml`).

### Clone

```bash
git clone https://github.com/JacobChan182/SmartCalendar.git
cd SmartCalendar
```

### Build

From the project root:

```bash
mvn clean install -DskipTests
```

This builds both `core` and `app-fx`.

### Run (Maven)

From the project root:

```bash
mvn -pl app-fx -am javafx:run
```

- `-pl app-fx` – run only the JavaFX module.
- `-am` – build dependencies (e.g. `core`) automatically.

### Run (IDE)

- Import the project as a **Maven project**.
- Set the run configuration to:
    - Module: `app-fx`
    - Main class: `com.smartcalendar.fx.App`
- Run.

---

## Usage

1. **Login / Signup**
    - Start the app.
    - Either create a new account on the signup screen or log in with an existing account.
    - On success, the main calendar window opens.

2. **Create an event**
    - Navigate to the desired month.
    - **Double-click** on a day cell.
    - Fill in the fields in the **New Event** dialog.
    - Click **Create**; the new event appears as a pill in that day’s cell and in **Day details**.

3. **Edit an event**
    - Select an event in **Day details**.
    - Click **Edit**.
    - Adjust the information in the dialog (title, time, location, category).
    - Click **Save**; the event updates in both the day list and calendar grid.

4. **Delete an event**
    - Select an event in **Day details**.
    - Click **Delete**; the event is removed.

5. **Use color suggestions**
    - Pick a color in the **Color Suggestions** panel.
    - Wait for the suggested palettes to appear under each scheme type.
    - Hover over any color box to see its hex code.

6. **Check the weather**
    - Enter **city** and **country/code** in the Weather section.
    - Click **Get weather**.
    - Read current and daily temperature plus a brief condition description.
    - If something goes wrong, an error message appears in the weather error label.

---

## Roadmap

- [x] Multi-module Maven project (`core`, `app-fx`)
- [x] Login & signup UI integrated with core
- [x] Monthly calendar grid with event pills and `+n` overflow
- [x] Event creation via double-click dialog
- [x] Event editing via dialog
- [x] Event deletion workflow
- [x] Color suggestions with ColorPicker + external API
- [x] Weather assistant for user-typed city/country
- [x] Persistent event storage (JSON/SQLite)
- [x] General UX polish and bug fixing

---

## Contribution Guidelines

- Use feature branches (e.g. `feat/edit-event-ui`, `feat/weather-panel`).
- Keep commits focused and descriptive (e.g., `feat: add edit event dialog`).
- Run `mvn test` before opening a pull request.
- For UI changes, include a short description and, if possible, a screenshot.

---

## License

**All rights reserved. No license granted.**

# SmartCalendar

_A local-first desktop calendar with a realistic month grid, powerful event management, optional weather overlay, and color-scheme suggestions for daily planning._

## Overview
SmartCalendar is a Java desktop app focused on **speed, privacy, and offline usability**. It shows a real-world month grid (28–31 cells), supports event CRUD and reminders, and (optionally) overlays local weather and lightweight color-scheme ideas to help with day planning. No cloud account is required; data stays on your device.

## Key Features
- **Real Month View**: 28–31 day grid, keyboard and mouse navigation.
- **Events & Reminders**: Create, edit, delete; all-day/multi-day; flexible advance reminders (e.g., 10 min before, on time).
- **Jump to Date**: Quick navigation to any date.
- **Local-First Storage**: JSON or SQLite (configurable), easy import/export.
- **(Planned) Weather Overlay**: Daily/hourly weather overlay driven by latitude/longitude or city.
- **(Planned) Color Suggestions**: Simple color palettes to inspire outfits/notes/themes.
- **(Planned) Auto Location**: Ask permission, then try GPS/system/network-based location with safe fallbacks.
- **Privacy by Default**: Location (if enabled) is used locally and never uploaded.

## Status & Roadmap
- [x] Core/CA engine foundation
- [ ] Month grid UI (JavaFX)
- [ ] Event CRUD end-to-end
- [ ] Reminder engine (local notifications)
- [ ] Weather overlay (Open-Meteo or similar)
- [ ] Color scheme panel (The Color API or similar)
- [ ] Auto location & permission flow

**Milestones**
1. **M1** Core models + storage + month grid
2. **M2** Full event CRUD + reminder engine
3. **M3** Weather overlay + units (°C/°F)
4. **M4** Color suggestions panel
5. **M5** Auto location + privacy dialog

## Tech Stack
- **Language**: Java (17+)
- **UI**: JavaFX (FXML + Controllers)
- **Build**: Gradle (recommended) or Maven
- **Tests**: JUnit (plus Mockito if needed)
- **HTTP**: Java 11+ `HttpClient` (or Retrofit/OkHttp if preferred)
- **Persistence**: JSON or SQLite (choose one per build)

## Architecture
```
smartcalendar/
├─ app/            # App bootstrap & dependency wiring
├─ core/           # Domain: entities, use cases, services, ports
├─ infra/          # Adapters: storage, HTTP clients, system location
├─ ui/             # JavaFX views (FXML), controllers, view-models
└─ shared/         # Common utils: time, result types, errors
```
**Clean boundaries**: `core` is UI-agnostic; `ui` talks to `core` via interfaces; `infra` implements ports like `EventRepository`, `WeatherClient`, `ColorSchemeClient`, `LocationProvider`.

## Getting Started (Development)

### 1) Clone
```bash
git clone https://github.com/JacobChan182/SmartCalendar.git
cd SmartCalendar
```

### 2) JDK
- Install **JDK 17+**.
- In IntelliJ IDEA, set the Project SDK to your JDK 17+.

### 3) Gradle Setup (JavaFX)
_Add this minimal build file if you don’t have one yet (adjust `mainClass` to your package/class name)._

```gradle
plugins {
    id 'application'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation "org.openjfx:javafx-controls:21"
    implementation "org.openjfx:javafx-fxml:21"
    testImplementation "org.junit.jupiter:junit-jupiter:5.10.0"
}

application {
    // Replace with your actual main class:
    mainClass = "com.smartcalendar.app.Main"
}

tasks.test {
    useJUnitPlatform()
}
```

### 4) Run
```bash
./gradlew clean run
```
First launch will create a local data folder (e.g., `~/.smartcalendar/`).

## Configuration
- **Units**: `C` or `F`.
- **Location**:
    - `auto = true` will try system/GPS/network location (with permission).
    - Fallbacks: last known position or manual city/lat-lon.
- **Storage**:
    - JSON: simple files (portable, easy to diff).
    - SQLite: single-file DB (robust queries, concurrency-safe).
- **Reminders**: per-event advance offsets (e.g., 10m, 1h, 1d).
- **Privacy**: location never leaves the device; auto-location can be disabled anytime.

## Development Guide
- **Branching**: `main` (stable); feature branches `feat/...`; fixes `fix/...`.
- **Commits**: `feat:`, `fix:`, `test:`, `docs:`, `refactor:`.
- **Issues**: add steps to reproduce, expected/actual, logs/screenshots.
- **Style**: Google Java Style or team convention (optionally enforce via Spotless/IDE).
- **UI Notes**: Month view uses a real calendar grid (28–31 cells). Use FXML + Scene Builder for fast iteration.

## Testing
- **Unit**: core use cases (event CRUD, reminder timing, parsers).
- **Contract**: API adapters (weather/color) with mock servers.
- **UI (minimal)**: critical flows (create/edit, drag-resize, date jump).
```bash
./gradlew test
```

## Design Notes
- **Why JavaFX (vs Swing)**: modern FXML layout, rich controls (TableView, DatePicker, Dialog), CSS-like styling, Scene Builder drag-and-drop, strong HiDPI support.
- **Local-First**: fast start, offline support, user-controlled backups.
- **Extensibility**: ports/adapters make it easy to swap storage or APIs.

## Contributing
1. Open an issue and describe the change.
2. Create a `feat/...` branch from `main`.
3. Add tests for new behavior.
4. Submit a PR; link related issue(s); include screenshots for UI changes.

## FAQ
**Q: Do I need a Google account or Google Calendar API?**  
A: No. SmartCalendar is **local-only**. External APIs are optional (weather/color only).

**Q: Will GPS constantly run?**  
A: No. If enabled, location is requested on demand with clear user consent and can be disabled anytime.

**Q: Where is my data stored?**  
A: In your home directory (e.g., `~/.smartcalendar/`) or a custom path you configure.

## License
MIT (or update to your course’s required license).

## Credits
- JavaFX community and tooling.
- (Planned) Weather: Open-Meteo (or similar).
- (Planned) Colors: The Color API (or similar).

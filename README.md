# SmartCalendar

_A local-first desktop calendar with a realistic month grid, fast event workflows, optional weather overlay, and color-scheme suggestions._

---

## Overview
SmartCalendar is a **privacy-first** Java desktop app. It renders a **real month grid (28/29/30/31 cells)**, supports event CRUD and reminders, and (optionally) overlays local weather and lightweight color palettes for day planning. No cloud account is required; data stays on your device.

---

## Maven Coordinates (Project Root)
The project is a multi-module Maven build. Parent POM coordinates:

```xml
<groupId>TUT0101-1</groupId>
<artifactId>smartcalendar-parent</artifactId>
<version>1.0</version>
<packaging>pom</packaging>
```

Example dependency from `app-fx` to `core`:

```xml
<dependency>
  <groupId>TUT0101-1</groupId>
  <artifactId>core</artifactId>
  <version>${project.version}</version>
</dependency>
```

---

## Status & Roadmap
- [x] Clean Architecture core (domain/use cases)
- [x] JavaFX app module boots successfully
- [ ] Month grid UI (realistic 28–31 cells)
- [ ] Event CRUD end-to-end
- [ ] Reminder engine (local notifications)
- [ ] Weather overlay (Open-Meteo)
- [ ] Color suggestions panel (The Color API)
- [ ] Auto location + permission flow

**Milestones**
1. **M1** Core models + storage + month grid
2. **M2** Full CRUD + reminder engine
3. **M3** Weather overlay + units (°C/°F)
4. **M4** Color suggestions panel
5. **M5** Auto location + privacy dialog

---

## Tech Stack
- **JDK:** **21** (project standard)
- **UI:** JavaFX (FXML + Controllers)
- **Build:** **Maven** (multi-module: `core`, `app-fx`)
- **Tests:** JUnit (Mockito optional)
- **HTTP:** `java.net.http.HttpClient`
- **Persistence:** JSON or SQLite (TBD, pick per build)

---

## Project Structure
```
SmartCalendar/
├─ core/                 # Domain & use cases (UI-agnostic)
│  └─ src/...
├─ app-fx/               # JavaFX UI (depends on core)
│  └─ src/main/java/com/smartcalendar/fx/App.java
├─ docs/plantuml/        # Diagrams (optional)
├─ pom.xml               # Parent POM (aggregates modules)
└─ README.md
```

**Boundaries:** `core` exposes interfaces/DTOs; `app-fx` talks to `core` only via those interfaces. Future adapters (storage, HTTP, location) implement ports.

---

## Getting Started (Development)

### 0) Prerequisites
- Install **JDK 21**.
- In IntelliJ IDEA: set **Project SDK = JDK 21**.
- Import the project **as Maven** from the **root `pom.xml`** (then click *Reimport* once).

### 1) Clone
```bash
git clone https://github.com/JacobChan182/SmartCalendar.git
cd SmartCalendar
```

### 2) Build
```bash
# Build all modules and install core to local Maven repo
mvn -q -DskipTests install
```

### 3) Run the JavaFX app
```bash
# Run only the UI module (will build core automatically)
mvn -q -pl app-fx -am javafx:run
```
**IDE equivalent:** Maven tool window → run root **install**, then in **app-fx → Plugins → javafx → javafx:run**.

> The current entry point is `com.smartcalendar.fx.App`, which shows a minimal window. Replace the center content with the month grid and hook up use cases from `core`.

---

## Configuration (planned)
- **Units:** `C` / `F`
- **Location:** `auto=true` tries system/GPS/network; fallbacks to last known or manual lat/lon
- **Storage:** `json` (simple/debuggable) or `sqlite` (robust single-file DB)
- **Privacy:** location is local-only and can be disabled anytime

---

## Development Guide
- **Branching:** `main` (stable), `feat/*` (features), `fix/*` (bug fixes)
- **Commit style:** `feat: …`, `fix: …`, `docs: …`, `test: …`, `refactor: …`
- **PRs:** reference related issues; include screenshots/GIFs for UI changes
- **UI conventions:** only the current month’s valid cells (no 6×7 gray overflow); header `<  Year Month  >`; right pane for **day events** and **day weather**
- **Accessibility:** color tiles contrast ≥ 3:1; keyboard navigation supported

---

## Testing
```bash
mvn -q test
```
- **core:** unit tests for entities/use cases
- **infra (when added):** contract tests for gateways (e.g., via WireMock)
- **ui (minimal):** smoke tests of critical flows (create/edit, jump to date)

---

## Why JavaFX (over Swing)
- Faster layout with **FXML + Scene Builder**
- Rich controls (TableView, DatePicker, Dialog)
- CSS-like styling, HiDPI/retina friendly
- Can embed legacy Swing via `SwingNode` during transition

---

## Troubleshooting

**`No plugin found for prefix 'javafx'`**  
`app-fx/pom.xml` is missing the plugin or the Maven view wasn’t reimported. Ensure:
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.openjfx</groupId>
      <artifactId>javafx-maven-plugin</artifactId>
      <version>0.0.8</version>
      <configuration>
        <mainClass>com.smartcalendar.fx.App</mainClass>
      </configuration>
    </plugin>
  </plugins>
</build>
```

**`module javafx.controls not found`**  
Launch via **`mvn javafx:run`** (the plugin sets the module path). Don’t just run `main()` without VM options.

**`Error: Output directory is empty`**  
`app-fx/src/main/java` has no sources. Add `com.smartcalendar.fx.App` (or update `mainClass` accordingly).

**`error: release version 21 not supported`**  
Maven is using a non-21 JDK. Fix by using JDK 21 for Maven:
- Command line (temporary):
  ```bash
  setx JAVA_HOME "C:\Program Files\Java\jdk-21"
  ```
  or set `JAVA_HOME` and PATH for the session.
- IntelliJ: **Settings → Build Tools → Maven → Runner → JRE = JDK 21**.

**`app-fx` cannot resolve `core`**  
Run a full build first: `mvn -q -DskipTests install`, or run with `-pl app-fx -am` so Maven builds dependencies automatically.

---

## Contribution Workflow
1. Open an issue; outline motivation and approach.
2. Branch from `main` → `feat/...` or `fix/...`.
3. Add/adjust tests.
4. Submit PR; tag module(s) affected (`core`, `app-fx`, `infra`).

---

## FAQ
**Do I need Google Calendar or any account?**  
No. The app is **local-only**. External APIs are optional (weather/colors).

**Will GPS run continuously?**  
No. If enabled, location is requested on demand with explicit consent and can be turned off anytime.

**Where is my data stored?**  
In your home directory (e.g., `~/.smartcalendar/`) or a custom path you configure.

---

## License
**All rights reserved. No license granted.**

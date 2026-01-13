# 🎮 Tic Tac Toe

<div align="center">

<!-- CI/CD Badges -->
[![Android CI](https://github.com/2026-DEV2-007-Hatem-NOUREDDINE/tictactoe/actions/workflows/android_check.yml/badge.svg)](https://github.com/2026-DEV2-007-Hatem-NOUREDDINE/tictactoe/actions/workflows/android_check.yml)
[![Release](https://github.com/2026-DEV2-007-Hatem-NOUREDDINE/tictactoe/actions/workflows/release.yml/badge.svg)](https://github.com/2026-DEV2-007-Hatem-NOUREDDINE/tictactoe/actions/workflows/release.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=alert_status)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=coverage)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)

**A modern Android Tic Tac Toe game built with Clean Architecture and Test-Driven Development**

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-API%2026+-3DDC84?logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2025.12-4285F4?logo=jetpackcompose&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-blueviolet)
![TDD](https://img.shields.io/badge/TDD-100%25-brightgreen)

![Alt text](images/Kata_TicTacToe.png?raw=true "Tic Tac Toe")

> ## About this Kata
>
> This short and simple Kata should be performed using **Test Driven Development** (TDD).
>
> ## Rules
>
> The rules are described below :
>
> - X always goes first.
> - Players cannot play on a played position.
> - Players alternate placing X's and O's on the board until either:
> - One player has three in a row, horizontally, vertically or diagonally
> - All nine squares are filled.
> - If a player is able to draw three X's or three O's in a row, that player wins.
> - If all nine squares are filled and neither player has three in a row, the game is a draw.
>
> ## Useful link
>
> Clean Code - TDD : https://cleancoders.com/episode/clean-code-episode-6-p1
>
> **IMPORTANT:**  Implement the requirements focusing on **writing the best code** you can produce.

---

## 📋 Table of Contents

| Section                                                             | Description                           |
|---------------------------------------------------------------------|---------------------------------------|
| [🏠 Architecture](#1-application-architecture)                      | Clean Architecture & module structure |
| [🛠️ Technical Choices](#2-technical-choices)                       | Technology stack & design decisions   |
| [✅ Code Quality](#3-code-quality-and-cicd)                          | CI/CD, testing, and quality tools     |
| [🤝 Contributing](#4-contribution-guidelines--conventions)          | Git workflow & conventions            |
| [🧪 TDD Deep Dive](#5-a-deep-dive-into-test-driven-development-tdd) | Test-Driven Development approach      |
| [▶️ Running](#6-how-to-run-the-project)                             | How to build and run                  |
| [📸 Screenshots](#7-screenshot-testing-visual-regression)           | Visual regression testing             |
| [📂 Files](#8-file-organization)                                    | Project structure                     |

---

## 1. Application Architecture

This project follows the **Clean Architecture** pattern, which promotes a separation of concerns and a high degree of independence from external
frameworks.

### Module Dependency Graph

```mermaid
graph TB
    subgraph "📱 Presentation Layer"
        APP[":app<br/>Android UI"]
    end

    subgraph "🧠 Domain Layer"
        DOMAIN[":domain<br/>Business Logic"]
    end

    subgraph "💾 Data Layer"
        DATA[":data<br/>Repository"]
    end

    APP -->|" uses "| DOMAIN
    DATA -->|" implements "| DOMAIN
    style APP fill: #4CAF50, color: #fff
    style DOMAIN fill: #2196F3, color: #fff
    style DATA fill: #FF9800, color: #fff
```

### Data Flow

```mermaid
sequenceDiagram
    participant UI as 📱 GameScreen
    participant VM as 🎛️ ViewModel
    participant UC as ⚙️ UseCase
    participant REPO as 💾 Repository
    participant GAME as 🎮 Game
    UI ->> VM: User taps cell (row, col)
    VM ->> UC: PlayTurnUseCase.invoke(row, col)
    UC ->> REPO: getGame()
    REPO -->> UC: Game instance
    UC ->> GAME: play(row, col)
    GAME -->> UC: Updated state
    UC ->> REPO: saveSnapshot()
    UC -->> VM: Result<GameState>
    VM -->> UI: Update BoardUiState
```

### Component Details

```mermaid
classDiagram
    class Game {
        +currentPlayer: Player
        +winner: Player?
        +isDraw: Boolean
        +play(row, col)
        +getCell(row, col)
        +getSnapshot()
        +restore(state)
    }

    class GameViewModel {
        +uiState: StateFlow
        +play(row, col)
        +reset(size)
    }

    class GameRepository {
        <<interface>>
        +getGame(): Game
        +saveSnapshot(state)
        +loadSnapshot(): GameState?
    }

    class PlayTurnUseCase {
        +invoke(row, col): Result
    }

    GameViewModel --> PlayTurnUseCase
    PlayTurnUseCase --> GameRepository
    GameRepository <|.. GameRepositoryImpl
    GameRepositoryImpl --> Game
```

### Layer Descriptions

| Layer            | Module    | Responsibility                              |
|------------------|-----------|---------------------------------------------|
| **Presentation** | `:app`    | UI, ViewModel, Compose components           |
| **Domain**       | `:domain` | Game logic, UseCases, Repository interface  |
| **Data**         | `:data`   | Repository implementation, data persistence |

### Key Principles

```mermaid
mindmap
  root((Clean Architecture))
    Dependency Rule
      App depends on Domain
      Data depends on Domain
      Domain has NO dependencies
    SOLID Principles
      Single Responsibility
      Open/Closed
      Liskov Substitution
      Interface Segregation
      Dependency Inversion
    Benefits
      Testability
      Maintainability
      Scalability
```

---

## 2. Technical Choices

### Technology Stack

```mermaid
graph LR
    subgraph "🎨 UI"
        COMPOSE[Jetpack Compose]
        M3[Material 3]
    end

    subgraph "🏗️ Architecture"
        CLEAN[Clean Architecture]
        MVVM[MVVM Pattern]
    end

    subgraph "🔧 DI"
        HILT[Hilt/Dagger]
    end

    subgraph "🧪 Testing"
        JUNIT[JUnit 5]
        MOCKK[MockK]
        ROBORAZZI[Roborazzi]
    end

    subgraph "📊 Quality"
        DETEKT[Detekt]
        KTLINT[KtLint]
        JACOCO[JaCoCo]
    end
```

| Technology             | Purpose                                            |
|------------------------|----------------------------------------------------|
| **Kotlin**             | Official language for modern Android development   |
| **Jetpack Compose**    | Modern declarative UI toolkit for native Android   |
| **Hilt**               | Dependency injection for improved testability      |
| **Clean Architecture** | Separation of concerns and maintainability         |
| **JUnit 5**            | Modern testing framework with nested tests support |
| **MockK**              | Kotlin-native mocking library                      |
| **Roborazzi**          | Screenshot testing for visual regression           |

### UI Features

- **Edge-to-Edge Display**: Modern, immersive UI drawing behind system bars
- **Material 3**: Following the latest Material Design guidelines
- **Animations**: Smooth shake animation on game over
- **Haptic Feedback**: Tactile feedback for user actions

---

## 3. Code Quality and CI/CD

### Quality Tools Overview

```mermaid
graph TB
    subgraph "📝 Code"
        CODE[Source Code]
    end

    subgraph "🔍 Static Analysis"
        DETEKT[Detekt<br/>Code Smells]
        KTLINT[KtLint<br/>Code Style]
        LINT[Android Lint<br/>Best Practices]
    end

    subgraph "🧪 Testing"
        UNIT[Unit Tests]
        INSTR[Instrumentation Tests]
        SCREENSHOT[Screenshot Tests]
    end

    subgraph "📊 Coverage"
        JACOCO[JaCoCo]
    end

    subgraph "☁️ Cloud"
        SONAR[SonarCloud]
    end

    CODE --> DETEKT
    CODE --> KTLINT
    CODE --> LINT
    CODE --> UNIT
    CODE --> INSTR
    CODE --> SCREENSHOT
    UNIT --> JACOCO
    INSTR --> JACOCO
    DETEKT --> SONAR
    KTLINT --> SONAR
    LINT --> SONAR
    JACOCO --> SONAR
```

### CI/CD Pipeline Architecture

Our CI pipeline uses **6 parallel jobs** for maximum efficiency:

```mermaid
flowchart LR
subgraph "Stage 1"
BUILD[🏗️ Build]
end

subgraph "Stage 2"
LINT[🔍 Lint]
TEST[🧪 Test]
SCREEN[📸 Screenshot]
end

subgraph "Stage 3"
INSTR[📱 Instrumentation]
end

subgraph "Stage 4"
SONAR[🔎 SonarCloud]
end

BUILD --> LINT
BUILD --> TEST
BUILD --> SCREEN

LINT --> INSTR
TEST --> INSTR

INSTR --> SONAR
TEST --> SONAR

style BUILD fill: #4CAF50, color:#fff
style LINT fill: #2196F3, color:#fff
style TEST fill: #9C27B0, color:#fff
style SCREEN fill: #FF9800, color:#fff
style INSTR fill: #E91E63, color:#fff
style SONAR fill: #00BCD4, color:#fff
```

| Job                 | Description                          | Depends On            |
|---------------------|--------------------------------------|-----------------------|
| **Build**           | Compiles debug APK, caches artifacts | -                     |
| **Lint**            | Runs Detekt, KtLint, Android Lint    | Build                 |
| **Test**            | Unit tests with JaCoCo coverage      | Build                 |
| **Screenshot**      | Roborazzi visual regression tests    | Build                 |
| **Instrumentation** | Emulator-based UI tests              | Lint, Test            |
| **SonarCloud**      | Code quality analysis                | Test, Instrumentation |

### JaCoCo Coverage Tasks

```mermaid
graph TD
    subgraph "Unit Tests Only 🚀"
        JACOCO_TEST[jacocoTestReport]
    end

    subgraph "Instrumentation Only 📱"
        JACOCO_INSTR[jacocoInstrumentationReport]
    end

    subgraph "Full Coverage 📊"
        JACOCO_FULL[jacocoFullReport]
    end

    JACOCO_TEST --> JACOCO_FULL
    JACOCO_INSTR --> JACOCO_FULL
```

```bash
# Unit tests coverage only (fast, debug variant)
./gradlew jacocoTestReport

# Instrumentation tests coverage only (requires emulator)
./gradlew jacocoInstrumentationReport

# Full coverage report (unit + instrumentation)
./gradlew jacocoFullReport
```

### Test Coverage by Module

| Module    | Test Types                     | Coverage Target |
|-----------|--------------------------------|-----------------|
| `:app`    | Unit, Screenshot, Instrumented | 80%             |
| `:domain` | Unit (TDD)                     | 100%            |
| `:data`   | Unit                           | 80%             |

### Key CI Features

- **⚡ Gradle Build Cache**: Intelligent caching for faster builds
- **🔄 Concurrency Control**: Cancels redundant workflow runs
- **📊 Rich Summaries**: Detailed GitHub Step Summaries with:
    - Test counts (passed/failed/skipped)
    - Coverage with visual progress bars
    - Links to HTML reports
    - Screenshot comparisons
- **🎚️ KVM Acceleration**: Faster emulator tests
- **📦 Artifact Uploads**: All reports downloadable

### 🚀 Auto-Release Workflow

When code is **merged to `main`**, an automatic release is created:

```mermaid
flowchart LR
    MERGE[📥 Merge to main] --> BUILD[🏗️ Build Release APK]
BUILD --> VERSION[📅 Generate Version]
VERSION --> TAG[🏷️ Create Git Tag]
TAG --> RELEASE[🚀 GitHub Release]

style MERGE fill: #9C27B0,color: #fff
style BUILD fill: #4CAF50,color: #fff
style VERSION fill: #2196F3,color: #fff
style TAG fill: #FF9800,color: #fff
style RELEASE fill: #E91E63,color: #fff
```

| Property           | Value                        |
|--------------------|------------------------------|
| **Trigger**        | Push to `main` (after merge) |
| **Version Format** | `YYYY.MM.DD-commit`          |
| **Tag Format**     | `vYYYY.MM.DD-commit`         |
| **APK Name**       | `tictactoe-{version}.apk`    |
| **Changelog**      | Auto-generated from commits  |

> 💡 The workflow ignores changes to `.md` files and snapshots to avoid unnecessary releases.

### Running Locally

```bash
# Full static analysis check
./gradlew detekt ktlintCheck lintDebug

# Unit tests with coverage
./gradlew testDebugUnitTest jacocoTestReport

# Screenshot tests
./gradlew verifyRoborazziDebug

# Record new screenshots
./gradlew recordRoborazziDebug

# Full SonarCloud analysis
./gradlew detekt ktlintCheck lintDebug testDebugUnitTest jacocoTestReport detektReportMergeXml sonar
```

### Static Analysis Reports

After running the static analysis tasks, HTML reports are available per module:

| Report          | Path                                      |
|-----------------|-------------------------------------------|
| 🔎 Detekt       | `*/build/reports/detekt/detekt.html`      |
| 📝 KtLint       | `*/build/reports/ktlint/*/*.html`         |
| 📱 Android Lint | `*/build/reports/lint-results-debug.html` |

### Required GitHub Configuration

| Type     | Name                     | Description                         |
|----------|--------------------------|-------------------------------------|
| Secret   | `SONAR_TOKEN`            | Authentication token for SonarCloud |
| Variable | `SONAR_PROJECT_KEY`      | Your SonarCloud project identifier  |
| Variable | `SONAR_ORGANIZATION_KEY` | Your SonarCloud organization        |

---

## 4. Contribution Guidelines & Conventions

### Git Workflow

```mermaid
gitGraph
    commit id: "main"
    branch feature/new-ui
    checkout feature/new-ui
    commit id: "feat: add board"
    commit id: "test: add tests"
    checkout main
    merge feature/new-ui id: "PR merge"
    commit id: "release"
```

### Git Hooks

Install them with:

```bash
./gradlew installGitHooks
```

### Commit Convention

We follow **Conventional Commits**:

```mermaid
graph LR
    subgraph "Commit Structure"
        TYPE[type] --> SCOPE["(scope)"]
        SCOPE --> SUBJECT[": subject"]
    end

subgraph "Types"
FEAT[feat ✨]
FIX[fix 🐛]
DOCS[docs 📚]
TEST[test 🧪]
REFACTOR[refactor ♻️]
end
```

| Type       | Description      | Example                            |
|------------|------------------|------------------------------------|
| `feat`     | New feature      | `feat(ui): add game board`         |
| `fix`      | Bug fix          | `fix(game): correct win detection` |
| `docs`     | Documentation    | `docs: update README`              |
| `test`     | Tests            | `test(game): add TDD tests`        |
| `refactor` | Code refactoring | `refactor: extract helper`         |
| `style`    | Formatting       | `style: apply ktlint`              |
| `chore`    | Maintenance      | `chore: update deps`               |
| `ci`       | CI changes       | `ci: add sonar job`                |

### Branch Naming

```mermaid
graph LR
    FEATURE[feature/] --> DESC1[description]
BUGFIX[bugfix/] --> DESC2[description]
HOTFIX[hotfix/] --> DESC3[description]
RELEASE[release/] --> VERSION[version]
```

---

## 5. A Deep Dive into Test-Driven Development (TDD)

### The TDD Cycle

```mermaid
graph LR
    RED["🔴 RED<br/>Write failing test"] --> GREEN["🟢 GREEN<br/>Write minimal code"]
    GREEN --> REFACTOR["🔵 REFACTOR<br/>Clean up code"]
    REFACTOR --> RED
    style RED fill: #f44336, color: #fff
    style GREEN fill: #4CAF50, color: #fff
    style REFACTOR fill: #2196F3, color: #fff
```

### TDD Timeline for Game Class

```mermaid
timeline
    title Building Game.kt with TDD

    section Initial State
        Step 1: Write test for currentPlayer = X
            : Create Game class with initial state

    section Making Moves
        Step 2: Write test for player switching
            : Add play() method
        Step 3: Write test for recording moves
            : Add board and getCell()

    section Validation
        Step 4: Write test for PositionTaken
            : Add guard clause for occupied cells
        Step 5: Write test for InvalidPosition
            : Add bounds checking

    section Win Detection
        Step 6: Write test for horizontal win
            : Add checkWin() for rows
        Step 7: Write test for vertical win
            : Extend checkWin() for columns
        Step 8: Write test for diagonal win
            : Extend checkWin() for diagonals

    section Game Over
        Step 9: Write test for draw
            : Add moveCount and isDraw
        Step 10: Write test for GameOver exception
            : Add guard clause for ended games
```

### Test Structure Mapping to Rules

```mermaid
graph TD
    subgraph "GameTest.kt Structure"
        ROOT[Tic-Tac-Toe Game Rules]
        ROOT --> R1[Rule 1: X always goes first]
        ROOT --> R2[Rule 2: Cannot play on played position]
        ROOT --> R3[Rule 3: Players alternate]
        ROOT --> R4[Rule 4: Three in a row ends game]
        ROOT --> R5[Rule 5: Three in a row = win]
        ROOT --> R6[Rule 6: Full board = draw]
        R4 --> R4H[Horizontal Wins]
        R4 --> R4V[Vertical Wins]
        R4 --> R4D[Diagonal Wins]
        R4 --> R4O[Game Over]
    end

    style ROOT fill: #9C27B0, color: #fff
    style R1 fill: #4CAF50, color: #fff
    style R2 fill: #f44336, color: #fff
    style R3 fill: #2196F3, color: #fff
    style R4 fill: #FF9800, color: #fff
    style R5 fill: #00BCD4, color: #fff
    style R6 fill: #E91E63, color: #fff
```

### TDD Step-by-Step Example

#### Step 1: Initial State (Rule 1)

**🔴 RED**: Write a failing test

```kotlin
@Test
fun `new game should start with Player X`() {
    val game = Game() // ❌ Fails: Game doesn't exist
    assertEquals(Player.X, game.currentPlayer)
}
```

**🟢 GREEN**: Write minimal code

```kotlin
class Game {
    val currentPlayer: Player = Player.X
}
```

#### Step 2: Making Moves (Rule 3)

**🔴 RED**: Test player switching

```kotlin
@Test
fun `after X plays, it should be O's turn`() {
    val game = Game()
    game.play(0, 0) // ❌ Fails: play() doesn't exist
    assertEquals(Player.O, game.currentPlayer)
}
```

**🟢 GREEN**: Add play method

```kotlin
fun play(row: Int, col: Int) {
    currentPlayer = Player.O
}
```

**🔵 REFACTOR**: Make it generic

```kotlin
fun play(row: Int, col: Int) {
    currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
}
```

#### Step 3: Invalid Moves (Rule 2)

**🔴 RED**: Test occupied cell

```kotlin
@Test
fun `playing on occupied cell should throw PositionTaken`() {
    val game = Game()
    game.play(0, 0)
    assertThrows<GameException.PositionTaken> {
        game.play(0, 0) // ❌ Fails: no exception thrown
    }
}
```

**🟢 GREEN**: Add guard clause

```kotlin
fun play(row: Int, col: Int) {
    if (board[row][col] != null) {
        throw GameException.PositionTaken()
    }
    // ... rest of method
}
```

---

## 6. How to Run the Project

### Prerequisites

```mermaid
graph LR
    JDK[JDK 21+] --> STUDIO[Android Studio]
    STUDIO --> PROJECT[Open Project]
    PROJECT --> RUN[Run App]
```

* JDK 21 or higher
* Android Studio Ladybug or higher

### Commands

```bash
# Run Unit Tests (TDD Check)
./gradlew :domain:test

# Build the Application
./gradlew :app:assembleDebug

# Install on device
./gradlew :app:installDebug

# Run all tests with coverage
./gradlew jacocoTestReport
```

---

## 7. Screenshot Testing (Visual Regression)

### Roborazzi Workflow

```mermaid
flowchart TD
    subgraph "Development"
        CODE[Make UI Changes]
        RECORD[./gradlew recordRoborazziDebug]
        COMMIT[Commit Snapshots]
    end

subgraph "CI Pipeline"
CI_VERIFY[./gradlew verifyRoborazziDebug]
PASS{Match?}
SUCCESS[✅ Pass]
FAIL[❌ Fail + Upload Diff]
end

CODE --> RECORD
RECORD --> COMMIT
COMMIT --> CI_VERIFY
CI_VERIFY --> PASS
PASS -->|Yes|SUCCESS
PASS -->|No|FAIL
```

### Commands

```bash
# Record new/updated screenshots
./gradlew recordRoborazziDebug

# Verify screenshots match
./gradlew verifyRoborazziDebug
```

### Handling Failures

1. Download the `screenshot-report` artifact
2. Open `index.html` to compare expected vs actual
3. If changes are intentional:
   ```bash
   ./gradlew recordRoborazziDebug
   ```
4. Commit the updated snapshots

---

## 8. File Organization

```mermaid
graph TD
    ROOT[tictactoe/]

ROOT --> APP[app/]
ROOT --> DATA[data/]
ROOT --> DOMAIN[domain/]
ROOT --> BUILD_LOGIC[build-logic/]
ROOT --> SNAPSHOTS[snapshots/]
ROOT --> GITHUB[.github/]

APP --> APP_MAIN[main/kotlin/.../app/]
APP --> APP_TEST[test/]
APP --> APP_ANDROID[androidTest/]

APP_MAIN --> UI[ui/]
UI --> VIEWMODEL[viewmodel/]
UI --> COMPONENTS[components/]
UI --> THEME[theme/]

DOMAIN --> DOMAIN_MAIN[main/kotlin/.../domain/]
DOMAIN --> DOMAIN_TEST[test/]

DOMAIN_MAIN --> MODEL[model/]
DOMAIN_MAIN --> USECASE[usecase/]
DOMAIN_MAIN --> REPO_INT[repository/]

MODEL --> GAME[Game.kt]
MODEL --> PLAYER[Player.kt]
MODEL --> EXCEPTION[GameException.kt]

style ROOT fill: #9C27B0, color: #fff
style APP fill: #4CAF50, color: #fff
style DATA fill: #FF9800, color: #fff
style DOMAIN fill: #2196F3, color: #fff
style GAME fill: #E91E63, color: #fff
```

### Directory Structure

```
.
├── app/                  # 📱 Android Module (UI)
│   └── src/
│       ├── main/kotlin/.../app/
│       │   ├── ui/
│       │   │   ├── GameScreen.kt
│       │   │   ├── viewmodel/GameViewModel.kt
│       │   │   ├── components/{Board, Cell, GameControls, GameStatus}.kt
│       │   │   └── theme/{Color, Theme, Type}.kt
│       │   └── di/
│       ├── test/             # Unit + Screenshot tests
│       └── androidTest/      # Instrumentation tests
│
├── data/                 # 💾 Data Module
│   └── src/main/kotlin/.../data/
│       ├── di/DataModule.kt
│       └── repository/GameRepositoryImpl.kt
│
├── domain/               # 🧠 Domain Module (Pure Kotlin)
│   └── src/
│       ├── main/kotlin/.../domain/
│       │   ├── model/{Game, Player, GameState, GameException}.kt
│       │   ├── repository/GameRepository.kt
│       │   └── usecase/{PlayTurn, Reset, Load, GetSnapshot}UseCase.kt
│       └── test/             # TDD unit tests
│
├── build-logic/          # 🔧 Convention Plugins
│   └── convention/
│       └── src/main/kotlin/
│           ├── DetektConventionPlugin.kt
│           ├── KtLintConventionPlugin.kt
│           ├── JacocoConventionPlugin.kt
│           ├── JacocoReportConventionPlugin.kt
│           └── SonarConventionPlugin.kt
│
├── snapshots/            # 📸 Roborazzi golden images
│   └── roborazzi/
│
└── .github/workflows/    # 🚀 CI/CD
    └── android_check.yml
```

---

## 📄 License

This project is created for the **BNP Paribas Kata** exercise.

---

<div align="center">

### 🏆 Built with Excellence

**Clean Architecture** • **Test-Driven Development** • **100% Coverage**

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/new_code?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)

Made with ❤️ by [Hatem NOUREDDINE](https://github.com/2026-DEV2-007-Hatem-NOUREDDINE)

</div>

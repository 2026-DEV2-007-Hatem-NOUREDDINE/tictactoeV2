# 🎮 Tic Tac Toe

<!-- CI/CD Badges -->
[![Android CI](https://github.com/2026-DEV2-007-Hatem-NOUREDDINE/tictactoe/actions/workflows/android_check.yml/badge.svg)](https://github.com/2026-DEV2-007-Hatem-NOUREDDINE/tictactoe/actions/workflows/android_check.yml)

<!-- SonarCloud Badges -->
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=alert_status)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=coverage)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=bugs)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=code_smells)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=sqale_index)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe&metric=security_rating)](https://sonarcloud.io/dashboard?id=2026-DEV2-007-Hatem-NOUREDDINE_tictactoe)

<!-- Technology Badges -->
![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7.6-4285F4?logo=jetpackcompose&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.2-02303A?logo=gradle&logoColor=white)

<!-- Code Quality Badges -->
![Code Style](https://img.shields.io/badge/Code%20Style-ktlint-F37726?logo=kotlin&logoColor=white)
![Static Analysis](https://img.shields.io/badge/Static%20Analysis-Detekt-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

<!-- Architecture Badges -->
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

1. [Application Architecture](#1-application-architecture)
2. [Technical Choices](#2-technical-choices)
3. [Code Quality and CI/CD](#3-code-quality-and-cicd)
4. [Contribution Guidelines](#4-contribution-guidelines--conventions)
5. [How to Run the Project](#6-how-to-run-the-project)
6. [Screenshot Testing](#7-screenshot-testing-visual-regression)
7. [File Organization](#8-file-organization)

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

    APP -->|"uses"| DOMAIN
    DATA -->|"implements"| DOMAIN

    style APP fill:#4CAF50,color:#fff
    style DOMAIN fill:#2196F3,color:#fff
    style DATA fill:#FF9800,color:#fff
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
        INSTR[📱 Instrumentation]
    end

    subgraph "Stage 3"
        SCREEN[📸 Screenshot]
    end

    subgraph "Stage 4"
        SONAR[🔎 SonarCloud]
    end

    BUILD --> LINT
    BUILD --> TEST
    BUILD --> INSTR
    BUILD --> SCREEN

    LINT --> SONAR
    INSTR --> SONAR
    TEST --> SONAR

    style BUILD fill:#4CAF50,color:#fff
    style LINT fill:#2196F3,color:#fff
    style TEST fill:#9C27B0,color:#fff
    style SCREEN fill:#FF9800,color:#fff
    style INSTR fill:#E91E63,color:#fff
    style SONAR fill:#00BCD4,color:#fff
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

### Running Locally

```bash
# Full CI check
./gradlew assembleDebug detekt ktlintCheck lintDebug testDebugUnitTest

# Coverage report
./gradlew jacocoTestReport

# Screenshot tests
./gradlew verifyRoborazziDebug

# Record new screenshots
./gradlew recordRoborazziDebug
```

### Required GitHub Secrets

| Secret                   | Description                         |
|--------------------------|-------------------------------------|
| `SONAR_TOKEN`            | Authentication token for SonarCloud |
| `SONAR_PROJECT_KEY`      | Your SonarCloud project identifier  |
| `SONAR_ORGANIZATION_KEY` | Your SonarCloud organization        |

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

---

## 5. How to Run the Project

### Prerequisites

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

## 6. Screenshot Testing (Visual Regression)

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
    PASS -->|Yes| SUCCESS
    PASS -->|No| FAIL
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

## 7. File Organization

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

    DATA --> DATA_MAIN[main/kotlin/.../data/]
    DATA --> DATA_TEST[test/]

    DATA_MAIN --> DI[di/]
    DATA_MAIN --> REPO_[repository/]

    style ROOT fill:#9C27B0,color:#fff
    style APP fill:#4CAF50,color:#fff
    style DATA fill:#FF9800,color:#fff
    style DOMAIN fill:#2196F3,color:#fff
    style GAME fill:#E91E63,color:#fff
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

This project is created for the BNP Paribas Kata exercise.

---

<p align="center">
  Made with ❤️ using Test-Driven Development
</p>

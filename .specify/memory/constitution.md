<!--
SYNC IMPACT REPORT
==================
Version change: 1.0.0 → 1.1.0 (MINOR - new sections added)

Modified sections:
- Development Workflow: Added containerized development environment
- Development Workflow: Added CI/CD with GitHub Actions

Added sections:
- Development Environment (containerized Android dev)
- CI/CD Pipeline (GitHub Actions)

Removed sections: None

Templates requiring updates:
- .specify/templates/plan-template.md ✅ No changes needed (generic template)
- .specify/templates/spec-template.md ✅ No changes needed (generic template)
- .specify/templates/tasks-template.md ✅ No changes needed (generic template)

Follow-up TODOs: None
-->

# DayRater Constitution

## Core Principles

### I. Modern Android First

All development MUST use current Android best practices as of 2026:

- **Language**: Kotlin only (no Java)
- **UI**: Jetpack Compose exclusively (no XML Views)
- **Architecture**: Follow official Android architecture guidelines (ViewModel, Repository pattern where appropriate)
- **Material Design**: Material 3 components and theming
- **Target SDK**: Latest stable Android SDK; minimum SDK chosen to cover family devices

**Rationale**: A maintainable OSS project requires modern, well-supported technologies. Compose and Kotlin are the official recommended stack and have the best tooling, documentation, and community support.

### II. Local-First, Export-Ready

Data MUST be stored locally with optional export capabilities:

- **Primary Storage**: Room (SQLite) on-device database
- **No Mandatory Cloud**: App MUST function fully offline with zero network connectivity
- **Export Options**: Users MUST be able to export data via Android share intents (Google Drive, Gmail, file system, etc.)
- **No Backend**: No server infrastructure, no accounts, no sync services to maintain

**Rationale**: This is a family side-project. Eliminating cloud dependencies removes hosting costs, privacy concerns, and operational burden. Export via share intents leverages the Android ecosystem without custom integrations.

### III. Pragmatic Quality

Code MUST be maintainable for OSS but not over-engineered:

- **Test What Matters**: Unit tests for business logic (rating calculations, data transformations); UI tests only for critical user flows
- **Keep It Simple**: Prefer straightforward solutions over abstract patterns; add complexity only when pain is felt
- **Document Intent**: README and code comments explain *why*, not just *what*
- **Accessibility Built-In**: Support dynamic text scaling, Material color themes (including dark mode), and standard Android accessibility features

**Rationale**: As a solo-developer OSS project, time is limited. Tests should catch real bugs, not satisfy coverage metrics. Simplicity aids future contributors and your future self.

## Technology Stack

| Layer | Technology | Notes |
|-------|------------|-------|
| Language | Kotlin 2.x | Coroutines for async |
| UI | Jetpack Compose | Material 3 theming |
| Navigation | Compose Navigation | Type-safe navigation |
| Database | Room | SQLite wrapper |
| DI | Hilt | Optional but recommended |
| Build | Gradle (Kotlin DSL) | Version catalogs |
| Min SDK | TBD (based on family devices) | Target latest stable |

**Prohibited**:
- Java source files
- XML layouts (except Android Manifest and resources)
- Third-party cloud SDKs (Firebase, AWS, etc.)
- PWA/web wrappers

## Development Environment

Development SHOULD use containerized tooling to avoid local SDK installation:

- **Dev Container**: Use VS Code Dev Containers or GitHub Codespaces with Android SDK pre-configured
- **No Local SDK Required**: Contributors SHOULD NOT need to install Java, Android SDK, or Gradle locally
- **Reproducible Builds**: Container ensures consistent build environment across machines
- **Emulator Option**: For UI testing, use Android Emulator in container (if supported) or connect to physical device via ADB over network

**Rationale**: Containerized development lowers the barrier to contribution and eliminates "works on my machine" issues. Family members or future contributors can build without SDK setup.

## Development Workflow

**Solo Developer Process**:

1. **Branch per feature**: `feature/XXX-description` or `fix/XXX-description`
2. **Commits**: Conventional commits format (`feat:`, `fix:`, `docs:`, `refactor:`)
3. **Self-review**: Review diff before merge; no PR approval required (solo project)
4. **Release**: Semantic versioning; APK built and distributed via GitHub Releases for sideloading

**Quality Gates** (automated via GitHub Actions):

- [ ] Code compiles without warnings
- [ ] Lint checks pass (`./gradlew lint`)
- [ ] Unit tests pass (`./gradlew test`)
- [ ] App installs and runs on a test device/emulator (manual)

## CI/CD Pipeline

All automation runs via **GitHub Actions**:

| Trigger | Workflow | Artifacts |
|---------|----------|----------|
| Push to `main` | Build + Test | — |
| Pull Request | Build + Test + Lint | — |
| Tag `v*.*.*` | Build + Test + Release | Signed APK to GitHub Releases |

**Release Process**:
1. Tag commit with semantic version: `git tag v1.0.0 && git push --tags`
2. GitHub Actions builds release APK
3. APK automatically uploaded to GitHub Releases
4. Family members download APK from Releases page and sideload

**Rationale**: GitHub Actions provides free CI/CD for public repos. Releases page is the single source of truth for obtaining the app—no Play Store or external hosting needed.

## Governance

This constitution governs all development decisions for DayRater. When in doubt, refer to these principles.

**Amendment Process**:
1. Propose change in a GitHub issue or commit message
2. Update this document with rationale
3. Increment version per semantic versioning:
   - MAJOR: Principle removed or fundamentally changed
   - MINOR: New principle or section added
   - PATCH: Clarifications and typo fixes

**Compliance**: All feature specs and implementation plans SHOULD reference relevant principles. Deviations MUST be documented with justification.

**Version**: 1.1.0 | **Ratified**: 2026-01-16 | **Last Amended**: 2026-01-16

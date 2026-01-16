# Research: Daily Rating Core

**Feature**: 001-daily-rating  
**Date**: 2026-01-16  
**Purpose**: Resolve technical decisions and document best practices for implementation

## Research Tasks

### 1. Android Project Structure (2026 Best Practices)

**Decision**: Use feature-based modular structure with conventional Android layers

**Rationale**: Modern Android projects organize by feature (vertical slices) rather than by layer (horizontal). This improves build times, enforces separation, and makes navigation intuitive. For a smaller app like DayRater, a single module with feature packages is sufficient—multi-module can be added later if needed.

**Alternatives Considered**:
- Multi-module from start: Rejected—overkill for initial scope, adds Gradle complexity
- Pure layer-based (data/domain/ui folders): Rejected—harder to navigate as features grow

**Structure**:
```
app/src/main/java/com/dayrater/
├── MainActivity.kt                 # Single activity, Compose navigation
├── DayRaterApplication.kt          # Application class (Hilt setup)
├── di/                             # Hilt modules
│   └── DatabaseModule.kt
├── data/                           # Data layer
│   ├── local/
│   │   ├── DayRaterDatabase.kt     # Room database
│   │   ├── dao/
│   │   │   ├── RatingDao.kt
│   │   │   ├── CategoryDao.kt
│   │   │   └── FamilyMemberDao.kt
│   │   └── entity/
│   │       ├── DailyRatingEntity.kt
│   │       ├── CategoryEntity.kt
│   │       └── FamilyMemberEntity.kt
│   └── repository/
│       ├── RatingRepository.kt
│       └── SettingsRepository.kt
├── domain/                         # Domain/business logic (optional layer)
│   └── model/
│       ├── Rating.kt
│       ├── Category.kt
│       └── FamilyMember.kt
└── ui/                             # Presentation layer
    ├── theme/
    │   ├── Theme.kt
    │   ├── Color.kt
    │   └── Type.kt
    ├── navigation/
    │   └── NavGraph.kt
    ├── rating/                     # Rating feature
    │   ├── RatingScreen.kt
    │   └── RatingViewModel.kt
    ├── history/                    # History feature
    │   ├── HistoryScreen.kt
    │   └── HistoryViewModel.kt
    ├── settings/                   # Settings feature
    │   ├── SettingsScreen.kt
    │   └── SettingsViewModel.kt
    └── components/                 # Shared UI components
        ├── EmojiRatingSelector.kt
        └── CategoryCard.kt
```

---

### 2. Room Database Design

**Decision**: Single Room database with three main tables, using foreign keys for relationships

**Rationale**: Room provides compile-time SQL verification, LiveData/Flow integration, and migrations. The schema is simple enough for a single database file.

**Schema Design**:

```kotlin
// Entities
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: CategoryType, // DEFAULT, CUSTOM, SPOUSE, CHILD
    val familyMemberId: Long? = null, // FK to family_members for SPOUSE/CHILD types
    val displayOrder: Int,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationshipType: RelationshipType, // SPOUSE, CHILD
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "daily_ratings",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("date"), Index("categoryId")]
)
data class DailyRatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate, // Using TypeConverter
    val categoryId: Long?,
    val ratingValue: RatingValue, // NEGATIVE, NEUTRAL, POSITIVE
    val updatedAt: Long = System.currentTimeMillis()
)
```

**Alternatives Considered**:
- Denormalized single table: Rejected—harder to query by category, wastes space
- Separate database per feature: Rejected—unnecessary complexity

---

### 3. Jetpack Compose Navigation

**Decision**: Use Compose Navigation with type-safe routes (Navigation 2.8+ style)

**Rationale**: Type-safe navigation avoids string-based route errors. Kotlin serialization for arguments is now the recommended approach.

**Routes**:
```kotlin
@Serializable sealed class Screen {
    @Serializable data object Rating : Screen()
    @Serializable data object History : Screen()
    @Serializable data class DayDetail(val date: String) : Screen() // ISO date string
    @Serializable data object Settings : Screen()
    @Serializable data object FamilySetup : Screen()
    @Serializable data object CustomCategories : Screen()
    @Serializable data object Export : Screen()
}
```

**Alternatives Considered**:
- String-based routes: Rejected—error-prone, no compile-time safety
- Third-party navigation (Voyager, Decompose): Rejected—adds dependency, Compose Navigation is sufficient

---

### 4. State Management Pattern

**Decision**: ViewModel + StateFlow + Repository pattern

**Rationale**: This is the official Android architecture recommendation. StateFlow provides lifecycle-aware state observation in Compose.

**Pattern**:
```kotlin
// ViewModel exposes UI state as StateFlow
class RatingViewModel @Inject constructor(
    private val ratingRepository: RatingRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()
    
    fun onRatingSelected(categoryId: Long, rating: RatingValue) {
        viewModelScope.launch {
            ratingRepository.saveRating(categoryId, rating)
        }
    }
}

// Compose collects state
@Composable
fun RatingScreen(viewModel: RatingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Render based on uiState
}
```

**Alternatives Considered**:
- MVI with explicit intents: Rejected—adds boilerplate, overkill for this scope
- Compose State only (no ViewModel): Rejected—loses lifecycle awareness, harder to test

---

### 5. Dependency Injection with Hilt

**Decision**: Use Hilt for DI

**Rationale**: Hilt is the recommended DI solution for Android. It integrates well with ViewModel, provides compile-time verification, and reduces boilerplate compared to manual DI.

**Setup**:
```kotlin
@HiltAndroidApp
class DayRaterApplication : Application()

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DayRaterDatabase =
        Room.databaseBuilder(context, DayRaterDatabase::class.java, "dayrater.db")
            .build()
    
    @Provides
    fun provideRatingDao(db: DayRaterDatabase): RatingDao = db.ratingDao()
}
```

**Alternatives Considered**:
- Koin: Rejected—runtime DI, less integration with Android components
- Manual DI: Rejected—more boilerplate, error-prone

---

### 6. Data Export Format

**Decision**: Export as CSV (primary) with optional JSON

**Rationale**: CSV is universally readable in spreadsheet software (Excel, Google Sheets). JSON is useful for programmatic consumption or backup/restore.

**CSV Format**:
```csv
date,category,category_type,rating,rating_label
2026-01-15,Overall Day,DEFAULT,POSITIVE,😊
2026-01-15,Interactions with Alex,SPOUSE,NEUTRAL,😐
2026-01-15,Physical Activity,DEFAULT,NEGATIVE,😢
```

**Export Implementation**:
```kotlin
// Use Android ShareSheet via Intent
fun exportData(context: Context, csvContent: String) {
    val file = File(context.cacheDir, "dayrater_export_${LocalDate.now()}.csv")
    file.writeText(csvContent)
    
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Export Ratings"))
}
```

---

### 7. Dev Container Setup

**Decision**: Use existing Android Dev Container images with SDK 35

**Ionale**: Pre-built images from the community (e.g., `mobiledevops/android-sdk-image` or custom Dockerfile) provide Android SDK, Gradle, and JDK without local installation.

**devcontainer.json**:
```json
{
  "name": "DayRater Android",
  "image": "mcr.microsoft.com/devcontainers/base:ubuntu",
  "features": {
    "ghcr.io/devcontainers/features/java:1": { "version": "21" }
  },
  "postCreateCommand": "bash .devcontainer/setup-android-sdk.sh",
  "customizations": {
    "vscode": {
      "extensions": ["mathiasfrohlich.Kotlin"]
    }
  }
}
```

**Alternatives Considered**:
- Full Android Studio in container: Rejected—heavy, not needed for CLI builds
- No container: Rejected—constitution requires containerized dev

---

### 8. GitHub Actions CI/CD

**Decision**: Use `android-actions/setup-android` with Gradle caching

**Workflow**:
```yaml
name: Build & Test
on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
      - uses: android-actions/setup-android@v3
      - uses: gradle/actions/setup-gradle@v3
      - run: ./gradlew build test lint
      
  release:
    if: startsWith(github.ref, 'refs/tags/v')
    needs: build
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: 'temurin', java-version: '21' }
      - uses: android-actions/setup-android@v3
      - run: ./gradlew assembleRelease
      - uses: softprops/action-gh-release@v1
        with:
          files: app/build/outputs/apk/release/*.apk
```

---

## Summary

All technical decisions align with the constitution and 2026 Android best practices:

| Area | Decision | Constitution Alignment |
|------|----------|------------------------|
| Language | Kotlin 2.x | ✅ I. Modern Android First |
| UI | Jetpack Compose + Material 3 | ✅ I. Modern Android First |
| Architecture | ViewModel + Repository | ✅ I. Modern Android First |
| Storage | Room (SQLite) | ✅ II. Local-First |
| Export | CSV via Share Intent | ✅ II. Local-First |
| DI | Hilt | ✅ I. Modern Android First |
| Testing | JUnit 5 + Compose Test | ✅ III. Pragmatic Quality |
| Dev Environment | Dev Containers | ✅ Development Environment |
| CI/CD | GitHub Actions | ✅ CI/CD Pipeline |

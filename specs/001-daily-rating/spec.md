# Feature Specification: Daily Rating Core

**Feature Branch**: `001-daily-rating`  
**Created**: 2026-01-16  
**Status**: Draft  
**Input**: Core daily rating functionality for tracking daily vibes across multiple life categories

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Rate My Day (Priority: P1)

As a user, I want to quickly rate my day across multiple categories so I can track how different aspects of my life are going. Each evening (or whenever I choose), I open the app and see today's rating card. I tap through each category and select one of three emoji faces (sad 😢, neutral 😐, happy 😊) to record how that area went. The categories include: overall day, interactions with my spouse, interactions with each child, physical activity, emotional state, and self-care. Once I've rated all categories, my entry is saved automatically.

**Why this priority**: This is the core value proposition—without the ability to rate your day, the app has no purpose. Everything else builds on this foundation.

**Independent Test**: Open the app, tap through all default categories selecting emoji ratings, close the app, reopen it—the ratings should persist and be visible for today.

**Acceptance Scenarios**:

1. **Given** the app is open on a new day with no ratings recorded, **When** I view the rating screen, **Then** I see all configured categories with no selection (or a "not yet rated" state)
2. **Given** I am on the rating screen, **When** I tap an emoji for a category, **Then** that emoji is visually selected and the rating is saved immediately
3. **Given** I have already rated a category today, **When** I tap a different emoji, **Then** my rating updates to the new selection
4. **Given** I have rated some categories, **When** I close and reopen the app, **Then** my ratings for today are still visible and unchanged
5. **Given** it is a new calendar day, **When** I open the app, **Then** I see a fresh rating card with no selections (previous day's data is preserved in history)

---

### User Story 2 - Configure Family Members (Priority: P2)

As a user, I want to set up my family structure (spouse name, children names) so that the rating categories are personalized to my family. During first launch or from settings, I enter my spouse's name and add each child's name. The app then shows rating categories like "Interactions with Sarah" and "Interactions with Tommy" instead of generic labels.

**Why this priority**: Personalization makes the app meaningful for family use. Without configured names, the "child interactions" category is ambiguous and less engaging.

**Independent Test**: Open settings, add spouse name "Alex" and two children "Emma" and "Jack", return to rating screen—categories should show "Interactions with Alex", "Interactions with Emma", "Interactions with Jack".

**Acceptance Scenarios**:

1. **Given** I am in settings, **When** I enter a spouse name, **Then** the name is saved and reflected in the rating category label
2. **Given** I am in settings, **When** I add a child name, **Then** a new rating category appears for that child
3. **Given** I have multiple children configured, **When** I remove one child, **Then** their rating category is removed (historical data preserved)
4. **Given** I have not configured any family members, **When** I view the rating screen, **Then** I see generic labels ("Spouse", "Child 1") or a prompt to configure family
5. **Given** I change a family member's name, **When** I view history, **Then** historical entries show the name as it was when recorded (or update to new name—either is acceptable)

---

### User Story 3 - View Rating History (Priority: P3)

As a user, I want to see my past ratings so I can reflect on patterns and trends over time. I can browse a calendar or list view showing which days I rated and what my ratings were. I can tap into any day to see the full breakdown by category.

**Why this priority**: Historical view enables reflection, which is a key benefit of mood tracking. However, the app still provides value (daily recording habit) without this feature.

**Independent Test**: Rate several days, navigate to history view, see a list/calendar of rated days, tap one day to see its category breakdown.

**Acceptance Scenarios**:

1. **Given** I have rated multiple days, **When** I open the history view, **Then** I see a list or calendar showing days with ratings
2. **Given** I am viewing history, **When** I tap on a specific day, **Then** I see all category ratings for that day
3. **Given** I have no rating history, **When** I open history view, **Then** I see an empty state with helpful guidance
4. **Given** I am viewing a past day's ratings, **When** I look at the display, **Then** the ratings are read-only (cannot edit past days)

---

### User Story 4 - Add Custom Categories (Priority: P4)

As a user, I want to add my own rating categories beyond the defaults so I can track aspects of life that matter specifically to me (e.g., "Work Stress", "Sleep Quality", "Gratitude").

**Why this priority**: Customization extends the app's usefulness but the default categories cover the most common use cases. This is an enhancement.

**Independent Test**: Go to settings, add a custom category "Work Stress", return to rating screen—"Work Stress" appears as a new category to rate.

**Acceptance Scenarios**:

1. **Given** I am in settings, **When** I add a custom category with a name, **Then** it appears in my rating screen
2. **Given** I have custom categories, **When** I delete one, **Then** it is removed from rating screen (historical data preserved)
3. **Given** I have the maximum reasonable number of categories, **When** I try to add another, **Then** I see guidance about practical limits (soft limit, not hard block)

---

### User Story 5 - Export My Data (Priority: P5)

As a user, I want to export my rating history so I can back it up, analyze it in a spreadsheet, or share it with a therapist/coach. I can export to a standard format (CSV or JSON) and share via Android's share sheet (email, Google Drive, file manager, etc.).

**Why this priority**: Data export supports the "local-first, export-ready" principle but isn't needed for daily use. Important for data ownership.

**Independent Test**: Accumulate some rating history, go to settings, tap export, choose share target—receive a file with all ratings in readable format.

**Acceptance Scenarios**:

1. **Given** I have rating history, **When** I tap export, **Then** the Android share sheet opens with my data file
2. **Given** I export my data, **When** I open the file, **Then** it contains all my ratings in a structured, readable format (CSV or JSON)
3. **Given** I have no rating history, **When** I tap export, **Then** I see a message that there's nothing to export
4. **Given** I share to Google Drive, **When** the share completes, **Then** the file is accessible in my Drive

---

### Edge Cases

- What happens when the user rates the same category multiple times in one day? → Last rating wins; each tap updates the existing rating
- What happens if the user opens the app exactly at midnight? → Use device's current date at time of rating; if date changes mid-session, next rating action uses new date
- What happens if a child is removed but has historical ratings? → Historical data is preserved and visible in history; category just doesn't appear for new days
- What happens with very long family member names? → Truncate display with ellipsis; full name visible on tap or in settings
- What happens if the device date is changed backward? → Allow rating for the "new" current date; don't overwrite future dates that were already rated (treat as separate entries)

## Requirements *(mandatory)*

### Functional Requirements

**Rating Core**
- **FR-001**: App MUST display all configured rating categories for the current day
- **FR-002**: Each category MUST support exactly three rating states: negative (😢), neutral (😐), positive (😊)
- **FR-003**: Users MUST be able to change a rating by tapping a different emoji (updates immediately)
- **FR-004**: Ratings MUST persist locally and survive app restart
- **FR-005**: App MUST treat each calendar day (device local time) as a separate rating period
- **FR-006**: App MUST auto-save ratings immediately upon selection (no explicit save button)

**Default Categories**
- **FR-007**: App MUST include these default categories: Overall Day, Spouse Interactions, Physical Activity, Emotional State, Self-Care
- **FR-008**: App MUST create one "Child Interactions" category per configured child
- **FR-009**: Categories without a configured person (no spouse set, no children) SHOULD show a generic label or be hidden until configured

**Family Configuration**
- **FR-010**: Users MUST be able to set a spouse name in settings
- **FR-011**: Users MUST be able to add, rename, and remove children in settings
- **FR-012**: Family member names MUST be reflected in rating category labels
- **FR-013**: Removing a family member MUST preserve their historical rating data

**Custom Categories**
- **FR-014**: Users MUST be able to create custom rating categories with a user-defined name
- **FR-015**: Users MUST be able to delete custom categories (historical data preserved)
- **FR-016**: Custom categories MUST behave identically to default categories (same 3-emoji scale)

**History**
- **FR-017**: Users MUST be able to view a list of past days with ratings
- **FR-018**: Users MUST be able to view the category breakdown for any historical day
- **FR-019**: Historical ratings MUST be read-only (no editing past days)

**Export**
- **FR-020**: Users MUST be able to export all rating data to a shareable file format
- **FR-021**: Export MUST use Android share intents (no direct cloud integration)
- **FR-022**: Export format MUST be human-readable (CSV or JSON)

**Accessibility & Settings**
- **FR-023**: App MUST respect system text size settings (dynamic type)
- **FR-024**: App MUST support light and dark color themes
- **FR-025**: App MUST function fully offline with zero network connectivity

### Key Entities

- **DailyRating**: A collection of category ratings for a single calendar day. Key attributes: date, list of category ratings, timestamp of last modification
- **CategoryRating**: A single rating within a day. Key attributes: category reference, rating value (negative/neutral/positive), timestamp
- **Category**: A ratable aspect of the user's day. Key attributes: name, type (default vs custom vs family-member), display order, active status
- **FamilyMember**: A configured person (spouse or child). Key attributes: name, relationship type, creation date
- **UserSettings**: App-wide preferences. Key attributes: configured family members, custom categories, theme preference

## Assumptions

- Users will rate once per day, typically in the evening (but can rate/update anytime during the day)
- Family size is small (1 spouse, 0-6 children typical); no hard limits but UI optimized for <15 total categories
- Users understand emoji meaning intuitively (sad=bad, neutral=okay, happy=good); no onboarding tutorial needed
- Device clock is reasonably accurate; app trusts system date for day boundaries
- Export is for backup/analysis; no import functionality in initial version

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can complete a full day rating (all categories) in under 30 seconds
- **SC-002**: App launches and is ready to rate within 2 seconds on a mid-range device
- **SC-003**: 100% of rating data persists correctly across app restarts and device reboots
- **SC-004**: Users can configure family members and see changes reflected immediately (no app restart)
- **SC-005**: Exported data file can be opened and read in standard spreadsheet software or text editor
- **SC-006**: App functions identically with no network connection as with full connectivity
- **SC-007**: History view loads and displays 1 year of daily ratings smoothly (no perceptible lag)

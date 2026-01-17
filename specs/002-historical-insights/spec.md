# Feature Specification: Historical Insights

**Feature Branch**: `002-historical-insights`  
**Created**: 2026-01-17  
**Status**: Draft  
**Input**: Historical summary, tracking, and visualization features for rating data

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Weekly Summary View (Priority: P1)

As a user, I want to see a summary of my past week so I can quickly understand how my week went across all categories. When I open the weekly view, I see the current week with each category showing an average or distribution of my ratings. I can swipe or tap to navigate to previous weeks.

**Why this priority**: Weekly reflection is the most actionable time frame—short enough to remember specifics, long enough to see patterns. This is the foundation for all other insights.

**Independent Test**: Rate at least 4 days, navigate to weekly summary, see aggregated data for each category with visual indicators.

**Acceptance Scenarios**:

1. **Given** I have rated at least one day this week, **When** I open the weekly summary, **Then** I see all my categories with their weekly performance
2. **Given** I am viewing the weekly summary, **When** I see a category row, **Then** I see a visual indicator (emoji distribution, average, or mini bar) showing that week's ratings
3. **Given** I am viewing the current week, **When** I swipe left or tap "previous", **Then** I see the previous week's summary
4. **Given** I have no ratings for a week, **When** I view that week's summary, **Then** I see an empty state indicating no data
5. **Given** I am viewing a weekly summary, **When** I tap on a category, **Then** I see the daily breakdown for that category that week

---

### User Story 2 - Monthly Overview (Priority: P2)

As a user, I want to see a monthly overview with a calendar heat map so I can visualize my rating patterns over the month at a glance. Each day shows a color or indicator based on my overall rating (or average across categories).

**Why this priority**: Monthly view provides the "big picture" perspective that helps identify longer-term patterns and is visually satisfying for habit tracking.

**Independent Test**: Rate several days across a month, open monthly view, see a calendar with color-coded days based on ratings.

**Acceptance Scenarios**:

1. **Given** I have ratings for multiple days in a month, **When** I open the monthly overview, **Then** I see a calendar grid with days color-coded by rating
2. **Given** I am viewing the monthly calendar, **When** I see a rated day, **Then** the color indicates the overall mood (red/yellow/green or similar gradient)
3. **Given** I am viewing a month, **When** I tap on a specific day, **Then** I navigate to that day's detail view
4. **Given** I am viewing the current month, **When** I swipe or tap navigation, **Then** I can browse to previous/future months
5. **Given** a day has no rating, **When** I view the calendar, **Then** that day shows as unrated (gray or empty)

---

### User Story 3 - Trend Graphs (Priority: P3)

As a user, I want to see line graphs showing how my ratings change over time so I can visualize trends and patterns. I can view graphs for any category over selectable time periods (week, month, 3 months, year).

**Why this priority**: Graphs provide the clearest visualization of improvement or decline over time, but require more data to be meaningful. Useful after users have accumulated history.

**Independent Test**: Rate for 2+ weeks, open trend view, select a category, see a line graph showing rating values over time.

**Acceptance Scenarios**:

1. **Given** I have rating history, **When** I open the trends view, **Then** I see a graph for my overall day rating
2. **Given** I am viewing trends, **When** I select a different category, **Then** the graph updates to show that category's history
3. **Given** I am viewing a graph, **When** I select a different time range (week/month/3mo/year), **Then** the graph adjusts to show that period
4. **Given** I have sparse data (gaps in rating), **When** I view the graph, **Then** gaps are handled gracefully (connected lines or visible gaps)
5. **Given** I tap on a point in the graph, **When** I interact with it, **Then** I see the specific date and rating value

---

### User Story 4 - Statistics Dashboard (Priority: P4)

As a user, I want to see key statistics about my rating history so I can understand my patterns numerically. Statistics include: current streak, longest streak, average ratings by category, best/worst days of the week, and rating distribution.

**Why this priority**: Statistics provide concrete numbers that complement visual trends. Streaks are particularly motivating for habit formation.

**Independent Test**: Accumulate 2+ weeks of ratings, open statistics view, see streak count, averages, and distribution breakdown.

**Acceptance Scenarios**:

1. **Given** I have consecutive days rated, **When** I view statistics, **Then** I see my current rating streak
2. **Given** I have historical data, **When** I view statistics, **Then** I see my longest-ever streak
3. **Given** I have ratings across categories, **When** I view statistics, **Then** I see average rating per category
4. **Given** I have sufficient data, **When** I view statistics, **Then** I see which day of the week tends to be best/worst
5. **Given** I have rating history, **When** I view statistics, **Then** I see the distribution of 😢/😐/😊 ratings overall

---

### Edge Cases

- What if user has only 1-2 days of data? → Show available data with messaging that more data improves insights
- What if user rates inconsistently (many gaps)? → Handle gracefully; don't imply failed streaks harshly
- What about weeks that span two months? → Use ISO week definition (Monday start) or device locale preference
- What if a category was added mid-month? → Only show data from when category existed; note partial data
- What about deleted categories? → Include historical data in insights; mark as "archived category"
- What about timezone changes/travel? → Use the date that was recorded at rating time; don't retroactively adjust

## Requirements *(mandatory)*

### Functional Requirements

**Weekly Summary**
- **FR-001**: App MUST display a weekly summary view accessible from main navigation or history
- **FR-002**: Weekly summary MUST show all active categories with their weekly aggregation
- **FR-003**: Weekly aggregation MUST display as emoji distribution count (e.g., 3😊 2😐 1😢) or visual bar
- **FR-004**: Users MUST be able to navigate to previous/next weeks
- **FR-005**: Tapping a category in weekly view MUST show daily breakdown for that category

**Monthly Overview**
- **FR-006**: App MUST display a monthly calendar view with heat map coloring
- **FR-007**: Heat map coloring MUST indicate overall day rating (positive=green, neutral=yellow, negative=red)
- **FR-008**: Unrated days MUST be visually distinct (gray or empty)
- **FR-009**: Users MUST be able to navigate to previous/next months
- **FR-010**: Tapping a day in the calendar MUST navigate to that day's detail view

**Trend Graphs**
- **FR-011**: App MUST display line graphs for rating trends over time
- **FR-012**: Users MUST be able to select which category to graph
- **FR-013**: Users MUST be able to select time range: 1 week, 1 month, 3 months, 1 year, all time
- **FR-014**: Graph MUST handle missing days gracefully (interpolation or gaps)
- **FR-015**: Graph data points SHOULD be tappable to show exact date/value

**Statistics**
- **FR-016**: App MUST display current consecutive-day rating streak
- **FR-017**: App MUST display longest-ever rating streak
- **FR-018**: App MUST display average rating per category (as numeric or emoji equivalent)
- **FR-019**: App SHOULD display best/worst day of week (requires 2+ weeks of data)
- **FR-020**: App MUST display overall rating distribution (count of each emoji type)

**General**
- **FR-021**: All insights MUST update in real-time as new ratings are added
- **FR-022**: Insights MUST work fully offline (computed locally from Room data)
- **FR-023**: Insights views MUST respect system text size and theme settings

### Key Entities

- **WeeklySummary**: Aggregated data for a 7-day period. Key attributes: start date, end date, list of category summaries, days rated count
- **CategorySummary**: Aggregation for one category over a period. Key attributes: category reference, positive/neutral/negative counts, average score
- **MonthlyCalendarData**: A month's worth of daily rating indicators. Key attributes: year, month, map of day→rating indicator
- **TrendDataPoint**: A single point on a trend graph. Key attributes: date, rating value (or null if not rated)
- **UserStatistics**: Computed statistics. Key attributes: current streak, longest streak, category averages, day-of-week averages, total counts

## Assumptions

- Users have already been using the app for at least a few days (insights are less useful on day 1)
- Weekly/monthly boundaries follow device locale or ISO standard (configurable later if needed)
- "Streak" means consecutive calendar days with at least one rating recorded
- Average calculations convert emoji to numeric: 😢=1, 😐=2, 😊=3 (or 0/1/2)
- Graph library (if used) will be a lightweight Compose-compatible option (Vico, YCharts, or custom Canvas)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Weekly summary loads and displays within 500ms for 1 year of data
- **SC-002**: Monthly calendar renders smoothly (60fps) when navigating between months
- **SC-003**: Trend graphs render within 1 second for 1 year of daily data
- **SC-004**: All statistics compute correctly (verified by unit tests against known data sets)
- **SC-005**: Users can access insights with maximum 2 taps from the rating screen
- **SC-006**: Insights views handle edge cases (no data, partial data, gaps) without crashes or confusing UI
- **SC-007**: All insight computations work offline with zero network dependency


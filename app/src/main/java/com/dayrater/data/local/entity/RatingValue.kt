package com.dayrater.data.local.entity

/**
 * Represents the three possible rating values for any category.
 * Maps to emoji display in the UI.
 */
enum class RatingValue(val emoji: String, val label: String) {
    NEGATIVE("😢", "Sad"),
    NEUTRAL("😐", "Neutral"),
    POSITIVE("😊", "Happy")
}

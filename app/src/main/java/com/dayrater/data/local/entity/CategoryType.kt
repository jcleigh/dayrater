package com.dayrater.data.local.entity

/**
 * Categorizes the type of rating category.
 * Used to distinguish between built-in, family-member-based, and custom categories.
 */
enum class CategoryType {
    /** Built-in categories: Overall Day, Physical Activity, Emotional State, Self-Care */
    DEFAULT,
    
    /** Auto-created category for spouse interactions */
    SPOUSE,
    
    /** Auto-created category for child interactions (one per child) */
    CHILD,
    
    /** User-defined custom categories */
    CUSTOM
}

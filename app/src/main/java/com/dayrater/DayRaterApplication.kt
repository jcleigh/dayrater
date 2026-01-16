package com.dayrater

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * DayRater Application class.
 * 
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 * throughout the application.
 */
@HiltAndroidApp
class DayRaterApplication : Application()

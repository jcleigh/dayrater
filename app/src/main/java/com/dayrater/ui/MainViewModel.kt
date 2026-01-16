package com.dayrater.ui

import androidx.lifecycle.ViewModel
import com.dayrater.data.repository.SettingsRepository
import com.dayrater.data.repository.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel for the main activity to manage app-level settings like theme.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    /**
     * The current theme mode.
     */
    val themeMode: Flow<ThemeMode> = settingsRepository.themeMode
}

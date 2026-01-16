package com.dayrater.ui.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.repository.RatingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel for the export screen.
 */
@HiltViewModel
class ExportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ratingRepository: RatingRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()
    
    fun onEvent(event: ExportEvent) {
        when (event) {
            is ExportEvent.SelectDateRange -> {
                _uiState.update { it.copy(selectedDateRange = event.range) }
            }
            is ExportEvent.SetCustomStartDate -> {
                _uiState.update { it.copy(customStartDate = event.date) }
            }
            is ExportEvent.SetCustomEndDate -> {
                _uiState.update { it.copy(customEndDate = event.date) }
            }
            is ExportEvent.SelectFormat -> {
                _uiState.update { it.copy(exportFormat = event.format) }
            }
            is ExportEvent.Export -> exportData()
            is ExportEvent.Share -> shareExportedFile()
            is ExportEvent.DismissSuccess -> {
                _uiState.update { it.copy(exportSuccess = false) }
            }
            is ExportEvent.DismissError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }
    
    private fun exportData() {
        val currentState = _uiState.value
        
        // Validate custom range if selected
        if (currentState.isCustomRange) {
            if (currentState.customStartDate == null || currentState.customEndDate == null) {
                _uiState.update { it.copy(error = "Please select both start and end dates") }
                return
            }
            if (currentState.customStartDate.isAfter(currentState.customEndDate)) {
                _uiState.update { it.copy(error = "Start date must be before end date") }
                return
            }
        }
        
        _uiState.update { it.copy(isExporting = true) }
        
        viewModelScope.launch {
            try {
                val content = when (currentState.exportFormat) {
                    ExportFormat.CSV -> ratingRepository.exportToCsv(
                        currentState.effectiveStartDate,
                        currentState.effectiveEndDate
                    )
                    ExportFormat.JSON -> ratingRepository.exportToJson(
                        currentState.effectiveStartDate,
                        currentState.effectiveEndDate
                    )
                }
                
                if (content.lines().size <= 1 && currentState.exportFormat == ExportFormat.CSV) {
                    _uiState.update { it.copy(
                        isExporting = false,
                        error = "No data to export for the selected period"
                    ) }
                    return@launch
                }
                
                // Save to file
                val fileName = generateFileName(currentState.exportFormat)
                val file = saveToFile(fileName, content)
                
                _uiState.update { it.copy(
                    isExporting = false,
                    exportSuccess = true,
                    exportedFilePath = file.absolutePath
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isExporting = false,
                    error = e.message ?: "Export failed"
                ) }
            }
        }
    }
    
    private fun generateFileName(format: ExportFormat): String {
        val timestamp = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val extension = when (format) {
            ExportFormat.CSV -> "csv"
            ExportFormat.JSON -> "json"
        }
        return "dayrater_export_$timestamp.$extension"
    }
    
    private fun saveToFile(fileName: String, content: String): File {
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        
        val file = File(exportDir, fileName)
        file.writeText(content)
        return file
    }
    
    private fun shareExportedFile() {
        val filePath = _uiState.value.exportedFilePath ?: return
        val file = File(filePath)
        
        if (!file.exists()) {
            _uiState.update { it.copy(error = "Export file not found") }
            return
        }
        
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            
            val mimeType = when {
                file.name.endsWith(".csv") -> "text/csv"
                file.name.endsWith(".json") -> "application/json"
                else -> "text/plain"
            }
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            val chooserIntent = Intent.createChooser(shareIntent, "Share DayRater Export")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Failed to share: ${e.message}") }
        }
    }
}

/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 05/10/2025
 * @version v1
 */
package com.SM_BSC.stepfriend.ui.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Dataset
data class MainUIState (
    val totalSteps: Int = 0, // Total steps overall that have been saved
    val todaySteps: Int = 0, // steps taken between 00:00 and 23:59
    val upgradePercent: Double? = null, // The percentage the steps have been upgraded.
    val upgradedSteps: Int = 0, // The upgraded steps gotten from today (todaySteps + todaySteps * upgradePercent)
)

// Initiative the ViewModel for the UI.
class MainViewModel: ViewModel() {

    // create ui states
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    fun generateUI() {
        _uiState.update { currentState ->
            currentState.copy(
                totalSteps = 0,
                todaySteps = 0,
                upgradePercent = null,
                upgradedSteps = 0
            )
        }
    }
}
/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 05/10/2025
 * @version v1
 */

package com.SM_BSC.stepfriend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.SM_BSC.stepfriend.ui.models.MainViewModel
import com.SM_BSC.stepfriend.ui.theme.StepFriendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepFriendTheme {
                Scaffold { innerPadding ->
                    InitUX(innerPadding)
                }
            }
        }
    }
}

@Composable
fun InitUX(innerPadding: PaddingValues) {
    val viewModel: MainViewModel = viewModel()

    StepFriendScreen(viewModel)
}

@Composable
fun StepFriendScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
}
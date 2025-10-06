package com.SM_BSC.stepfriend.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.SM_BSC.stepfriend.ui.models.MainViewModel

sealed class Screen (var route: String) {
    object Menu: Screen("menu_screen") // Top Left Nav
    object History: Screen("history_screen") // Top Right Nav
    object Main: Screen("main_screen") // Bottom Left Nav
    object Upgrade: Screen("upgrade_screen") // Bottom Middle Nav
    object Information: Screen("info_screen") // Bottom Right Nav

}

@Composable
fun MenuScreen(innerPadding: PaddingValues) {
    Text(text = "The Menu Screen", Modifier.padding(innerPadding))
}
@Composable
fun MainScreen(innerPadding: PaddingValues, viewModel: MainViewModel) {
    Text(text = "The Main Screen", Modifier.padding(innerPadding))
}

@Composable
fun HistoryScreen(innerPadding: PaddingValues) {
    Text(text = "The History Screen", Modifier.padding(innerPadding))

}

@Composable
fun UpgradeScreen(innerPadding: PaddingValues) {
    Text(text = "The Game Upgrade Screen", Modifier.padding(innerPadding))

}

@Composable
fun InformationScreen(innerPadding: PaddingValues) {
    Text(text = "The Information Screen", Modifier.padding(innerPadding))

}
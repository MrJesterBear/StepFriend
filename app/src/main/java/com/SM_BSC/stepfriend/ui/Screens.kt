package com.SM_BSC.stepfriend.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.SM_BSC.stepfriend.ui.db.StepsEntity

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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(innerPadding: PaddingValues, steps: List<StepsEntity>) {

//    Text(text = "The Main Screen", Modifier.padding(innerPadding))
    Column {
        steps.forEach { step ->
            Column (Modifier.padding(innerPadding)) {
                Text(text = "Total Steps: ${step.totalSteps}")
                Text(text = "Steps Today: ${step.stepsToday}")
                Text(text = "Upgraded Steps: ${step.updatedSteps}")
                Text(text = "Upgrade Percent: ${step.upgradedPercent}")
            }
        }
    }

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
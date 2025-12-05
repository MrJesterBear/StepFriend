package com.SM_BSC.stepfriend.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SM_BSC.stepfriend.ui.db.StepsEntity
import com.SM_BSC.stepfriend.ui.db.UpgradesEntity
import com.SM_BSC.stepfriend.ui.models.StepViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    Column () {
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
fun UpgradeScreen(
    innerPadding: PaddingValues,
    stepsViewModel: StepViewModel,
    upgrades: List<UpgradesEntity>?,
    steps: List<StepsEntity>
) {
    Column(Modifier.padding(innerPadding)) {
        Column(Modifier.fillMaxSize()) {
            upgrades?.forEach { upgrade ->
                Column(Modifier.padding(10.dp)) {
                    Card(modifier = Modifier.size(width = 500.dp, height = 100.dp), onClick = { buyUpgrade(upgrade.upgradeID, upgrade.basePrice, upgrade.quantityOwned, upgrades, steps, stepsViewModel) }) {
                        Text (text = upgrade.upgradeName, fontSize = 16.sp) // Header
                        Text (text = upgrade.upgradeDesc, fontSize = 12.sp) // Description

                        var multiplier: Int

                        if (upgrade.quantityOwned == 0) {
                            multiplier = 1
                        } else {
                            multiplier = upgrade.quantityOwned!!
                        }

                        var total: Double = upgrade.basePrice * multiplier
                        Text (text = "Price: $total Steps", fontSize = 10.sp) // Step Price
                        Text (text = "Total Owned: ${upgrade.quantityOwned}", fontSize = 10.sp)
                    }
                }
            }

            // Current Step Total.
            steps.forEach { step ->
                Text (text = "${step.totalSteps} Steps", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 20.sp,)
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun buyUpgrade(
    ID: Int,
    price: Double,
    quantityOwned: Int?,
    upgrades: List<UpgradesEntity>,
    steps: List<StepsEntity>,
    stepsViewModel: StepViewModel
) {
    println("hehe this has been pressed: $ID" )

    // Check to see if can be afforded.
    var pos: Int = ID - 1
    var multiplier: Int

    // Ensure multiplier is not null.
    if (quantityOwned == 0) {
        multiplier = 1
    } else {
        multiplier = quantityOwned!!
    }

    // total price needed.
    var total: Double = price * multiplier

    var currentSteps = steps[0].totalSteps

    if (currentSteps!! >= total) {

        // Buy that upgrade!
        var newUpgradeTotal = quantityOwned + 1
        var newUpgradePercantage = steps[0].upgradedPercent!! + upgrades[pos].percentModifier
        var newStepTotal = steps[0].totalSteps!! - total


        stepsViewModel.updateUpgradeQuantity(ID, newUpgradeTotal)

        // get current date for the updating.
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.now().format(formatter);

        stepsViewModel.updateStepUpgrade(newUpgradePercantage, currentDate, newStepTotal)

        // Update the lists now that it has done.
        stepsViewModel.updateListDay(currentDate)
        stepsViewModel.updateUpgradesList()


    } else {
        // Let user know they couldn't purchase it.
    }

}

@Composable
fun InformationScreen(innerPadding: PaddingValues) {
    Text(text = "The Information Screen", Modifier.padding(innerPadding))

}
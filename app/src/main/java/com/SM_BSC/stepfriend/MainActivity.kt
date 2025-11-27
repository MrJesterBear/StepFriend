/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 05/10/2025
 * @version v1
 */

package com.SM_BSC.stepfriend

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.SM_BSC.stepfriend.ui.Screen
import com.SM_BSC.stepfriend.ui.theme.StepFriendTheme
import androidx.navigation.compose.composable
import com.SM_BSC.stepfriend.ui.HistoryScreen
import com.SM_BSC.stepfriend.ui.InformationScreen
import com.SM_BSC.stepfriend.ui.MainScreen
import com.SM_BSC.stepfriend.ui.MenuScreen
import com.SM_BSC.stepfriend.ui.UpgradeScreen
import com.SM_BSC.stepfriend.ui.models.StepViewModel
import kotlin.getValue


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val stepsViewModel: StepViewModel by viewModels() // ViewModel instance - wont be used until accessed.
        setContent {
            InitUX(stepsViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InitUX(stepsViewModel: StepViewModel) {



    // Nav Controller for navigation
    val navController = rememberNavController()

    StepFriendTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar(navController) },
            bottomBar = { BottomBar(navController) }
        ) { innerPadding ->
            // Base Default View
            val steps by stepsViewModel.stepsList.observeAsState(emptyList()) // Watch this data, if it updates the front end will update.

            NavHost(navController = navController, startDestination = Screen.Main.route) {
                composable(route = Screen.Main.route) { // Main Screen
                    MainScreen(innerPadding, steps)
                }
                composable(route = Screen.Menu.route) { // Menu Screen
                    MenuScreen(innerPadding)
                }
                composable(route = Screen.History.route) { // History Screen
                    HistoryScreen(innerPadding)
                }
                composable(route = Screen.Upgrade.route) { // Upgrades Screen
                    UpgradeScreen(innerPadding)
                }
                composable(route = Screen.Information.route) { // Information Screen
                    InformationScreen(innerPadding)
                }
        }
    }
}
    }

@OptIn(ExperimentalMaterial3Api::class) // Opt in to the experimental material3api
@Composable
fun TopBar(navController: NavHostController) {
    TopAppBar(title = {
        Row(modifier = Modifier.fillMaxWidth()) { // Row to hold the buttons and space them out.
            TextButton( // Menu Button
                onClick = {
                    navController.navigate(Screen.Menu.route)
                },
            ) {
                Icon(
                    Icons.Default.Menu,
                    "Menu Button" ,
                    Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f)) // Spacer to push the buttons to the edges.

            TextButton( // History Button
                onClick = {
                    navController.navigate(Screen.History.route)
                },
            ) {
                Icon(
                    Icons.Default.DateRange,
                    "History Button",
                    Modifier.size(60.dp)
                )
            }
        }
    })
}

@Composable
fun BottomBar(navController: NavHostController) {
    BottomAppBar {
        TextButton( // Home button
            onClick = {
                navController.navigate(Screen.Main.route)
            },
            Modifier.weight(1f).size(60.dp)

        ) {
            Icon(
                Icons.Default.Home,
                "Home button",
                Modifier.size(60.dp)
            )
        }

        TextButton( // Upgrades Button
            onClick = {
                navController.navigate(Screen.Upgrade.route)
            },
            Modifier.weight(1f).size(60.dp)
        ) {
            Icon(
                Icons.Default.Build,
                "Upgrades Button",
                Modifier.size(60.dp)
            )
        }

        TextButton( // Information Button
            onClick = {
                navController.navigate(Screen.Information.route)
            },
            Modifier.weight(1f).size(60.dp)
        ) {
            Icon(
                Icons.Default.Info,
                "Information button",
                Modifier.size(60.dp)
            )
        }
    }
}


/**
 * Preview for Android Studio IDE.
 */
//@Preview
//@Composable
//fun StepFriendPreview() {
//    InitUX(stepsViewModel)
//}
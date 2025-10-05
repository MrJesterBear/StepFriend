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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.SM_BSC.stepfriend.ui.Screen
import com.SM_BSC.stepfriend.ui.models.MainViewModel
import com.SM_BSC.stepfriend.ui.theme.StepFriendTheme
import androidx.navigation.compose.composable
import com.SM_BSC.stepfriend.ui.MainScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InitUX()
        }
    }
}

@Composable
fun InitUX() {
    // Base Default View
    val viewModel: MainViewModel = viewModel()

    // Nav Controller for navigation
    val navController = rememberNavController()

    StepFriendTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar(navController) },
            bottomBar = { BottomBar(navController) }
        ) { innerPadding ->
            NavHost(navController = navController, startDestination = Screen.Main.route) {
                composable(route = Screen.Main.route) {
                    MainScreen(innerPadding)
                }
        }
    }
}
    }

@OptIn(ExperimentalMaterial3Api::class) // Opt in to the experimental material3api
@Composable
fun TopBar(navController: NavHostController) {
    TopAppBar(title = {
        TextButton(
            onClick = {
                navController.navigate(Screen.Menu.route)
            },
        ) {
            Icon(
                Icons.Default.Menu,
                "Menu Button"
            )
        }
        TextButton(
            onClick = {
                navController.navigate(Screen.Main.route)
            },
        ) {
            Icon(
                Icons.Default.List,
                "Menu Button"
            )
        }
    })
}


@Composable
fun BottomBar(navController: NavHostController) {
    BottomAppBar {
        TextButton(
            onClick = {
                navController.navigate(Screen.Main.route)
            },
            Modifier.weight(1f)

        ) {
            Icon(
                Icons.Default.Home,
                "Home button"
            )
        }

        TextButton(
            onClick = {
                navController.navigate(Screen.Main.route)
            },
            Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.Build,
                "Upgrades Button"
            )
        }

        TextButton(
            onClick = {
                navController.navigate(Screen.Main.route)
            },
            Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.DateRange,
                "History button"
            )
        }
    }
}


/**
 * Preview for Android Studio IDE.
 */
@Preview
@Composable
fun StepFriendPreview() {
    InitUX()
}
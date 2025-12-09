/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 09/12/2025
 * @version v1.3
 */

package com.SM_BSC.stepfriend

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.SM_BSC.stepfriend.ui.Screen
import com.SM_BSC.stepfriend.ui.theme.StepFriendTheme
import androidx.navigation.compose.composable
import com.SM_BSC.stepfriend.steps.Steps
import com.SM_BSC.stepfriend.steps.checkLocationPermissions
import com.SM_BSC.stepfriend.ui.HistoryScreen
import com.SM_BSC.stepfriend.ui.InformationScreen
import com.SM_BSC.stepfriend.ui.MainScreen
import com.SM_BSC.stepfriend.ui.MapScreen
import com.SM_BSC.stepfriend.ui.MenuScreen
import com.SM_BSC.stepfriend.ui.UpgradeScreen
import com.SM_BSC.stepfriend.ui.db.StepsEntity
import com.SM_BSC.stepfriend.ui.models.StepViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.getValue

class MainActivity : ComponentActivity() {
    // init class for step counter.
    val stepCounter = Steps()
    val stepsViewModel: StepViewModel by viewModels() // ViewModel instance - wont be used until accessed.

    // Global variable for Location Client.
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // get current context
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermissions(this)

        setContent {
            // Create the lists for steps.
            val stepList by stepsViewModel.stepsList.observeAsState(emptyList()) // Watch this data, if it updates the front end will update.
            InitAccelerometer(this, stepList)
            InitUX(stepList, this)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() { // Resume the app.
        super.onResume()
        setContent {
            val stepList by stepsViewModel.stepsList.observeAsState(emptyList()) // Watch this data, if it updates the front end will update.
            InitAccelerometer(this, stepList)
            InitUX(stepList, this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * @param activity - The context of the current activity. Passing it through for the main screen.
     * @param stepList - The steplist information for doing step logic.
     */
    @RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InitAccelerometer(activity: MainActivity, stepList: List<StepsEntity>) {

    // get composable context.
    val context = LocalContext.current

    // access sensor
    val sensorManager = remember {
        getSystemService(context, SensorManager::class.java)
    }

    // Remember xyz for updating
    var x by remember { mutableFloatStateOf(0f)}
    var y by remember { mutableFloatStateOf(0f)}
    var z by remember { mutableFloatStateOf(0f)}

    // Declare Steps
    var steps: Int by remember { mutableIntStateOf(0)}

    // Init the sensor
    DisposableEffect(Unit) {
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Init listener
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                x = event.values[0]
                y = event.values[1]
                z = event.values[2]
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Attach listener.
        sensorManager?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        // Removes when screen closes.
        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    /**
     Now that a listener has been created, calculate the actual logic.
     */

    // Set steps to the current steps taken today.
     stepList.forEach { list ->
        steps = list.stepsToday!! // Chance to be null.
    }

    // Calculate the Step.
    stepCounter.calculateStep(x.toDouble(), y.toDouble(), z.toDouble())

    // Check if a new step was taken
    if (stepCounter.stepCount > steps) {

        // Update variable and then update rooms.
        steps = stepCounter.stepCount

        // do Calculations for other variables.
        // Total Steps (Current steps as currency)
        // steps today (Steps base taken today) // This has been gotten already.
        // updatedSteps steps (step upgraded by the upgradeMultiplier)

        var totalSteps: Double? = 1.0
        var updatedSteps: Double? = 1.0
        var upgradeMultiplier: Double? = 1.0

        // Should only be one in list on init.
        stepList.forEach { stats ->
            totalSteps = stats.totalSteps
            updatedSteps = stats.updatedSteps
            upgradeMultiplier = stats.upgradedPercent

        }

        // calculate new total steps.
        val stepsDoneToday = steps
        val upgradedStep: Double = 1 * upgradeMultiplier!! // 1 as in 1 step. this area can only be gotten to if a step has been taken.
        val newTotalUpgraded: Double

        if (upgradeMultiplier > 1) {
            // Actually add the upgraded step to the draw. otherwise, nah, cause it's not been upgraded.
             newTotalUpgraded = updatedSteps!! + upgradedStep
        } else {
             newTotalUpgraded = updatedSteps!!
        }

        val newTotalOverall: Double = totalSteps!! + upgradedStep

        // With that, update the row and list.
        stepsViewModel.updateCurrentStepRecord(newTotalOverall, stepsDoneToday, newTotalUpgraded)
    }

    // Get Todays Stats for displaying.
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val currentDate = LocalDate.now().format(formatter)

    stepsViewModel.updateListDay(currentDate)
}

    /***
     * @param steps - Pass in the steps list to pass in to the main screen if needed.
     * @param activity - Passing in the context of the current activity
     * Initialises the front design for the app.
     */
    @RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InitUX(steps: List<StepsEntity>, activity: MainActivity) {

    // Create the upgrades list.
    val upgrades by stepsViewModel.upgradesList.observeAsState(emptyList())
    stepsViewModel.updateUpgradesList()

    // Create walk list
    val walkList by stepsViewModel.walkList.observeAsState(emptyList())
    stepsViewModel.updateWalks()

    // Nav Controller for navigation
    val navController = rememberNavController()

    StepFriendTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar(navController) },
            bottomBar = { BottomBar(navController) }
        ) { innerPadding ->
            // Base Default View
            NavHost(navController = navController, startDestination = Screen.Main.route) {
                composable(route = Screen.Main.route) { // Main Screen
                    MainScreen(innerPadding, steps, stepsViewModel, activity, fusedLocationClient, walkList)
                }
                composable(route = Screen.Menu.route) { // Menu Screen
                    MenuScreen(innerPadding)
                }
                composable(route = Screen.History.route) { // History Screen
                    HistoryScreen(innerPadding, stepsViewModel, navController)
                }
                composable(route = Screen.Map.route) {
                    MapScreen(innerPadding, stepsViewModel, navController)
                }
                composable(route = Screen.Upgrade.route) { // Upgrades Screen
                    UpgradeScreen(innerPadding, stepsViewModel, upgrades, steps)
                }
                composable(route = Screen.Information.route) { // Information Screen
                    InformationScreen(innerPadding)
                }
        }
    }
}
    }

    /**
     * @param navController - The nav controller for navigation.
     * Design for the top navigation bar which includes the Menu and History buttons.
     */
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
                Icon( // Material icon, choosing a default icon from material and giving it a description for screen readers.
                    Icons.Default.DateRange,
                    "History Button",
                    Modifier.size(60.dp)
                )
            }
        }
    })
}

    /**
     * @param navController - The nav controller for navigation.
     * Design for the bottom navigation bar which includes the Home, Upgrades and Information buttons.
     */
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

}
/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 06/12/2025
 * @version v2.1
 */

package com.SM_BSC.stepfriend.ui

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.SM_BSC.stepfriend.MainActivity
import com.SM_BSC.stepfriend.steps.checkLocationType
import com.SM_BSC.stepfriend.ui.db.StepsEntity
import com.SM_BSC.stepfriend.ui.db.UpgradesEntity
import com.SM_BSC.stepfriend.ui.db.WalkEntity
import com.SM_BSC.stepfriend.ui.models.StepViewModel
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

// Global Variables for usage.
var lat: Double = 0.0
var lng: Double = 0.0

var mapID: Int = 0
var walkID: Int = 0

sealed class Screen (var route: String) {
    object Menu: Screen("menu_screen") // Top Left Nav
    object History: Screen("history_screen") // Top Right Nav
    object Map: Screen("map_screen") // History Screen
    object Main: Screen("main_screen") // Bottom Left Nav
    object Upgrade: Screen("upgrade_screen") // Bottom Middle Nav
    object Information: Screen("info_screen") // Bottom Right Nav

}

@Composable
fun MenuScreen(innerPadding: PaddingValues) {
    Text(text = "The Menu Screen", Modifier.padding(innerPadding))
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    innerPadding: PaddingValues,
    steps: List<StepsEntity>,
    stepsViewModel: StepViewModel,
    activity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    walkList: List<WalkEntity>?,
) {
// Example of Snackbar taken from official docs with comments for understanding (https://developer.android.com/develop/ui/compose/components/snackbar)
    val scope = rememberCoroutineScope() // Get routine info.
    val snackbarHostState = remember { SnackbarHostState() } // Remember the state of the snackbar.
    var buttonChoice: Boolean =  false

    var timer: Timer = Timer("GeolocationTask", false)
    var timerCanceled: Boolean = false


    // Create viewmodel for walks
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = { // Create the snackbar label using a button.
            ExtendedFloatingActionButton(
                text = { Text("Show snackbar") },
                icon = { Icon(Icons.Filled.Clear, contentDescription = "") },
                onClick = {// What happens when a snackbar is launched
                    scope.launch {
                        snackbarHostState.showSnackbar("Snackbar")
                    }
                }
            )
        }
    ) {
        Column() {
            steps.forEach { step ->
                Column(Modifier.padding(innerPadding)) {
                    Text(text = "Total Steps: ${step.totalSteps}")
                    Text(text = "Steps Today: ${step.stepsToday}")
                    Text(text = "Upgraded Steps: ${step.updatedSteps}")
                    Text(text = "Upgrade Percent: ${step.upgradedPercent}")
                }
            }

            Card(modifier = Modifier.size(width = 500.dp, height = 100.dp), onClick = {
                if (!buttonChoice) {
                    // Check here to ensure that location has been enabled.

                    // Button has not been pressed yet, start the walk logic
                    buttonChoice = true
                    print("Button should now be true as starting timer: $buttonChoice")


                    // Update Button Text.
//                    walkText = mutableStateOf("Stop your Walk")
//                    walkSubText = mutableStateOf("Stopping will create a new record for your history.")
                    if (timerCanceled) {
                        timer = Timer("GeolocationTask", false)
                        timerCanceled = true
                    }

                    println("old Lat $lat / Lng $lng")
                    // Create a walk by running the logic once.
                    walkLogic(fusedLocationClient, activity)
                    println("New Lat $lat / Lng $lng")

                    // Populate with the new variables.
                    stepsViewModel.insertWalk(walkID,lat, lng)

                    // Get the most recent ID
                    stepsViewModel.updateWalks()
                    println("New WalkID ${walkList!![0].walkID}")

                    // Start the timer task. https://stackoverflow.com/questions/43348623/how-to-call-a-function-after-delay-in-kotlin
                    timer.scheduleAtFixedRate(500, 5000) { // After 30 second increments, run this method.
                        println("old Lat $lat / Lng $lng")
                        walkLogic(fusedLocationClient, activity)
                        println("New Lat $lat / Lng $lng")
//                            stepsViewModel.insertWaypoint(walkList!![0].walkID,lat, lng)
                    }

//
                    scope.launch {
                        snackbarHostState.showSnackbar("Walk Started")
                    }
                } else {
                    // Button has been pressed, so clean up.
                    buttonChoice = false

                    println("Button should now be false as cancelling timer: $buttonChoice")

                    // Update Text
//                    walkText = mutableStateOf("Start your Walk")
//                    walkSubText = mutableStateOf("(Requires Location Permissions)")

                    // Stop Timer and do a final geolocation call.
                    timer.cancel()
                    timerCanceled = true

                    // Get the final details.
                    walkLogic(fusedLocationClient, activity)
                    stepsViewModel.updateWalks()

                    stepsViewModel.finishWalk(walkList!![0].walkID, lat, lng)

                    scope.launch {
                        snackbarHostState.showSnackbar("Walk Finished.")
                    }
                }
            }) {

                Text(text = "Start Walk", fontSize = 26.sp)
                Text(text = "(Requires Location Permissions)", fontSize = 16.sp)
            }

            }
        }
    }

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun walkLogic(fusedLocationClient: FusedLocationProviderClient, activity: MainActivity) {

    val priority = checkLocationType(activity)
    fusedLocationClient.getCurrentLocation(
        priority,
        CancellationTokenSource().token
    )
        .addOnSuccessListener { location: Location? ->
            if (location == null)
                Toast.makeText(activity, "Cannot get location.", Toast.LENGTH_SHORT).show()
            else {
                val lat = location.latitude
                val lng = location.longitude
//                println("Lat: $lat , Long: $lng")
                setLatAndLng(lat, lng)
            }

        }

}

fun setLatAndLng(newLat: Double, newLng: Double) {
    lat = newLat
    lng = newLng
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    innerPadding: PaddingValues,
    stepsViewModel: StepViewModel,
    navController: NavHostController
) {
//    Text(text = "The History Screen", Modifier.padding(innerPadding))

//    Create Cards for the last 4 walks
    val walks by stepsViewModel.walkList.observeAsState()
    stepsViewModel.updateWalks()

    Column(Modifier.padding(innerPadding)) {
        Column(Modifier.fillMaxSize()) {
            Text(text = "History", Modifier.padding(7.dp), fontSize = 26.sp)
            walks?.let {
                if (it.isEmpty()) {
                    // If empty, display a card with some help text.
                    Card(modifier = Modifier.size(width = 500.dp, height = 100.dp)) {
                        Text(text = "You have no walking History", fontSize = 16.sp)
                        Text(text = "If you would like to start a walk, click the Start Walk button on the main game screen.", fontSize = 12.sp)
                    }
                } else {
                    walks?.forEach { walk ->
                        Card(modifier = Modifier.size(width = 500.dp, height = 100.dp), onClick = {
                              mapID = walk.walkID
                            
                            // PUt the walk ID to the main activity.
                            
                            // Route to the map screen.
                            navController.navigate(Screen.Map.route)
                        }) {
                            Text(text = "Your walk on: ${walk.date} - (Walk ${walk.walkID})", fontSize = 16.sp)
                            Text(text = "Start Coords: ${walk.startLat},${walk.startLng}")
                            Text(text = "End Coords: ${walk.endLat},${walk.endLng}")
                        }

                    }
                }
            }

        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(
    innerPadding: PaddingValues,
    stepsViewModel: StepViewModel,
    navController: NavHostController
) {

    // Adapted from: https://medium.com/@ridvanozcan48/how-to-use-google-maps-in-jetpack-compose-step-by-step-android-guide-55aedac89e43
    // Comments for Understanding.

    // Get the requires lists (walks and Waypoints.

    val walkList by stepsViewModel.mapWalkList.observeAsState()
    val waypointList by stepsViewModel.mapWaypointList.observeAsState()

    // Update each list based on the walkID that is saved by clicking the card in the history.
    stepsViewModel.getWaypointDetails(mapID)
    stepsViewModel.getWalkDetails(mapID)

    // Store important walk information
    var startingCoords: LatLng = LatLng(0.0, 0.0)
    var endingCoords: LatLng = LatLng(0.0, 0.0)

    walkList?.forEach { walk ->
        startingCoords = LatLng(walk.startLat, walk.startLng)
        endingCoords = LatLng(walk.endLat!!, walk.endLng!!)
    }

    println("MapID : $mapID")
    println("StartingCoords: $startingCoords")

    val targetPos = startingCoords // Target Position will be the start location.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetPos, 15f)
    }

    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }



//     Create a list of waypoints for creating a route.
    var routeCoordinates: List<LatLng> = listOf(
        startingCoords, // Starting Position
        waypointList?.forEach { waypoint ->
            LatLng(waypoint.waypointLat, waypoint.waypointLng)
        },// Waypoints in here.
        endingCoords // Ending Position
    ) as List<LatLng>

    var newCoordinates: List<LatLng> = routeCoordinates

    Column(Modifier.padding(innerPadding)) {

        Button(onClick = { // Brings back to history menu.
            navController.navigate(Screen.History.route)
        }) {
            Text(text = "Go Back")
        }

        GoogleMap( // Call the google map composable to display the map with the information needed.
            cameraPositionState = cameraPositionState,
            properties = properties
        ) {
            Polyline( // Polyline designs the line with each point, useful for tracking directional changes.
                points = newCoordinates,
                color = Color.Red,
                width = 5f
            )
            Marker( // Displays a marker at a position, one for starting position and another for the ending position.
                state = MarkerState(position = startingCoords),
                title = "Start Position"
            )
            Marker(
                state = MarkerState(position = endingCoords),
                title = "End Position"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpgradeScreen(
    innerPadding: PaddingValues,
    stepsViewModel: StepViewModel,
    upgrades: List<UpgradesEntity>?,
    steps: List<StepsEntity>
) {
// Example of Snackbar taken from official docs with comments for understanding (https://developer.android.com/develop/ui/compose/components/snackbar)

    val scope = rememberCoroutineScope() // Get routine info.
    val snackbarHostState = remember { SnackbarHostState() } // Remember the state of the snackbar.

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = { // Create the snackbar label using a button.
            ExtendedFloatingActionButton(
                text = { Text("Show snackbar") },
                icon = { Icon(Icons.Filled.Clear, contentDescription = "") },
                onClick = {// What happens when a snackbar is launched
                    scope.launch {
                        snackbarHostState.showSnackbar("Snackbar")
                    }
                }
            )
        }
    ) {
        // Screen content
    Column(Modifier.padding(innerPadding)) {
        Text(text = "Upgrades", Modifier.padding(7.dp), fontSize = 26.sp)

        Column(Modifier.fillMaxSize()) {
            upgrades?.forEach { upgrade ->
                Column(Modifier.padding(10.dp)) {
                    Card(modifier = Modifier.size(width = 500.dp, height = 100.dp), onClick = {
                        scope.launch { // run this function when the card is clicked.
                            buyUpgrade(upgrade.upgradeID, upgrade.basePrice, upgrade.quantityOwned, upgrades, steps, stepsViewModel, snackbarHostState)
                        }
                    }
                    ) {
                        Text (text = upgrade.upgradeName, fontSize = 16.sp) // Header
                        Text (text = upgrade.upgradeDesc, fontSize = 12.sp) // Description

                        var multiplier: Int

                        if (upgrade.quantityOwned == 0) {
                            multiplier = 1
                        } else {
                            multiplier = upgrade.quantityOwned!! + 1
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
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun buyUpgrade(
    ID: Int,
    price: Double,
    quantityOwned: Int?,
    upgrades: List<UpgradesEntity>,
    steps: List<StepsEntity>,
    stepsViewModel: StepViewModel,
    snackbarHostState: SnackbarHostState
) {
//    println("hehe this has been pressed: $ID" )

    // Check to see if can be afforded.
    var pos: Int = ID - 1
    var multiplier: Int

    // Ensure multiplier is not null.
    if (quantityOwned == 0) {
        multiplier = 1
    } else {
        multiplier = quantityOwned!! + 1
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

        // Show a snackbar message.
        var upgradeName = upgrades[0].upgradeName
        snackbarHostState.showSnackbar("You bought a $upgradeName.")



    } else { // Whoops, sorry pal, nae upgrade for you.
        // Call the snackbar button with a message.
        snackbarHostState.showSnackbar("You do not have enough Steps.")


    }

}

@Composable
fun InformationScreen(innerPadding: PaddingValues) {
    Text(text = "The Information Screen", Modifier.padding(innerPadding))

}
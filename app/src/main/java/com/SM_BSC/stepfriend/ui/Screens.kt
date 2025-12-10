/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 09/12/2025
 * @version v2.2
 */

package com.SM_BSC.stepfriend.ui

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.SM_BSC.stepfriend.R
import com.SM_BSC.stepfriend.steps.checkLocationPermissions

// Global Variables for usage.
var mapID: Int = 0

/**
 * @param route - Determines which route to take.
 * Tells the nav host what screen is which and how to route.
 */
sealed class Screen (var route: String) {
    object Menu: Screen("menu_screen") // Top Left Nav
    object History: Screen("history_screen") // Top Right Nav
    object Map: Screen("map_screen") // History Screen
    object Main: Screen("main_screen") // Bottom Left Nav
    object Upgrade: Screen("upgrade_screen") // Bottom Middle Nav
    object Information: Screen("info_screen") // Bottom Right Nav

}

/**
 * @param innerPadding - The padding taken from the Scaffold.
 * A menu screen for additional functionality
 * TODO(Add Buttons for things like Clearing Data/Restarting, etc.)
 */
@Composable
fun MenuScreen(innerPadding: PaddingValues) {
    Text(text = "The Menu Screen", Modifier.padding(innerPadding))
}

/**
 * @param innerPadding - The padding taken from the Scaffold.
 * @param steps - the stepList for the database information
 * @param stepsViewModel - the view model for access database methods for the walk functionality
 * @param activity - the context of the activity.
 * @param fusedLocationClient - the location functionality for gathering coordinates for walk functionality.
 * @param walkList - the list for database information for walks.
 * The main screen that handles showcasing changes in step information and granting the ability to start/stop walks.
 */
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
    val scope = rememberCoroutineScope() // Get context info, similar to activity.
    val snackbarHostState = remember { SnackbarHostState() } // Remember the state of the snackbar.
    var buttonChoice: Boolean =  remember {false}

    var timer: Timer = Timer("GeolocationTask", false)
    var timerCanceled: Boolean = remember {false}

    var walkID: Int by remember { mutableIntStateOf(0) }

    var buttonMainText by remember {mutableStateOf("Start Walk")}
    var buttonSubText by remember { mutableStateOf("(Requires location permissions)")}

    // Example of Snackbar taken from official docs with comments for understanding (https://developer.android.com/develop/ui/compose/components/snackbar)
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
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { // Step information.

            steps.forEach { step ->
                // Image for some flare.
                Column(Modifier.padding(innerPadding)) {
                    Image(
                        painter = painterResource(id = R.drawable.step),
                        contentDescription = "A set of footprints.",
                        modifier = Modifier.height(128.dp).width(128.dp),
                        alignment = Alignment.Center,
                    )

                    Spacer(Modifier.padding(16.dp))

                    // Information for the steps.
                    Text(text = "Total Steps: ${step.totalSteps}")
                    Text(text = "Steps Today: ${step.stepsToday}")
                    Text(text = "Upgraded Steps: ${step.updatedSteps}")
                    Text(text = "Upgrade Percent: ${step.upgradedPercent}")
                }
            }

            Card(modifier = Modifier.size(width = 500.dp, height = 100.dp), onClick = { // The Start/Stop walk button
                if (!buttonChoice) {
                    // Check here to ensure that location has been enabled.
                    if (checkLocationPermissions(activity)) { // If location services enabled, continue


                        // Button has not been pressed yet, start the walk logic
                        buttonChoice = true
//                        println("Button should now be true as starting timer: $buttonChoice")

                        // Update Button Text.
                        buttonMainText = "Stop your Walk"
                        buttonSubText = "Stopping will create a new record for your history."

                        if (timerCanceled) {
                            timer = Timer("GeolocationTask", false)
                            timerCanceled = false
                        }

                        // Create a walk by running the logic once.
                        walkLogic(fusedLocationClient, activity, walkID, "Insert", stepsViewModel)

                        // Get the most recent ID
                        stepsViewModel.updateWalks()
                        walkID = walkList!![0].walkID
//                    walkID++
                        println("New WalkID $walkID")

                        // Start the timer task. https://stackoverflow.com/questions/43348623/how-to-call-a-function-after-delay-in-kotlin
                        timer.scheduleAtFixedRate(
                            500,
                            5000
                        ) { // After 30 second increments, run this method.
                            walkLogic(
                                fusedLocationClient,
                                activity,
                                walkID,
                                "Waypoint",
                                stepsViewModel
                            )
                            println("Logic ran in timer")
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar("Walk Started")
                        }
                    } else { // if location services are not enabled, worry.
                        scope.launch {
                            snackbarHostState.showSnackbar("Enable location services to use this feature.")
                        }
                    }

                } else {
                    // While this point should never be gotten to, check for location permissions just in case.
                    if (checkLocationPermissions(activity)) {


                        // Button has been pressed, so clean up.
                        buttonChoice = false

                        println("Button should now be false as cancelling timer: $buttonChoice")

                        // Update Text
                        buttonMainText = "Start your Walk"
                        buttonSubText = "(Requires Location Permissions)"

                        // Stop Timer and do a final geolocation call.
                        timer.cancel()
                        timerCanceled = true

                        stepsViewModel.updateWalks()
                        walkID = walkList!![0].walkID

                        println("Walk is now finished on ID $walkID")

                        // Get the final details.
                        walkLogic(fusedLocationClient, activity, walkID, "Finish", stepsViewModel)

                        scope.launch {
                            snackbarHostState.showSnackbar("Walk Finished.")
                        }
                    }else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Enable location services to use this feature.")
                        }
                    }
                }
            }) {
                // Card Text
                Text(text = "$buttonMainText", fontSize = 26.sp)
                Text(text = "$buttonSubText", fontSize = 16.sp)
            }

            }
        }
    }

/**
 * @param fusedLocationClient - Used for getting GPS coordinates.
 * @param activity - The context of the main application.
 * @param walkID - The walk ID for the current walk, to be used for queries to the DB.
 * @param type - The type of transaction to occur when updating the database (passed through.)
 * @param stepsViewModel - Access to the database methods.
 */
@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun walkLogic(
    fusedLocationClient: FusedLocationProviderClient,
    activity: MainActivity,
    walkID: Int,
    type: String,
    stepsViewModel: StepViewModel
) {

    // Get the current coordinates using a priority determined by the agreed permissions.
    val priority = checkLocationType(activity)
    fusedLocationClient.getCurrentLocation(
        priority,
        CancellationTokenSource().token
    )
        .addOnSuccessListener { location: Location? -> // On a successful execution, check to see if there was a delivered location.
            if (location == null)
                println("Location could not be gotten.")
            else {
                val lat = location.latitude
                val lng = location.longitude
                println("Lat: $lat , Long: $lng")

                setLatAndLng(lat, lng, walkID, type, stepsViewModel)
            }

        }

}

/**
 * @param newLat - the new Latitude to post to DB.
 * @param newLng - the new longitude to post to DB
 * @param walkID - the walkID used to find the correct entry to update
 * @param type - the type of DB interaction to execute.
 * @param stepsViewModel - The view model to access DB functions.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun setLatAndLng(
    newLat: Double,
    newLng: Double,
    walkID: Int,
    type: String,
    stepsViewModel: StepViewModel
) {

    // Determine what to do.
   if (type == "Insert") {
       println("Insert chosen.")
       stepsViewModel.insertWalk(newLat, newLng)
   } else if (type == "Waypoint") {
       println("Waypoint Chosen")
        stepsViewModel.insertWaypoint(walkID, newLat, newLng)
   } else {
       // Assume it is finished.
       println("No match, Finish Walk.")
       stepsViewModel.finishWalk(walkID, newLat, newLng)
   }

}

/**
 * @param innerPadding - padding based on ui scaffold.
 * @param stepsViewModel - View model to access the database for walk history.
 * @param navController - The nav controller to allow for routing to a new screen.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    innerPadding: PaddingValues,
    stepsViewModel: StepViewModel,
    navController: NavHostController
) {
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
                            // Store the mapID as a global variable to use it on the map screen.
                            mapID = walk.walkID

                            
                            // Route to the map screen.
                            navController.navigate(Screen.Map.route)
                        }) {
                            Text(text = "Your walk on: ${walk.date} - (Walk ${walk.walkID})", fontSize = 16.sp)
                            Text(text = "Start Coords: ${walk.startLat},${walk.startLng}")
                            Text(text = "End Coords: ${walk.endLat},${walk.endLng}")
                        }
                        // Space the cards out.
                        Spacer(Modifier.padding(10.dp))
                    }
                }
            }
        }
    }
}

/**
 * @param innerPadding - Modifier details from Scaffold
 * @param stepsViewModel - viewmodel for accessing the DB
 * @param navController - Nav controller for returning to the previous page.
 */
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

    val targetPos = startingCoords // Target Position will be the start location.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetPos, 15f)
    }

    // Properties for the map and how it should render.
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    //     Create a list of waypoints, starting with
    var routeCoordinates: List<LatLng> = listOf(
        startingCoords
    )

    // Initalise a blank list.
    var waypointRoute: List<LatLng> = listOf()

    // Go through the waypoints and concatenate them with the previous list
    // (Lists are read only.
    waypointList?.forEach { waypoint ->
         waypointRoute = waypointRoute + LatLng(waypoint.waypointLat, waypoint.waypointLng)
    }

    // .plus is the same as +. add the ending coords.
    waypointRoute = waypointRoute.plus(endingCoords)

    // Finally add it all together.
    var newCoordinates: List<LatLng> = routeCoordinates.plus(waypointRoute)

    newCoordinates.forEach { waypoint ->
        println("${waypoint.latitude},${waypoint.longitude}")
    }

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

/**
 * @param innerPadding - Scaffolding Modifiers
 * @param stepsViewModel - ViewModel to access the database
 * @param upgrades - List to view and display the upgrades
 * @param steps - List to check if enough steps to buy upgrade.
 */
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
                // Display Upgrade
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

/**
 * @param ID - the ID of the upgrade being purchased.
 * @param price - the price of the upgrade being purchased
 * @param quantityOwned - the amount of the ugprade the user has.
 * @param upgrades - The list to get information about the upgrades from the DB.
 * @param steps - the list to get information about the step count from the DB.
 * @param stepsViewModel - the viewmodel to interact with the database.
 * @param snackbarHostState - the snackbar class object to inform user.
 */
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

/**
 * @param innerPadding - Modifier values from Scaffold.
 */
@Composable
fun InformationScreen(innerPadding: PaddingValues) {
    Text(text = "The Information Screen", Modifier.padding(innerPadding))

}
/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 06/12/2025
 * @version v1
 */

package com.SM_BSC.stepfriend.steps

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.SM_BSC.stepfriend.MainActivity
import com.google.android.gms.location.Priority

// Adapted from https://sachankapil.medium.com/latest-method-how-to-get-current-location-latitude-and-longitude-in-android-give-support-for-c5132474c864
// Comments for understanding.

fun checkLocationPermissions(activity: MainActivity): Boolean {
//    val context = LocalContext.current

    return if (ActivityCompat.checkSelfPermission(activity, // Checks permission to see if it isnt granted
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, // Create a dialog to ask for the permissions.
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1 // https://stackoverflow.com/questions/44309857/request-code-for-permissions-in-android - Seems like it's used for more interanl tracking.
        )
        false // Returns false if the permission is not granted.
    } else {
        true // Otherwise, it's true!
    }
}

fun checkLocationType(activity: MainActivity): Int {
    if ((ActivityCompat.checkSelfPermission(activity, // Checks permission to see if it isnt granted
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )) {
        // Assume fine location is good.
        return Priority.PRIORITY_HIGH_ACCURACY
    } else {
        // Return the coarse priority.
        return Priority.PRIORITY_BALANCED_POWER_ACCURACY
    }
}

fun initLocationService() {

}
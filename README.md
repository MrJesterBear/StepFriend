# Step Friend!
## A step tracker with a gamey twist! 

This application is inspired by [Cookie Clicker](https://orteil.dashnet.org/cookieclicker/)

Part of an Assessment for University. (BSc Computing - Software Development)

# Minimum SDK Version - 24

## Features
- Automatic Tracking of steps when application is opened.
- Ability to purchase upgrades to make your step more powerful

## API Usage
### Basic
- Jetpack Compose
- Material 3
- ViewModels
- SensorManager
- Navigation

### Connectivity
- RoomsDB

## Known Bugs / Issues
- When the app is closed and re-opened, it takes a while for the step logic to begin working again, but it does after a bit.
> Potential fix for this is to run that piece of code in the background using withContext() or Coroutine.Launch(), but as it uses composable elements a rewrite may be needed.
- ~~Upgrading from no upgrades to 1 makes the price the same as before.~~
> ~~This is due to how the mathematics is done. a 0 makes sure it's a one just to not have a 0 price. this can be fixed by just adding one to the multiplier.~~
- ~~Starting / Ending a walk does not properly input data into Rooms, GPS inputs 0.0 when first started.~~
> ~~This One I am somewhat lost on. I believe I need to learn coroutines properly in order to do better wait cycles. and figure out a better design for getting the ID of a new walk or old walk.~~
- Waypoint data currently is not calculated in the polyline equation.
> Happens due to a mismatch of lists from the viewmodel to the LatLng list. Need to figure out a way to convert this.
- Composable refreshes when a step is calculated, damaging the walk-tracking process causing issues with stopping / starting a walk. Causes an indefinite loop.
> This could possibly be fixed with a background task that retains this information upon refresh of the composable.
- Map Screen loads too quickly / Doesn't refresh properly, causing it to not zoom in on the correct area.
> Self Explanatory, likely needs to pause and wait for data or refresh properly as the points for the polyline do still show up.

# Last Update: 7th December 2025 - 00:24
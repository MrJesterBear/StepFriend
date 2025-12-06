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

## Known Bugs
- When the app is closed and re-opened, it takes a while for the step logic to begin working again, but it does after a bit.
> Potential fix for this is to run that piece of code in the background using withContext() or Coroutine.Launch(), but as it uses composable elements a rewrite may be needed.
- ~~Upgrading from no upgrades to 1 makes the price the same as before.~~
> ~~This is due to how the mathematics is done. a 0 makes sure it's a one just to not have a 0 price. this can be fixed by just adding one to the multiplier.~~

# Last Update: 6th December 2025 - 00:37
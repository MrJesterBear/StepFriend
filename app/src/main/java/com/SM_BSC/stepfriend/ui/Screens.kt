package com.SM_BSC.stepfriend.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

sealed class Screen (var route: String) {
    object Main: Screen("main_screen")
    object Menu: Screen("menu_screen")

}

@Composable
fun MainScreen(innerPadding: PaddingValues) {
    Text(text = "Welcome to the App!", Modifier.padding(innerPadding))
}

@Composable
fun MenuScreen(innerPadding: PaddingValues) {

}
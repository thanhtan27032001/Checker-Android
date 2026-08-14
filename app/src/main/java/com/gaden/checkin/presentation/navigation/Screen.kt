package com.gaden.checkin.presentation.navigation

sealed class Screen(
    val route: String,
) {
    data object CheckIn: Screen("checkin")
    data object History: Screen("history")
}
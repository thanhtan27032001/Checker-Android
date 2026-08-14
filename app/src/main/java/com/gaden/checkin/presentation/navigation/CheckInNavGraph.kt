package com.gaden.checkin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gaden.checkin.presentation.checkin.CheckInScreen
import com.gaden.checkin.presentation.history.HistoryScreen

@Composable
fun CheckInNavGraph(
    navHostController: NavHostController = rememberNavController()
) {
    return NavHost(
        navController = navHostController,
        startDestination = Screen.CheckIn.route,
    ) {
        composable(Screen.CheckIn.route) {
            CheckInScreen(
                onHistoryClicked = { navHostController.navigate(Screen.History.route) }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onBackClicked = { navHostController.popBackStack() }
            )
        }
    }
}
package com.gaden.checkin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gaden.checkin.presentation.auth.LoginScreen
import com.gaden.checkin.presentation.checkin.CheckInScreen
import com.gaden.checkin.presentation.dashboard.DashboardScreen
import com.gaden.checkin.presentation.history.HistoryScreen
import com.gaden.checkin.presentation.leave.LeaveScreen
import com.gaden.checkin.presentation.splash.SplashScreen

@Composable
fun CheckInNavGraph(
    checkInNavGraphViewModel: CheckInNavGraphViewModel = hiltViewModel(),
    navHostController: NavHostController = rememberNavController()
) {
    LaunchedEffect(Unit) {
        checkInNavGraphViewModel.sessionExpiredEvent.collect {
            navHostController.navigate(Screen.Login.route) {
                popUpTo(Screen.CheckIn.route) { inclusive = true }
            }
        }
    }
    return NavHost(
        navController = navHostController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navHostController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToCheckIn = {
                    navHostController.navigate(Screen.CheckIn.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navHostController.navigate(Screen.CheckIn.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                } }
            )
        }
        composable(Screen.CheckIn.route) {
            CheckInScreen(
                onHistoryClicked = { navHostController.navigate(Screen.History.route) },
                onLeaveClicked = { navHostController.navigate(Screen.Leave.route) },
                onDashboardClick = { navHostController.navigate(Screen.Dashboard.route) }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onBackClicked = { navHostController.popBackStack() }
            )
        }
        composable(Screen.Leave.route) {
            LeaveScreen(
                onBackClicked = { navHostController.popBackStack() },
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onBackClick = { navHostController.popBackStack() },
            )
        }
    }
}
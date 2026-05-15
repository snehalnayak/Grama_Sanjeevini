package com.example.grama_sanjeevini.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grama_sanjeevini.ui.screens.HomeScreen
import com.example.grama_sanjeevini.ui.screens.LoginScreen
import com.example.grama_sanjeevini.ui.screens.PharmacistScreen
import com.example.grama_sanjeevini.ui.screens.SearchResultScreen
import com.example.grama_sanjeevini.ui.screens.SplashScreen

import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onSearchClick = { query, radius ->
                    navController.navigate(Screen.SearchResult.createRoute(query, radius))
                },
                onPharmacistClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = { pharmacistId ->
                navController.navigate(Screen.Pharmacist.createRoute(pharmacistId)) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(
            route = Screen.SearchResult.route,
            arguments = listOf(
                navArgument("query") { type = NavType.StringType },
                navArgument("radius") { 
                    type = NavType.IntType
                    defaultValue = -1 // Using -1 to represent "All"
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            val radius = backStackEntry.arguments?.getInt("radius").takeIf { it != -1 }
            SearchResultScreen(query = query, radius = radius)
        }
        composable(
            route = Screen.Pharmacist.route,
            arguments = listOf(navArgument("pharmacistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pharmacistId = backStackEntry.arguments?.getString("pharmacistId") ?: ""
            PharmacistScreen(pharmacistId = pharmacistId)
        }
    }
}

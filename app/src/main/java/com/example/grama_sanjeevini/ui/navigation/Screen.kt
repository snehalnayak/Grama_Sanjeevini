package com.example.grama_sanjeevini.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Login : Screen("login")
    object SearchResult : Screen("search_result/{query}?radius={radius}") {
        fun createRoute(query: String, radius: Int? = null) = 
            "search_result/$query" + if (radius != null) "?radius=$radius" else ""
    }
    object Pharmacist : Screen("pharmacist/{pharmacistId}") {
        fun createRoute(pharmacistId: String) = "pharmacist/$pharmacistId"
    }
}

package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Visits : Screen("visits", "Visits", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    object Orders : Screen("orders", "Orders", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Academy : Screen("academy", "Academy", Icons.Filled.School, Icons.Outlined.School)
    object More : Screen("more", "More", Icons.Filled.Menu, Icons.Outlined.Menu)
}

object Destinations {
    const val LOGIN = "login"
    const val HOME = "home"
    const val VISITS = "visits"
    const val ORDERS = "orders"
    const val ACADEMY = "academy"
    const val MORE = "more"

    const val DOCTOR_LIST = "doctor_list"
    const val DOCTOR_DETAIL = "doctor_detail/{doctorId}"
    const val ADD_DOCTOR = "add_doctor"
    const val START_VISIT = "start_visit/{doctorId}"
    const val VISIT_REPORT = "visit_report/{doctorId}"
    const val VISIT_HISTORY = "visit_history"

    const val PRODUCT_LIST = "product_list"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val PRODUCT_DOSSIER = "product_dossier/{productId}"
    const val MOA_VISUALIZER = "moa_visualizer/{productId}"
    const val COMPETITOR_BATTLE = "competitor_battle/{productId}"
    const val ASSESSMENT = "assessment/{productId}"

    const val STOCKIST_LIST = "stockist_list"
    const val STOCKIST_DETAIL = "stockist_detail/{stockistId}"
    const val RETAILER_LIST = "retailer_list"
    const val RETAILER_DETAIL = "retailer_detail/{retailerId}"

    const val CREATE_ORDER = "create_order"
    const val ORDER_SUBMISSION = "order_submission/{orderId}"
    const val ORDER_DETAIL = "order_detail/{orderId}"
    const val HQ_DASHBOARD = "hq_dashboard"
    const val SUPER_ADMIN_DASHBOARD = "super_admin_dashboard"

    const val EXPENSES = "expenses"
    const val ADD_EXPENSE = "add_expense"

    const val ATTENDANCE = "attendance"
    const val ROUTE_PLAN = "route_plan"
    const val FOLLOW_UPS = "follow_ups"

    const val PERFORMANCE = "performance"
    const val INCENTIVES = "incentives"
    const val GAMIFICATION = "gamification"
    const val LEADERBOARD = "leaderboard"
    const val NOTIFICATIONS = "notifications"

    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val HELP = "help"
}

package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.academy.*
import com.example.ui.admin.*
import com.example.ui.auth.LoginScreen
import com.example.ui.commercial.*
import com.example.ui.dashboard.HomeDashboardScreen
import com.example.ui.doctors.*
import com.example.ui.navigation.Destinations
import com.example.ui.navigation.Screen
import com.example.ui.performance.*
import com.example.ui.profile.*
import com.example.ui.theme.*
import com.example.ui.visits.*

@Composable
fun MainAppContainer() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var isLoggedIn by remember { mutableStateOf(true) }

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Visits,
        Screen.Orders,
        Screen.Academy,
        Screen.More
    )

    val showBottomBar = currentRoute in listOf(
        Destinations.HOME,
        Destinations.VISITS,
        Destinations.ORDERS,
        Destinations.ACADEMY,
        Destinations.MORE
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar && isLoggedIn) {
                NavigationBar(
                    containerColor = GeoSurface,
                    contentColor = GeoTextPrimary,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("main_bottom_nav_bar")
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = when (screen) {
                            Screen.Home -> currentRoute == Destinations.HOME
                            Screen.Visits -> currentRoute == Destinations.VISITS
                            Screen.Orders -> currentRoute == Destinations.ORDERS
                            Screen.Academy -> currentRoute == Destinations.ACADEMY
                            Screen.More -> currentRoute == Destinations.MORE
                        }

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.icon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                                    )
                                )
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GeoTextPrimary,
                                selectedTextColor = GeoTextPrimary,
                                unselectedIconColor = GeoTextSecondary,
                                unselectedTextColor = GeoTextSecondary,
                                indicatorColor = GeoSurfaceVariant
                            ),
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Destinations.HOME else Destinations.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth
            composable(Destinations.LOGIN) {
                LoginScreen(
                    onLoginSuccess = { role ->
                        isLoggedIn = true
                        val targetRoute = when (role) {
                            "SUPER_ADMIN" -> Destinations.SUPER_ADMIN_DASHBOARD
                            "ADMIN" -> Destinations.HQ_DASHBOARD
                            else -> Destinations.HOME
                        }
                        navController.navigate(targetRoute) {
                            popUpTo(Destinations.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            // Main Tabs
            composable(Destinations.HOME) {
                HomeDashboardScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(Destinations.VISITS) {
                DoctorListScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.ORDERS) {
                OrderListScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.ACADEMY) {
                AcademyDashboardScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(Destinations.MORE) {
                ProfileScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onLogout = {
                        isLoggedIn = false
                        navController.navigate(Destinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Doctor CRM
            composable(Destinations.DOCTOR_LIST) {
                DoctorListScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Destinations.DOCTOR_DETAIL,
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                DoctorDetailScreen(
                    doctorId = doctorId,
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.ADD_DOCTOR) {
                AddDoctorScreen(
                    onDoctorAdded = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Visits
            composable(
                route = Destinations.START_VISIT,
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                StartVisitScreen(
                    doctorId = doctorId,
                    onProceedToReport = { docId -> navController.navigate("visit_report/$docId") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Destinations.VISIT_REPORT,
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                VisitReportScreen(
                    doctorId = doctorId,
                    onVisitSubmitted = {
                        navController.navigate(Destinations.HOME) {
                            popUpTo(Destinations.HOME) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.VISIT_HISTORY) {
                VisitHistoryScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Product & Academy
            composable(Destinations.PRODUCT_LIST) {
                ProductListScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Destinations.PRODUCT_DETAIL,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    productId = productId,
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Destinations.MOA_VISUALIZER,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                MoaVisualizerScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Destinations.COMPETITOR_BATTLE,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                CompetitorBattleScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Destinations.ASSESSMENT,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                AssessmentScreen(
                    productId = productId,
                    onAssessmentCompleted = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Commercial
            composable(Destinations.CREATE_ORDER) {
                CreateOrderScreen(
                    onOrderCreated = { navController.popBackStack() },
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Destinations.ORDER_SUBMISSION,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderSubmissionScreen(
                    orderId = orderId,
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "order_detail/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailScreen(
                    orderId = orderId,
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.HQ_DASHBOARD) {
                AdminDashboardScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onLogout = {
                        isLoggedIn = false
                        navController.navigate(Destinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.SUPER_ADMIN_DASHBOARD) {
                SuperAdminScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onLogout = {
                        isLoggedIn = false
                        navController.navigate(Destinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.STOCKIST_LIST) {
                StockistListScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.RETAILER_LIST) {
                RetailerListScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.EXPENSES) {
                ExpenseListScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.ADD_EXPENSE) {
                AddExpenseScreen(
                    onExpenseAdded = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.ATTENDANCE) {
                AttendanceScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.ROUTE_PLAN) {
                RoutePlanScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.FOLLOW_UPS) {
                FollowUpScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Performance & Incentives
            composable(Destinations.PERFORMANCE) {
                PerformanceScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.INCENTIVES) {
                IncentiveScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.GAMIFICATION) {
                GamificationScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.LEADERBOARD) {
                LeaderboardScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.NOTIFICATIONS) {
                NotificationsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.PROFILE) {
                ProfileScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onLogout = {
                        isLoggedIn = false
                        navController.navigate(Destinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.SETTINGS) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Destinations.HELP) {
                HelpSupportScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

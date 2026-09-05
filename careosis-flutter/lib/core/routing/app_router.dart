import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../data/repository/careosis_repository.dart';
import '../../features/auth/presentation/login_screen.dart';
import '../../features/dashboard/presentation/home_dashboard_screen.dart';
import '../../features/doctors/presentation/doctor_screens.dart';
import '../../features/visits/presentation/visit_screens.dart';
import '../../features/commercial/presentation/commercial_screens.dart';
import '../../features/commercial/presentation/order_submission_screen.dart';
import '../../features/expenses/presentation/expense_management_screen.dart';
import '../../features/academy/presentation/academy_screens.dart';
import '../../features/performance/presentation/performance_and_incentive_screens.dart';
import '../../features/profile/presentation/profile_and_settings_screens.dart';
import '../../features/admin/presentation/admin_dashboard_screen.dart';
import '../../features/super_admin/presentation/super_admin_screen.dart';

GoRouter createRouter(CareOsisRepository repository) {
  return GoRouter(
    initialLocation: '/login',
    routes: [
      GoRoute(
        path: '/login',
        builder: (context, state) => LoginScreen(repository: repository),
      ),
      GoRoute(
        path: '/home',
        builder: (context, state) => HomeDashboardScreen(repository: repository),
      ),
      // Doctors & CRM
      GoRoute(
        path: '/doctors',
        builder: (context, state) => DoctorListScreen(repository: repository),
      ),
      GoRoute(
        path: '/doctors/add',
        builder: (context, state) => AddDoctorScreen(repository: repository),
      ),
      GoRoute(
        path: '/doctors/:doctorId',
        builder: (context, state) => DoctorDetailScreen(
          repository: repository,
          doctorId: state.pathParameters['doctorId'] ?? '',
        ),
      ),
      // Visits & Calls
      GoRoute(
        path: '/visits',
        builder: (context, state) => DoctorListScreen(repository: repository),
      ),
      GoRoute(
        path: '/visits/start/:doctorId',
        builder: (context, state) => StartVisitScreen(
          repository: repository,
          doctorId: state.pathParameters['doctorId'] ?? '',
        ),
      ),
      GoRoute(
        path: '/visits/history',
        builder: (context, state) => VisitHistoryScreen(repository: repository),
      ),
      // Commercial Orders
      GoRoute(
        path: '/orders',
        builder: (context, state) => OrderListScreen(repository: repository),
      ),
      GoRoute(
        path: '/orders/create',
        builder: (context, state) => CreateOrderScreen(repository: repository),
      ),
      GoRoute(
        path: '/orders/submission/:orderId',
        builder: (context, state) => OrderSubmissionScreen(
          repository: repository,
          orderId: state.pathParameters['orderId'] ?? '',
        ),
      ),
      // Field Expenses
      GoRoute(
        path: '/expenses',
        builder: (context, state) => ExpenseManagementScreen(repository: repository),
      ),
      GoRoute(
        path: '/expenses/add',
        builder: (context, state) => LogExpenseFormScreen(repository: repository),
      ),
      // Attendance & Routes
      GoRoute(
        path: '/attendance',
        builder: (context, state) => AttendanceScreen(repository: repository),
      ),
      GoRoute(
        path: '/routes',
        builder: (context, state) => RoutePlanScreen(repository: repository),
      ),
      GoRoute(
        path: '/follow-ups',
        builder: (context, state) => FollowUpScreen(repository: repository),
      ),
      // Academy
      GoRoute(
        path: '/academy',
        builder: (context, state) => AcademyDashboardScreen(repository: repository),
      ),
      GoRoute(
        path: '/products',
        builder: (context, state) => AcademyDashboardScreen(repository: repository),
      ),
      GoRoute(
        path: '/products/:productId',
        builder: (context, state) => ProductDetailScreen(
          repository: repository,
          productId: state.pathParameters['productId'] ?? '',
        ),
      ),
      GoRoute(
        path: '/products/:productId/moa',
        builder: (context, state) => MoaVisualizerScreen(
          repository: repository,
          productId: state.pathParameters['productId'] ?? '',
        ),
      ),
      GoRoute(
        path: '/products/:productId/battlecard',
        builder: (context, state) => CompetitorBattleScreen(
          repository: repository,
          productId: state.pathParameters['productId'] ?? '',
        ),
      ),
      // Performance & Incentives
      GoRoute(
        path: '/performance',
        builder: (context, state) => PerformanceScreen(repository: repository),
      ),
      GoRoute(
        path: '/incentives',
        builder: (context, state) => IncentiveScreen(repository: repository),
      ),
      GoRoute(
        path: '/leaderboard',
        builder: (context, state) => LeaderboardScreen(repository: repository),
      ),
      // Profile, Broadcasts & AI Help
      GoRoute(
        path: '/profile',
        builder: (context, state) => ProfileScreen(repository: repository),
      ),
      GoRoute(
        path: '/notifications',
        builder: (context, state) => NotificationsScreen(repository: repository),
      ),
      GoRoute(
        path: '/help',
        builder: (context, state) => HelpSupportScreen(repository: repository),
      ),
      // Admin & Super Admin Hubs
      GoRoute(
        path: '/admin/dashboard',
        builder: (context, state) => AdminDashboardScreen(repository: repository),
      ),
      GoRoute(
        path: '/super-admin',
        builder: (context, state) => SuperAdminScreen(repository: repository),
      ),
    ],
  );
}

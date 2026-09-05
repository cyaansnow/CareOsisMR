class ExpenseCalculator {
  static double calculateDailyTotal(List<double> expenses) {
    return expenses.fold<double>(0.0, (sum, val) => sum + val);
  }

  static Map<String, double> calculateCategoryTotals(
      List<MapEntry<String, double>> expenses) {
    final map = <String, double>{};
    for (final entry in expenses) {
      map[entry.key] = (map[entry.key] ?? 0.0) + entry.value;
    }
    return map;
  }

  static double calculateMileage({
    required String vehicleType,
    required double distanceKm,
  }) {
    final double rate = vehicleType.contains("4-Wheeler") || vehicleType.toLowerCase().contains("car")
        ? 8.00
        : 3.50;
    return distanceKm * rate;
  }
}

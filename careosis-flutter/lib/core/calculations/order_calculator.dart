class OrderItemInput {
  final int quantity;
  final double mrp;
  final double ratePerUnit;

  const OrderItemInput({
    required this.quantity,
    required this.mrp,
    required this.ratePerUnit,
  });
}

class OrderCalculationResult {
  final double subtotal;
  final double discountAmount;
  final double taxableAmount;
  final double gstAmount;
  final double totalAmount;
  final int totalItemsCount;
  final double retailerMarginPercent;
  final double estimatedDoctorCommission;

  const OrderCalculationResult({
    required this.subtotal,
    required this.discountAmount,
    required this.taxableAmount,
    required this.gstAmount,
    required this.totalAmount,
    required this.totalItemsCount,
    required this.retailerMarginPercent,
    required this.estimatedDoctorCommission,
  });
}

class OrderCalculator {
  static const double defaultGstRate = 0.12; // 12% pharmaceutical GST

  static double calculateItemTotal(
    int quantity,
    double ratePerUnit, {
    double discountPercent = 0.0,
  }) {
    final double gross = quantity * ratePerUnit;
    final double discount = gross * (discountPercent.clamp(0.0, 100.0) / 100.0);
    return (gross - discount).clamp(0.0, double.infinity);
  }

  static OrderCalculationResult calculateOrder(
    List<OrderItemInput> items, {
    double overallDiscountPercent = 0.0,
    double gstRate = defaultGstRate,
  }) {
    double subtotal = 0.0;
    double totalMrp = 0.0;
    int totalQuantity = 0;

    for (final item in items) {
      subtotal += item.quantity * item.ratePerUnit;
      totalMrp += item.quantity * item.mrp;
      totalQuantity += item.quantity;
    }

    final double discountAmount =
        subtotal * (overallDiscountPercent.clamp(0.0, 100.0) / 100.0);
    final double taxableAmount = subtotal - discountAmount;
    final double gstAmount = taxableAmount * gstRate;
    final double totalAmount = taxableAmount + gstAmount;

    // Retailer Margin is difference between MRP and Unit Rate relative to MRP
    final double retailerMarginPercent = totalMrp > 0
        ? ((totalMrp - subtotal) / totalMrp) * 100.0
        : 0.0;

    // Standard 10% prescription commission estimation on net sales
    final double estimatedDoctorCommission = taxableAmount * 0.10;

    return OrderCalculationResult(
      subtotal: subtotal,
      discountAmount: discountAmount,
      taxableAmount: taxableAmount,
      gstAmount: gstAmount,
      totalAmount: totalAmount,
      totalItemsCount: totalQuantity,
      retailerMarginPercent: retailerMarginPercent,
      estimatedDoctorCommission: estimatedDoctorCommission,
    );
  }
}

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/commercial_entities.dart';
import '../../../data/local/entities/doctor_and_mr_entities.dart';
import '../../../core/calculations/order_calculator.dart';
import '../../../core/services/location_tracking_service.dart';

class OrderListScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const OrderListScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: CareOsisTopBar(
        title: "Commercial Orders",
        subtitle: "POB Bookings & Invoicing",
        actions: [
          IconButton(
            icon: const Icon(Icons.add_shopping_cart, color: Colors.white),
            onPressed: () => context.push('/orders/create'),
          ),
        ],
      ),
      body: StreamBuilder<List<OrderModel>>(
        stream: repository.getAllOrders(),
        builder: (context, snapshot) {
          final orders = snapshot.data ?? [];
          if (orders.isEmpty) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const CareOsisEmptyState(
                      title: "No Commercial Orders Booked",
                      message: "No retailer chemist or stockist orders logged yet for this cycle.",
                      icon: Icons.shopping_cart_outlined,
                    ),
                    const SizedBox(height: 16),
                    ElevatedButton.icon(
                      onPressed: () => context.push('/orders/create'),
                      icon: const Icon(Icons.add_shopping_cart, color: Colors.white),
                      label: const Text("Book First POB Order", style: TextStyle(color: Colors.white)),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                      ),
                    ),
                  ],
                ),
              ),
            );
          }

          return ListView.builder(
            padding: const EdgeInsets.all(12),
            itemCount: orders.length,
            itemBuilder: (context, index) {
              final o = orders[index];
              return Card(
                margin: const EdgeInsets.only(bottom: 10),
                child: ListTile(
                  title: Text(o.customerName, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text("${o.orderDate} • ${o.itemsSummary}", style: const TextStyle(fontSize: 12)),
                      Text("Total: ₹${o.totalAmount.toStringAsFixed(0)} (incl. GST)", style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: CareOsisColors.medicalEmeraldPrimary)),
                    ],
                  ),
                  trailing: CareOsisStatusChip(label: o.status),
                  onTap: () => context.push('/orders/submission/${o.id}'),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

class CreateOrderScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const CreateOrderScreen({super.key, required this.repository});

  @override
  State<CreateOrderScreen> createState() => _CreateOrderScreenState();
}

class _CreateOrderScreenState extends State<CreateOrderScreen> {
  String _customerType = "RETAILER";
  String _customerName = "";
  int _boosterQty = 0;
  int _calciFizzQty = 0;
  double _discountPercent = 0.0;

  @override
  Widget build(BuildContext context) {
    final calc = OrderCalculator.calculateOrder([
      OrderItemInput(quantity: _boosterQty, mrp: 320.0, ratePerUnit: 224.0),
      OrderItemInput(quantity: _calciFizzQty, mrp: 280.0, ratePerUnit: 196.0),
    ], overallDiscountPercent: _discountPercent);

    return Scaffold(
      appBar: const CareOsisTopBar(title: "Create POB Order", showBack: true),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            DropdownButtonFormField<String>(
              value: _customerType,
              decoration: const InputDecoration(labelText: "Customer Channel", border: OutlineInputBorder()),
              items: const [
                DropdownMenuItem(value: "RETAILER", child: Text("Retail Chemist (POB)")),
                DropdownMenuItem(value: "STOCKIST", child: Text("Stockist / CFA Direct")),
              ],
              onChanged: (v) => setState(() => _customerType = v ?? "RETAILER"),
            ),
            const SizedBox(height: 12),
            TextField(
              decoration: const InputDecoration(
                labelText: "Account / Chemist Name",
                hintText: "Enter retailer chemist or stockist name",
                border: OutlineInputBorder(),
              ),
              onChanged: (v) => _customerName = v,
            ),
            const SizedBox(height: 16),
            const Text("Order Products", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            _buildProductRow("Booster Effervescent (MRP ₹320 / Rate ₹224)", _boosterQty, (q) => setState(() => _boosterQty = q)),
            _buildProductRow("Calci Fizz Effervescent (MRP ₹280 / Rate ₹196)", _calciFizzQty, (q) => setState(() => _calciFizzQty = q)),
            const SizedBox(height: 16),
            Card(
              color: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.06),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    _buildCalcRow("Subtotal", "₹${calc.subtotal.toStringAsFixed(2)}"),
                    _buildCalcRow("Taxable Amount", "₹${calc.taxableAmount.toStringAsFixed(2)}"),
                    _buildCalcRow("GST (12%)", "₹${calc.gstAmount.toStringAsFixed(2)}"),
                    const Divider(),
                    _buildCalcRow("Net Total Payable", "₹${calc.totalAmount.toStringAsFixed(2)}", isBold: true),
                    _buildCalcRow("Chemist Margin", "${calc.retailerMarginPercent.toStringAsFixed(1)}%"),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async {
                if (_customerName.trim().isEmpty) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text("Please enter Account / Chemist Name")),
                  );
                  return;
                }
                if (_boosterQty <= 0 && _calciFizzQty <= 0) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text("Please select at least 1 product quantity")),
                  );
                  return;
                }
                final orderId = "ORD-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}";
                final order = OrderModel(
                  id: orderId,
                  customerId: "CUST-${DateTime.now().millisecondsSinceEpoch % 1000}",
                  customerName: _customerName.trim(),
                  customerType: _customerType,
                  mrId: widget.repository.currentUser?.id ?? "MR",
                  orderDate: DateFormat("dd MMM yyyy").format(DateTime.now()),
                  subtotal: calc.subtotal,
                  discountPercent: _discountPercent,
                  discountAmount: calc.discountAmount,
                  gstAmount: calc.gstAmount,
                  totalAmount: calc.totalAmount,
                  itemsSummary: "Booster ($_boosterQty), Calci Fizz ($_calciFizzQty)",
                  status: "Submitted",
                  createdAt: DateTime.now().millisecondsSinceEpoch,
                );
                await widget.repository.createOrder(order, []);
                if (mounted) {
                  context.pushReplacement('/orders/submission/$orderId');
                }
              },
              child: const Text("Generate Invoice & Submit to HQ"),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProductRow(String title, int qty, ValueChanged<int> onChanged) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(child: Text(title, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600))),
            Row(
              children: [
                IconButton(icon: const Icon(Icons.remove_circle_outline), onPressed: qty > 0 ? () => onChanged(qty - 1) : null),
                Text("$qty", style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
                IconButton(icon: const Icon(Icons.add_circle_outline), onPressed: () => onChanged(qty + 1)),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCalcRow(String label, String value, {bool isBold = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: TextStyle(fontSize: 13, fontWeight: isBold ? FontWeight.bold : FontWeight.normal)),
          Text(value, style: TextStyle(fontSize: 13, fontWeight: isBold ? FontWeight.bold : FontWeight.normal, color: isBold ? CareOsisColors.medicalEmeraldPrimary : null)),
        ],
      ),
    );
  }
}

class AttendanceScreen extends StatefulWidget {
  final CareOsisRepository repository;
  const AttendanceScreen({super.key, required this.repository});

  @override
  State<AttendanceScreen> createState() => _AttendanceScreenState();
}

class _AttendanceScreenState extends State<AttendanceScreen> {
  bool _isLoadingGps = false;

  Future<void> _handleCheckIn(MRProfile? profile) async {
    setState(() => _isLoadingGps = true);
    try {
      final pos = await LocationTrackingService.instance.getCurrentPosition();
      final lat = pos?.latitude ?? 28.6139;
      final lng = pos?.longitude ?? 77.2090;
      final acc = pos != null ? "${pos.accuracy.toStringAsFixed(0)}m" : "Estimated";
      final locationStr = "GPS: ${lat.toStringAsFixed(4)}° N, ${lng.toStringAsFixed(4)}° E (±$acc)";

      final empId = profile?.empId ?? widget.repository.currentUser?.id ?? "MR";
      await widget.repository.checkInMR(
        empId: empId,
        locationName: locationStr,
        latitude: lat,
        longitude: lng,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Row(
              children: [
                const Icon(Icons.check_circle, color: Colors.white),
                const SizedBox(width: 8),
                Expanded(child: Text("Field Duty Started! $locationStr")),
              ],
            ),
            backgroundColor: CareOsisColors.medicalEmeraldPrimary,
            duration: const Duration(seconds: 4),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text("Error during check-in: $e"),
            backgroundColor: Colors.red.shade700,
          ),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoadingGps = false);
    }
  }

  Future<void> _handleCheckOut(MRProfile? profile) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Row(
          children: [
            Icon(Icons.logout, color: CareOsisColors.statusOrange),
            SizedBox(width: 8),
            Text("End Field Duty?"),
          ],
        ),
        content: const Text(
          "This will record your check-out timestamp, calculate total field hours, and stop background GPS location tracking.",
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text("Cancel"),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: CareOsisColors.statusOrange),
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text("Confirm Check-Out"),
          ),
        ],
      ),
    );

    if (confirm != true) return;

    setState(() => _isLoadingGps = true);
    try {
      final pos = await LocationTrackingService.instance.getCurrentPosition();
      final lat = pos?.latitude ?? 28.6139;
      final lng = pos?.longitude ?? 77.2090;
      final acc = pos != null ? "${pos.accuracy.toStringAsFixed(0)}m" : "Estimated";
      final locationStr = "GPS: ${lat.toStringAsFixed(4)}° N, ${lng.toStringAsFixed(4)}° E (±$acc)";

      final empId = profile?.empId ?? widget.repository.currentUser?.id ?? "MR";
      final updated = await widget.repository.checkOutMR(
        empId: empId,
        locationName: locationStr,
        latitude: lat,
        longitude: lng,
      );

      if (mounted) {
        showDialog(
          context: context,
          builder: (ctx) => AlertDialog(
            title: const Row(
              children: [
                Icon(Icons.verified, color: CareOsisColors.medicalEmeraldPrimary),
                SizedBox(width: 8),
                Text("Field Duty Completed"),
              ],
            ),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "Total Working Hours: ${updated?.workingHours ?? 'Completed'}",
                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                ),
                const SizedBox(height: 10),
                Text("Check-In: ${updated?.checkInTime ?? '--'}"),
                Text("Check-Out: ${updated?.checkOutTime ?? '--'}"),
                const SizedBox(height: 10),
                const Text(
                  "GPS tracking has been safely deactivated.",
                  style: TextStyle(color: Colors.black54, fontSize: 13),
                ),
              ],
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(ctx),
                child: const Text("OK"),
              ),
            ],
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text("Error during check-out: $e"),
            backgroundColor: Colors.red.shade700,
          ),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoadingGps = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final todayStr = DateFormat("EEEE, dd MMMM yyyy").format(DateTime.now());

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: const CareOsisTopBar(
        title: "Daily Attendance & GPS",
        subtitle: "Field Duty Lifecycle & Geotag",
        showBack: true,
      ),
      body: StreamBuilder<MRProfile?>(
        stream: widget.repository.getMRProfile(),
        builder: (context, profileSnapshot) {
          final profile = profileSnapshot.data;
          final isCheckedIn = profile?.isCheckedInToday ?? false;
          final checkInTime = profile?.checkInTime ?? "";

          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Top Date & Territory Header
                Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: const Color(0xFFE2E8F0)),
                  ),
                  child: Row(
                    children: [
                      CircleAvatar(
                        radius: 22,
                        backgroundColor: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.12),
                        child: const Icon(Icons.badge_outlined, color: CareOsisColors.medicalEmeraldPrimary),
                      ),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              todayStr,
                              style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold),
                            ),
                            const SizedBox(height: 2),
                            Text(
                              profile?.territory ?? "Assigned Field Beat",
                              style: const TextStyle(fontSize: 12, color: Colors.black54),
                            ),
                          ],
                        ),
                      ),
                      CareOsisStatusChip(
                        label: isCheckedIn ? "ON DUTY" : "OFF DUTY",
                        color: isCheckedIn ? CareOsisColors.statusGreen : Colors.grey,
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 16),

                // Live GPS Tracking Status Banner
                StreamBuilder<bool>(
                  stream: LocationTrackingService.instance.trackingStateStream,
                  builder: (context, trackingSnapshot) {
                    final trackingActive = trackingSnapshot.data ?? false;
                    return Container(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                      decoration: BoxDecoration(
                        color: trackingActive ? const Color(0xFFECFDF5) : const Color(0xFFF1F5F9),
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(
                          color: trackingActive ? const Color(0xFFA7F3D0) : const Color(0xFFCBD5E1),
                        ),
                      ),
                      child: Row(
                        children: [
                          Icon(
                            trackingActive ? Icons.my_location : Icons.location_disabled,
                            color: trackingActive ? const Color(0xFF059669) : Colors.grey.shade600,
                            size: 22,
                          ),
                          const SizedBox(width: 10),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  trackingActive ? "GPS Tracking Active" : "GPS Tracking Inactive",
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: 13,
                                    color: trackingActive ? const Color(0xFF065F46) : Colors.grey.shade800,
                                  ),
                                ),
                                Text(
                                  trackingActive
                                      ? "Background location updates running during duty"
                                      : "Check in below to start tracking your route",
                                  style: TextStyle(
                                    fontSize: 11,
                                    color: trackingActive ? const Color(0xFF047857) : Colors.grey.shade600,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Container(
                            width: 10,
                            height: 10,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              color: trackingActive ? Colors.greenAccent.shade700 : Colors.grey,
                            ),
                          ),
                        ],
                      ),
                    );
                  },
                ),
                const SizedBox(height: 16),

                // Primary Check-In / Check-Out Action Card
                Card(
                  elevation: 2,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                  color: isCheckedIn ? const Color(0xFFF0FDF4) : Colors.white,
                  child: Padding(
                    padding: const EdgeInsets.all(20),
                    child: Column(
                      children: [
                        Icon(
                          isCheckedIn ? Icons.verified_user : Icons.fingerprint,
                          size: 56,
                          color: isCheckedIn ? CareOsisColors.medicalEmeraldPrimary : Colors.blueGrey,
                        ),
                        const SizedBox(height: 12),
                        Text(
                          isCheckedIn ? "You are Checked In" : "Ready for Field Duty",
                          style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          isCheckedIn
                              ? "Checked in at $checkInTime. GPS tracking location coordinates."
                              : "Tap below to capture live GPS coordinates and start field duty.",
                          textAlign: TextAlign.center,
                          style: const TextStyle(color: Colors.black54, fontSize: 13),
                        ),
                        const SizedBox(height: 20),

                        // Action Button
                        if (_isLoadingGps)
                          const Column(
                            children: [
                              CircularProgressIndicator(),
                              SizedBox(height: 10),
                              Text("Acquiring GPS Fix...", style: TextStyle(fontSize: 12, color: Colors.black54)),
                            ],
                          )
                        else if (!isCheckedIn)
                          ElevatedButton.icon(
                            onPressed: () => _handleCheckIn(profile),
                            style: ElevatedButton.styleFrom(
                              backgroundColor: CareOsisColors.medicalEmeraldPrimary,
                              foregroundColor: Colors.white,
                              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                              minimumSize: const Size(double.infinity, 48),
                            ),
                            icon: const Icon(Icons.login),
                            label: const Text(
                              "Start Duty (Geotagged Check-In)",
                              style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
                            ),
                          )
                        else
                          ElevatedButton.icon(
                            onPressed: () => _handleCheckOut(profile),
                            style: ElevatedButton.styleFrom(
                              backgroundColor: const Color(0xFFDC2626),
                              foregroundColor: Colors.white,
                              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                              minimumSize: const Size(double.infinity, 48),
                            ),
                            icon: const Icon(Icons.logout),
                            label: const Text(
                              "End Duty (Geotagged Check-Out)",
                              style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
                            ),
                          ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 24),

                // Attendance Logs History Header
                const Row(
                  children: [
                    Icon(Icons.history, size: 20, color: CareOsisColors.medicalEmeraldPrimary),
                    SizedBox(width: 8),
                    Text(
                      "Field Attendance History",
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    ),
                  ],
                ),
                const SizedBox(height: 10),

                // Live Attendance Stream List
                StreamBuilder<List<AttendanceModel>>(
                  stream: widget.repository.getAllAttendance(),
                  builder: (context, attendanceSnapshot) {
                    final list = attendanceSnapshot.data ?? [];

                    if (list.isEmpty) {
                      return const Card(
                        child: CareOsisEmptyState(
                          icon: Icons.calendar_month,
                          title: "No Attendance Records Found",
                          message: "Your daily check-in logs and field duty hours will appear here.",
                        ),
                      );
                    }

                    return ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: list.length,
                      itemBuilder: (context, index) {
                        final item = list[index];
                        return Card(
                          margin: const EdgeInsets.only(bottom: 10),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                          child: Padding(
                            padding: const EdgeInsets.all(14),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Text(
                                      item.date,
                                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
                                    ),
                                    CareOsisStatusChip(label: item.status),
                                  ],
                                ),
                                const Divider(height: 16),
                                Row(
                                  children: [
                                    Expanded(
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          const Text("Check-In", style: TextStyle(fontSize: 11, color: Colors.black54)),
                                          Text(
                                            item.checkInTime.isNotEmpty ? item.checkInTime : "--",
                                            style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13),
                                          ),
                                        ],
                                      ),
                                    ),
                                    Expanded(
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          const Text("Check-Out", style: TextStyle(fontSize: 11, color: Colors.black54)),
                                          Text(
                                            item.checkOutTime.isNotEmpty ? item.checkOutTime : "--",
                                            style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13),
                                          ),
                                        ],
                                      ),
                                    ),
                                    Expanded(
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          const Text("Duty Hours", style: TextStyle(fontSize: 11, color: Colors.black54)),
                                          Text(
                                            item.workingHours.isNotEmpty ? item.workingHours : "Active",
                                            style: TextStyle(
                                              fontWeight: FontWeight.bold,
                                              fontSize: 13,
                                              color: item.workingHours.isNotEmpty
                                                  ? CareOsisColors.medicalEmeraldPrimary
                                                  : CareOsisColors.statusOrange,
                                            ),
                                          ),
                                        ],
                                      ),
                                    ),
                                  ],
                                ),
                                if (item.checkInLocation.isNotEmpty) ...[
                                  const SizedBox(height: 8),
                                  Row(
                                    children: [
                                      const Icon(Icons.pin_drop, size: 14, color: Colors.black45),
                                      const SizedBox(width: 4),
                                      Expanded(
                                        child: Text(
                                          item.checkInLocation,
                                          style: const TextStyle(fontSize: 11, color: Colors.black54),
                                          overflow: TextOverflow.ellipsis,
                                        ),
                                      ),
                                    ],
                                  ),
                                ],
                              ],
                            ),
                          ),
                        );
                      },
                    );
                  },
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}

class RoutePlanScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const RoutePlanScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CareOsisTopBar(title: "Field Beat Route", showBack: true),
      body: StreamBuilder<List<RoutePlanModel>>(
        stream: repository.getAllRoutes(),
        builder: (context, snapshot) {
          final routes = snapshot.data ?? [];
          if (routes.isEmpty) {
            return const Center(
              child: CareOsisEmptyState(
                title: "No Beat Routes Scheduled",
                message: "No beat plans assigned for today. Contact regional admin or plan a route.",
                icon: Icons.alt_route_outlined,
              ),
            );
          }
          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: routes.length,
            itemBuilder: (context, index) {
              final r = routes[index];
              return Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(r.title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                          CareOsisStatusChip(label: r.status),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text("Stops: ${r.stopsListText}", style: const TextStyle(fontSize: 13, color: Colors.black87)),
                    ],
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

class FollowUpScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const FollowUpScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CareOsisTopBar(title: "Follow-Up Action Items", showBack: true),
      body: StreamBuilder<List<FollowUpModel>>(
        stream: repository.getAllFollowUps(),
        builder: (context, snapshot) {
          final list = snapshot.data ?? [];
          if (list.isEmpty) {
            return const Center(
              child: CareOsisEmptyState(
                title: "No Follow-Ups Pending",
                message: "All prescriber detailing action items and commitments are complete.",
                icon: Icons.task_alt_outlined,
              ),
            );
          }
          return ListView.builder(
            padding: const EdgeInsets.all(12),
            itemCount: list.length,
            itemBuilder: (context, index) {
              final item = list[index];
              return Card(
                child: ListTile(
                  title: Text(item.personName, style: const TextStyle(fontWeight: FontWeight.bold)),
                  subtitle: Text("${item.followUpDate} • ${item.reason}"),
                  trailing: CareOsisStatusChip(label: item.priority),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

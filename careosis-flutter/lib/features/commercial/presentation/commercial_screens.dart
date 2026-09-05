import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/commercial_entities.dart';
import '../../../core/calculations/order_calculator.dart';
import '../../../core/engine/rule_engine.dart';

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
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.shopping_cart_outlined, size: 64, color: Colors.black26),
                  const SizedBox(height: 12),
                  const Text("No commercial orders booked yet"),
                  const SizedBox(height: 12),
                  ElevatedButton(
                    onPressed: () => context.push('/orders/create'),
                    child: const Text("Book First POB Order"),
                  ),
                ],
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
  String _customerName = "Apollo MedPlus Chemist";
  int _boosterQty = 10;
  int _calciFizzQty = 5;
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
              decoration: const InputDecoration(labelText: "Account Name", border: OutlineInputBorder()),
              controller: TextEditingController(text: _customerName),
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
                final orderId = "ORD-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}";
                final order = OrderModel(
                  id: orderId,
                  customerId: "RET-001",
                  customerName: _customerName,
                  customerType: _customerType,
                  mrId: widget.repository.currentUser?.id ?? "CO-MR-8492",
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

class AttendanceScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const AttendanceScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CareOsisTopBar(title: "Daily Attendance & GPS", showBack: true),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    const Icon(Icons.location_on, color: CareOsisColors.medicalEmeraldPrimary, size: 48),
                    const SizedBox(height: 8),
                    const Text("Today: 21 Aug 2026", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    const Text("Assigned Beat: North Delhi Central", style: TextStyle(color: Colors.black54)),
                    const SizedBox(height: 16),
                    ElevatedButton.icon(
                      onPressed: () async {
                        final timeStr = DateFormat("hh:mm a").format(DateTime.now());
                        final user = repository.currentUser;
                        final attRule = repository.resolveRule("ATTENDANCE", employeeId: user?.id, regionId: user?.assignedRegionIds);
                        final gpsRule = repository.resolveRule("GPS", employeeId: user?.id, regionId: user?.assignedRegionIds);

                        final eval = RuleEngine.evaluateAttendance(
                          checkInTimeFormatted: timeStr,
                          accuracyMeters: 38.0,
                          attendanceRule: attRule,
                          gpsRule: gpsRule,
                        );

                        final att = AttendanceModel(
                          id: "2026-08-21",
                          date: "2026-08-21",
                          checkInTime: timeStr,
                          status: eval.requiresApproval ? "Exception" : (eval.isLate ? "Late" : "Present"),
                          checkInLocation: "GPS: 28.7041° N, 77.1025° E (Accuracy: 38m)",
                        );

                        await repository.markAttendance(att, user?.id ?? "CO-MR-8492");

                        if (eval.requiresApproval) {
                          await repository.submitApprovalRequest(
                            module: "ATTENDANCE",
                            entityId: "2026-08-21",
                            title: "Attendance Exception ($timeStr)",
                            details: eval.exceptionReason,
                            submittedBy: user?.id ?? "CO-MR-8492",
                            submittedByName: user?.name ?? "Aman Chhabra",
                            scope: user?.assignedRegionIds ?? "REG-001",
                            sla: "24h",
                          );
                        }

                        if (context.mounted) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(
                              content: Text(eval.requiresApproval
                                  ? "Check-in logged with exception. Sent to Admin for review."
                                  : "Geotagged check-in verified on-time!"),
                              backgroundColor: eval.requiresApproval ? Colors.orange.shade800 : CareOsisColors.medicalEmeraldPrimary,
                            ),
                          );
                        }
                      },
                      icon: const Icon(Icons.fingerprint),
                      label: const Text("Geotagged Check-In"),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
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

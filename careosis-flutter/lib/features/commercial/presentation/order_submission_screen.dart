import 'package:flutter/material.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/commercial_entities.dart';

class OrderSubmissionScreen extends StatelessWidget {
  final CareOsisRepository repository;
  final String orderId;
  const OrderSubmissionScreen({super.key, required this.repository, required this.orderId});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<OrderModel?>(
      stream: repository.getOrderById(orderId),
      builder: (context, snapshot) {
        final order = snapshot.data;
        if (order == null) return const Scaffold(body: Center(child: CircularProgressIndicator()));

        return Scaffold(
          appBar: CareOsisTopBar(
            title: "Proforma Invoice #${order.id}",
            showBack: true,
          ),
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(order.customerName, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                            CareOsisStatusChip(label: order.status),
                          ],
                        ),
                        Text("Channel: ${order.customerType} • Date: ${order.orderDate}", style: const TextStyle(fontSize: 12, color: Colors.black54)),
                        const Divider(height: 24),
                        const Text("Booked Items:", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                        const SizedBox(height: 4),
                        Text(order.itemsSummary, style: const TextStyle(fontSize: 13)),
                        const Divider(height: 24),
                        _buildRow("Subtotal", "₹${order.subtotal.toStringAsFixed(2)}"),
                        _buildRow("GST (12%)", "₹${order.gstAmount.toStringAsFixed(2)}"),
                        _buildRow("Net Payable", "₹${order.totalAmount.toStringAsFixed(2)}", isBold: true),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 24),
                ElevatedButton.icon(
                  onPressed: () async {
                    await repository.sendOrderToHq(order.id);
                    if (context.mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text("Order transmitted to Central HQ & CFA depot!")),
                      );
                      Navigator.pop(context);
                    }
                  },
                  icon: const Icon(Icons.send_rounded),
                  label: const Text("Transmit to Central HQ / CFA Depot"),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildRow(String label, String value, {bool isBold = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 3),
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

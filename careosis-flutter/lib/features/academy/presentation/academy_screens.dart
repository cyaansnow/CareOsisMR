import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/theme/careosis_theme.dart';
import '../../../core/components/careosis_components.dart';
import '../../../data/repository/careosis_repository.dart';
import '../../../data/local/entities/product_and_academy_entities.dart';

class AcademyDashboardScreen extends StatelessWidget {
  final CareOsisRepository repository;
  const AcademyDashboardScreen({super.key, required this.repository});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: const CareOsisTopBar(title: "Clinical Academy", subtitle: "Scientific Dossiers & Mastery"),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Container(
              padding: const EdgeInsets.all(18),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [CareOsisColors.medicalTertiary, Color(0xFF6B4900)],
                ),
                borderRadius: BorderRadius.circular(16),
              ),
              child: const Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text("MR CLINICAL CERTIFICATION", style: TextStyle(color: Colors.white70, fontSize: 11, fontWeight: FontWeight.bold)),
                  SizedBox(height: 6),
                  Text("Expert MR Tier (78%)", style: TextStyle(color: Colors.white, fontSize: 22, fontWeight: FontWeight.bold)),
                  SizedBox(height: 4),
                  Text("Complete 3 remaining product masterclasses to achieve CareOsis Master MR badge.", style: TextStyle(color: Colors.white70, fontSize: 12)),
                ],
              ),
            ),
            const SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text("Scientific Product Portfolio", style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
                TextButton(
                  onPressed: () => context.push('/products'),
                  child: const Text("View All 19"),
                ),
              ],
            ),
            StreamBuilder<List<ProductModel>>(
              stream: repository.getAllProducts(),
              builder: (context, snapshot) {
                final products = snapshot.data ?? [];
                return ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: products.take(6).length,
                  itemBuilder: (context, index) {
                    final p = products[index];
                    return Card(
                      margin: const EdgeInsets.only(bottom: 10),
                      child: ListTile(
                        leading: CircleAvatar(
                          backgroundColor: CareOsisColors.medicalEmeraldPrimary.withOpacity(0.1),
                          child: const Icon(Icons.medication, color: CareOsisColors.medicalEmeraldPrimary),
                        ),
                        title: Text(p.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                        subtitle: Text("${p.category} • MRP ₹${p.mrp.toInt()}", style: const TextStyle(fontSize: 12)),
                        trailing: p.isFocusProduct ? const CareOsisStatusChip(label: "Focus") : null,
                        onTap: () => context.push('/products/${p.id}'),
                      ),
                    );
                  },
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}

class ProductDetailScreen extends StatelessWidget {
  final CareOsisRepository repository;
  final String productId;
  const ProductDetailScreen({super.key, required this.repository, required this.productId});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<ProductModel?>(
      stream: repository.getProductById(productId),
      builder: (context, snapshot) {
        final p = snapshot.data;
        if (p == null) return const Scaffold(body: Center(child: CircularProgressIndicator()));

        return Scaffold(
          backgroundColor: const Color(0xFFF8FAFC),
          appBar: CareOsisTopBar(title: p.name, subtitle: p.category, showBack: true),
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
                        Text(p.name, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                        Text("MRP: ₹${p.mrp.toInt()} | Chemist Rate: ₹${p.retailerRate.toInt()}", style: const TextStyle(fontSize: 13, color: CareOsisColors.medicalEmeraldPrimary, fontWeight: FontWeight.bold)),
                        const Divider(height: 20),
                        _buildSection("Composition", p.composition),
                        _buildSection("Clinical Indications", p.indications),
                        _buildSection("Mechanism of Action", p.mechanismOfAction),
                        _buildSection("Key Scientific Benefits", p.keyBenefits),
                        _buildSection("Prescriber Detailing Pitch", p.mrPitch),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: () => context.push('/products/${p.id}/moa'),
                        icon: const Icon(Icons.biotech_outlined),
                        label: const Text("MoA Visualizer"),
                      ),
                    ),
                    const SizedBox(width: 10),
                    Expanded(
                      child: OutlinedButton.icon(
                        onPressed: () => context.push('/products/${p.id}/battlecard'),
                        icon: const Icon(Icons.compare_arrows),
                        label: const Text("Battlecard"),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildSection(String title, String content) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.black54)),
          const SizedBox(height: 2),
          Text(content, style: const TextStyle(fontSize: 13, color: Colors.black87)),
        ],
      ),
    );
  }
}

class MoaVisualizerScreen extends StatelessWidget {
  final CareOsisRepository repository;
  final String productId;
  const MoaVisualizerScreen({super.key, required this.repository, required this.productId});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<ProductModel?>(
      stream: repository.getProductById(productId),
      builder: (context, snapshot) {
        final p = snapshot.data;
        return Scaffold(
          appBar: CareOsisTopBar(title: "${p?.name ?? 'Product'} Mechanism", showBack: true),
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Card(
              child: Padding(
                padding: const EdgeInsets.all(18),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Row(
                      children: [
                        Icon(Icons.biotech, color: CareOsisColors.medicalEmeraldPrimary, size: 28),
                        SizedBox(width: 8),
                        Text("Cellular Delivery Pathway", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                      ],
                    ),
                    const SizedBox(height: 14),
                    Text(p?.mechanismOfAction ?? "Mechanism details", style: const TextStyle(fontSize: 14, height: 1.5)),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}

class CompetitorBattleScreen extends StatelessWidget {
  final CareOsisRepository repository;
  final String productId;
  const CompetitorBattleScreen({super.key, required this.repository, required this.productId});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<ProductModel?>(
      stream: repository.getProductById(productId),
      builder: (context, snapshot) {
        final p = snapshot.data;
        return Scaffold(
          appBar: CareOsisTopBar(title: "${p?.name ?? 'Product'} Battlecard", showBack: true),
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Card(
              child: Padding(
                padding: const EdgeInsets.all(18),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text("Clinical Superiority Matrix", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 14),
                    Text(p?.competitorInfo ?? "Competitor benchmark information", style: const TextStyle(fontSize: 14, height: 1.5)),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}

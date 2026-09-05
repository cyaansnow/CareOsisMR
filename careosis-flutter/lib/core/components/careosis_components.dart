import 'package:flutter/material.dart';
import '../theme/careosis_theme.dart';

class CareOsisTopBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  final String? subtitle;
  final List<Widget>? actions;
  final bool showBack;
  final VoidCallback? onBack;

  const CareOsisTopBar({
    super.key,
    required this.title,
    this.subtitle,
    this.actions,
    this.showBack = false,
    this.onBack,
  });

  @override
  Widget build(BuildContext context) {
    return AppBar(
      title: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.white),
          ),
          if (subtitle != null)
            Text(
              subtitle!,
              style: const TextStyle(fontSize: 12, color: Colors.white70),
            ),
        ],
      ),
      leading: showBack
          ? IconButton(
              icon: const Icon(Icons.arrow_back, color: Colors.white),
              onPressed: onBack ?? () => Navigator.of(context).pop(),
            )
          : null,
      actions: actions,
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}

class CareOsisStatusChip extends StatelessWidget {
  final String label;
  final Color? color;
  final Color? textColor;

  const CareOsisStatusChip({
    super.key,
    required this.label,
    this.color,
    this.textColor,
  });

  @override
  Widget build(BuildContext context) {
    final statusColor = color ?? _resolveColor(label);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: statusColor.withOpacity(0.12),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: statusColor.withOpacity(0.3)),
      ),
      child: Text(
        label,
        style: TextStyle(
          color: textColor ?? statusColor,
          fontWeight: FontWeight.w600,
          fontSize: 12,
        ),
      ),
    );
  }

  Color _resolveColor(String status) {
    switch (status.toUpperCase()) {
      case "ACTIVE":
      case "APPROVED":
      case "COMPLETED":
      case "FINAL":
      case "PRESENT":
        return CareOsisColors.statusGreen;
      case "PENDING":
      case "ESTIMATED":
      case "IN-PROGRESS":
      case "SUBMITTED":
        return CareOsisColors.statusOrange;
      case "REJECTED":
      case "TERMINATED":
      case "SUSPENDED":
        return CareOsisColors.statusRed;
      default:
        return CareOsisColors.statusBlue;
    }
  }
}

class CareOsisOfflineBanner extends StatelessWidget {
  final int pendingCount;
  final VoidCallback onSync;

  const CareOsisOfflineBanner({
    super.key,
    required this.pendingCount,
    required this.onSync,
  });

  @override
  Widget build(BuildContext context) {
    if (pendingCount <= 0) return const SizedBox.shrink();
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
      color: const Color(0xFFFFF3CD),
      child: Row(
        children: [
          const Icon(Icons.cloud_off, color: Color(0xFF856404), size: 20),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              "$pendingCount pending changes saved offline.",
              style: const TextStyle(color: Color(0xFF856404), fontSize: 13, fontWeight: FontWeight.w500),
            ),
          ),
          TextButton(
            onPressed: onSync,
            style: TextButton.styleFrom(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              backgroundColor: const Color(0xFF856404),
              foregroundColor: Colors.white,
            ),
            child: const Text("Sync Now", style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }
}

class CareOsisEmptyState extends StatelessWidget {
  final IconData icon;
  final String title;
  final String message;
  final Widget? action;

  const CareOsisEmptyState({
    super.key,
    required this.icon,
    required this.title,
    required this.message,
    this.action,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 36, horizontal: 24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.grey.shade100,
                shape: BoxShape.circle,
              ),
              child: Icon(icon, size: 48, color: Colors.grey.shade400),
            ),
            const SizedBox(height: 16),
            Text(
              title,
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
                color: Colors.grey.shade800,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 6),
            Text(
              message,
              style: TextStyle(
                fontSize: 13,
                color: Colors.grey.shade600,
              ),
              textAlign: TextAlign.center,
            ),
            if (action != null) ...[
              const SizedBox(height: 16),
              action!,
            ],
          ],
        ),
      ),
    );
  }
}


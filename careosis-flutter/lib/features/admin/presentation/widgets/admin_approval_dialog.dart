import 'package:flutter/material.dart';
import '../../../../core/theme/careosis_theme.dart';
import '../../../../core/components/careosis_components.dart';
import '../../../../data/repository/careosis_repository.dart';
import '../../../../data/local/entities/admin_and_security_entities.dart';

class AdminApprovalDialog extends StatefulWidget {
  final CareOsisRepository repository;
  final ApprovalRequest request;
  final VoidCallback? onHandled;

  const AdminApprovalDialog({
    super.key,
    required this.repository,
    required this.request,
    this.onHandled,
  });

  static void show(BuildContext context, CareOsisRepository repository, ApprovalRequest request, {VoidCallback? onHandled}) {
    showDialog(
      context: context,
      builder: (ctx) => AdminApprovalDialog(
        repository: repository,
        request: request,
        onHandled: onHandled,
      ),
    );
  }

  @override
  State<AdminApprovalDialog> createState() => _AdminApprovalDialogState();
}

class _AdminApprovalDialogState extends State<AdminApprovalDialog> {
  final _reasonController = TextEditingController();
  bool _isRejecting = false;
  bool _isLoading = false;

  @override
  void dispose() {
    _reasonController.dispose();
    super.dispose();
  }

  Future<void> _handleApprove() async {
    setState(() => _isLoading = true);
    final user = widget.repository.currentUser;
    await widget.repository.approveApprovalRequest(
      approvalId: widget.request.approvalId,
      reviewerId: user?.id ?? "CO-ADM-101",
      reviewerName: user?.name ?? "Regional Admin",
      reviewerRole: user?.role ?? "ADMIN",
      comment: "Approved by ${user?.name ?? 'Admin'}",
    );
    if (mounted) {
      Navigator.of(context).pop();
      widget.onHandled?.call();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text("${widget.request.module} request #${widget.request.approvalId} approved successfully."),
          backgroundColor: CareOsisColors.medicalEmeraldPrimary,
        ),
      );
    }
  }

  Future<void> _handleReject() async {
    final reason = _reasonController.text.trim();
    if (reason.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text("Rejection reason is mandatory."),
          backgroundColor: Colors.red,
        ),
      );
      return;
    }

    setState(() => _isLoading = true);
    final user = widget.repository.currentUser;
    await widget.repository.rejectApprovalRequest(
      approvalId: widget.request.approvalId,
      reviewerId: user?.id ?? "CO-ADM-101",
      reviewerName: user?.name ?? "Regional Admin",
      reviewerRole: user?.role ?? "ADMIN",
      rejectionReason: reason,
    );
    if (mounted) {
      Navigator.of(context).pop();
      widget.onHandled?.call();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text("${widget.request.module} request rejected. Submitter notified with reason."),
          backgroundColor: Colors.orange.shade800,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final req = widget.request;
    return AlertDialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      title: Row(
        children: [
          Icon(
            req.module == "EXPENSE"
                ? Icons.receipt_long
                : req.module == "ATTENDANCE"
                    ? Icons.fingerprint
                    : req.module == "DOCTOR_VISIT"
                        ? Icons.local_hospital
                        : Icons.assignment_turned_in,
            color: CareOsisColors.medicalEmeraldPrimary,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text("Review ${req.module}", style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                Text("ID: ${req.approvalId} • SLA: ${req.sla}", style: const TextStyle(fontSize: 11, color: Colors.grey)),
              ],
            ),
          ),
        ],
      ),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: const Color(0xFFF1F5F9),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(req.title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                  const SizedBox(height: 4),
                  Text(req.details, style: const TextStyle(fontSize: 12, color: Colors.black87)),
                  const SizedBox(height: 8),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text("By: ${req.submittedByName} (${req.submittedBy})", style: const TextStyle(fontSize: 11, color: Colors.black54)),
                      CareOsisStatusChip(label: req.status),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            if (_isRejecting) ...[
              const Text(
                "Reason for Rejection *",
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Colors.red),
              ),
              const SizedBox(height: 6),
              TextField(
                controller: _reasonController,
                maxLines: 3,
                decoration: InputDecoration(
                  hintText: "Enter specific reason (e.g. Receipt missing, GPS location outside approved beat)...",
                  hintStyle: const TextStyle(fontSize: 12),
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                  filled: true,
                  fillColor: Colors.white,
                ),
              ),
              const SizedBox(height: 10),
            ],
          ],
        ),
      ),
      actions: [
        if (_isLoading)
          const Center(child: CircularProgressIndicator())
        else if (!_isRejecting) ...[
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text("Close"),
          ),
          OutlinedButton.icon(
            onPressed: () => setState(() => _isRejecting = true),
            icon: const Icon(Icons.close, size: 16, color: Colors.red),
            label: const Text("Reject", style: TextStyle(color: Colors.red)),
          ),
          ElevatedButton.icon(
            style: ElevatedButton.styleFrom(
              backgroundColor: CareOsisColors.medicalEmeraldPrimary,
              foregroundColor: Colors.white,
            ),
            onPressed: _handleApprove,
            icon: const Icon(Icons.check, size: 16),
            label: const Text("Approve"),
          ),
        ] else ...[
          TextButton(
            onPressed: () => setState(() => _isRejecting = false),
            child: const Text("Back"),
          ),
          ElevatedButton.icon(
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red.shade700,
              foregroundColor: Colors.white,
            ),
            onPressed: _handleReject,
            icon: const Icon(Icons.close, size: 16),
            label: const Text("Confirm Rejection"),
          ),
        ],
      ],
    );
  }
}

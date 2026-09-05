import '../../data/local/entities/admin_and_security_entities.dart';
import '../../data/local/db/careosis_database.dart';
import 'audit_service.dart';

class ApprovalService {
  final CareOsisDatabase _db;
  final AuditService _auditService;

  ApprovalService(this._db, this._auditService);

  /// Submits a new approval request into the unified Approval Engine
  Future<ApprovalRequest> submitRequest({
    required String module, // ATTENDANCE, DOCTOR_VISIT, EXPENSE, TARGET, INCENTIVE, ORDER
    required String entityId,
    required String title,
    required String details,
    required String submittedBy,
    required String submittedByName,
    String? approverId,
    String? approverRole,
    String? scope,
    String sla = "24h",
  }) async {
    final defaultApproverRole = (module == "TARGET" || module == "INCENTIVE") ? "SUPER_ADMIN" : "ADMIN";
    final defaultApproverId = (defaultApproverRole == "SUPER_ADMIN") ? "CO-SA-001" : (approverId ?? "CO-ADM-101");
    final approvalId = "APR-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}";

    final request = ApprovalRequest(
      approvalId: approvalId,
      module: module,
      entityId: entityId,
      title: title,
      details: details,
      submittedBy: submittedBy,
      submittedByName: submittedByName,
      approverId: defaultApproverId,
      approverRole: approverRole ?? defaultApproverRole,
      scope: scope ?? "REG-001",
      status: "PENDING",
      comment: "",
      sla: sla,
      createdAt: DateTime.now().millisecondsSinceEpoch,
    );

    _db.adminAndSecurityDao.insertApprovalRequest(request);

    await _auditService.logAction(
      actorId: submittedBy,
      actorName: submittedByName,
      actorRole: "EMPLOYEE",
      action: "SUBMIT_${module}_APPROVAL",
      targetEntity: module,
      entityId: entityId,
      oldValue: "NEW",
      newValue: "PENDING",
    );

    return request;
  }

  /// Approves a pending request
  Future<bool> approveRequest({
    required String approvalId,
    required String reviewerId,
    required String reviewerName,
    required String reviewerRole,
    String comment = "Approved",
  }) async {
    final existing = _db.adminAndSecurityDao.getApprovalById(approvalId);
    if (existing == null) return false;

    final updated = ApprovalRequest(
      approvalId: existing.approvalId,
      module: existing.module,
      entityId: existing.entityId,
      title: existing.title,
      details: existing.details,
      submittedBy: existing.submittedBy,
      submittedByName: existing.submittedByName,
      approverId: reviewerId,
      approverRole: reviewerRole,
      scope: existing.scope,
      status: "APPROVED",
      comment: comment,
      sla: existing.sla,
      createdAt: existing.createdAt,
      reviewedAt: DateTime.now().millisecondsSinceEpoch,
    );

    await _db.adminAndSecurityDao.updateApprovalRequest(updated);

    // Apply entity-specific approval status side-effects
    if (existing.module == "EXPENSE") {
      final exp = _db.expenses[existing.entityId];
      if (exp != null) {
        _db.expenses[existing.entityId] = exp;
        await _db.commercialDao.updateExpenseStatus(existing.entityId, "Approved");
      }
    } else if (existing.module == "ATTENDANCE") {
      final att = _db.attendance[existing.entityId];
      if (att != null) {
        await _db.platformDao.updateAttendanceApprovalStatus(existing.entityId, "APPROVED");
      }
    } else if (existing.module == "DOCTOR_VISIT") {
      final visit = _db.doctorVisits[existing.entityId];
      if (visit != null) {
        await _db.doctorVisitDao.updateVisitApprovalStatus(existing.entityId, "APPROVED");
      }
    }

    await _auditService.logAction(
      actorId: reviewerId,
      actorName: reviewerName,
      actorRole: reviewerRole,
      action: "APPROVE_${existing.module}",
      targetEntity: existing.module,
      entityId: existing.entityId,
      oldValue: "PENDING",
      newValue: "APPROVED",
    );

    return true;
  }

  /// Rejects a pending request (Mandatory reason required)
  Future<bool> rejectRequest({
    required String approvalId,
    required String reviewerId,
    required String reviewerName,
    required String reviewerRole,
    required String rejectionReason,
  }) async {
    if (rejectionReason.trim().isEmpty) {
      throw ArgumentError("Rejection reason is mandatory.");
    }

    final existing = _db.adminAndSecurityDao.getApprovalById(approvalId);
    if (existing == null) return false;

    final updated = ApprovalRequest(
      approvalId: existing.approvalId,
      module: existing.module,
      entityId: existing.entityId,
      title: existing.title,
      details: existing.details,
      submittedBy: existing.submittedBy,
      submittedByName: existing.submittedByName,
      approverId: reviewerId,
      approverRole: reviewerRole,
      scope: existing.scope,
      status: "REJECTED",
      comment: rejectionReason,
      sla: existing.sla,
      createdAt: existing.createdAt,
      reviewedAt: DateTime.now().millisecondsSinceEpoch,
    );

    await _db.adminAndSecurityDao.updateApprovalRequest(updated);

    // Apply entity-specific rejection status side-effects
    if (existing.module == "EXPENSE") {
      await _db.commercialDao.updateExpenseStatus(existing.entityId, "Rejected: $rejectionReason");
    } else if (existing.module == "ATTENDANCE") {
      await _db.platformDao.updateAttendanceApprovalStatus(existing.entityId, "REJECTED");
    } else if (existing.module == "DOCTOR_VISIT") {
      await _db.doctorVisitDao.updateVisitApprovalStatus(existing.entityId, "REJECTED");
    }

    await _auditService.logAction(
      actorId: reviewerId,
      actorName: reviewerName,
      actorRole: reviewerRole,
      action: "REJECT_${existing.module}",
      targetEntity: existing.module,
      entityId: existing.entityId,
      oldValue: "PENDING",
      newValue: "REJECTED ($rejectionReason)",
    );

    return true;
  }
}

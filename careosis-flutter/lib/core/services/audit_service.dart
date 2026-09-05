import 'package:intl/intl.dart';
import '../../data/local/entities/admin_and_security_entities.dart';
import '../../data/local/db/careosis_database.dart';

class AuditService {
  final CareOsisDatabase _db;
  AuditService(this._db);

  /// Records an immutable audit log entry
  Future<AuditLog> logAction({
    required String actorId,
    required String actorName,
    required String actorRole,
    required String action,
    required String targetEntity,
    String entityId = "",
    String oldValue = "",
    String newValue = "",
  }) async {
    final timestamp = DateTime.now().millisecondsSinceEpoch;
    final formattedDate = DateFormat('dd MMM yyyy, hh:mm a').format(DateTime.fromMillisecondsSinceEpoch(timestamp));
    final auditId = "AUD-${timestamp.toString().substring(6)}";

    final log = AuditLog(
      auditId: auditId,
      userId: actorId,
      userName: actorName,
      userRole: actorRole,
      action: action,
      targetEntity: targetEntity,
      entityId: entityId,
      oldValue: oldValue,
      newValue: newValue,
      timestamp: timestamp,
      formattedDate: formattedDate,
    );

    _db.adminAndSecurityDao.insertAuditLog(log);
    return log;
  }
}

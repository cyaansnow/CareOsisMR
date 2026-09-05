class NotificationModel {
  final String id;
  final String title;
  final String message;
  final String type; // Visit, FollowUp, Order, Expense, Incentive, Training, Announcement
  final int timestamp;
  final String timeFormatted;
  final bool isRead;
  final String actionRoute;

  const NotificationModel({
    required this.id,
    required this.title,
    required this.message,
    required this.type,
    required this.timestamp,
    required this.timeFormatted,
    this.isRead = false,
    this.actionRoute = "",
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'title': title,
        'message': message,
        'type': type,
        'timestamp': timestamp,
        'timeFormatted': timeFormatted,
        'isRead': isRead ? 1 : 0,
        'actionRoute': actionRoute,
      };

  factory NotificationModel.fromMap(Map<String, dynamic> map) => NotificationModel(
        id: map['id'] as String,
        title: map['title'] as String,
        message: map['message'] as String,
        type: map['type'] as String,
        timestamp: (map['timestamp'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
        timeFormatted: map['timeFormatted'] as String? ?? "",
        isRead: map['isRead'] == 1 || map['isRead'] == true,
        actionRoute: map['actionRoute'] as String? ?? "",
      );
}

class AchievementModel {
  final String id;
  final String title;
  final String description;
  final String iconCategory; // Visits, Sales, Academy, Streak
  final int progress;
  final int maxProgress;
  final bool isUnlocked;
  final String unlockedDate;

  const AchievementModel({
    required this.id,
    required this.title,
    required this.description,
    required this.iconCategory,
    required this.progress,
    required this.maxProgress,
    this.isUnlocked = false,
    this.unlockedDate = "",
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'title': title,
        'description': description,
        'iconCategory': iconCategory,
        'progress': progress,
        'maxProgress': maxProgress,
        'isUnlocked': isUnlocked ? 1 : 0,
        'unlockedDate': unlockedDate,
      };

  factory AchievementModel.fromMap(Map<String, dynamic> map) => AchievementModel(
        id: map['id'] as String,
        title: map['title'] as String,
        description: map['description'] as String,
        iconCategory: map['iconCategory'] as String,
        progress: (map['progress'] as num?)?.toInt() ?? 0,
        maxProgress: (map['maxProgress'] as num?)?.toInt() ?? 100,
        isUnlocked: map['isUnlocked'] == 1 || map['isUnlocked'] == true,
        unlockedDate: map['unlockedDate'] as String? ?? "",
      );
}

class LeaderboardModel {
  final String id;
  final int rank;
  final String mrName;
  final String territory;
  final double sales;
  final double achievementPercent;
  final int visitsCount;
  final int trainingPercent;
  final int points;
  final String period;

  const LeaderboardModel({
    required this.id,
    required this.rank,
    required this.mrName,
    required this.territory,
    required this.sales,
    required this.achievementPercent,
    required this.visitsCount,
    required this.trainingPercent,
    required this.points,
    this.period = "August 2026",
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'rank': rank,
        'mrName': mrName,
        'territory': territory,
        'sales': sales,
        'achievementPercent': achievementPercent,
        'visitsCount': visitsCount,
        'trainingPercent': trainingPercent,
        'points': points,
        'period': period,
      };

  factory LeaderboardModel.fromMap(Map<String, dynamic> map) => LeaderboardModel(
        id: map['id'] as String,
        rank: (map['rank'] as num?)?.toInt() ?? 1,
        mrName: map['mrName'] as String,
        territory: map['territory'] as String,
        sales: (map['sales'] as num?)?.toDouble() ?? 0.0,
        achievementPercent: (map['achievementPercent'] as num?)?.toDouble() ?? 0.0,
        visitsCount: (map['visitsCount'] as num?)?.toInt() ?? 0,
        trainingPercent: (map['trainingPercent'] as num?)?.toInt() ?? 0,
        points: (map['points'] as num?)?.toInt() ?? 0,
        period: map['period'] as String? ?? "August 2026",
      );
}

class SyncQueueModel {
  final int? id;
  final String entityType; // DOCTOR_VISIT, ORDER, EXPENSE, ATTENDANCE, DOCTOR
  final String entityId;
  final String action; // INSERT, UPDATE, DELETE
  final String payloadPreview;
  final String status; // PENDING, SYNCING, SYNCED, FAILED
  final int retryCount;
  final int createdAt;

  const SyncQueueModel({
    this.id,
    required this.entityType,
    required this.entityId,
    required this.action,
    required this.payloadPreview,
    this.status = "PENDING",
    this.retryCount = 0,
    required this.createdAt,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'entityType': entityType,
        'entityId': entityId,
        'action': action,
        'payloadPreview': payloadPreview,
        'status': status,
        'retryCount': retryCount,
        'createdAt': createdAt,
      };

  factory SyncQueueModel.fromMap(Map<String, dynamic> map) => SyncQueueModel(
        id: (map['id'] as num?)?.toInt(),
        entityType: map['entityType'] as String,
        entityId: map['entityId'] as String,
        action: map['action'] as String,
        payloadPreview: map['payloadPreview'] as String,
        status: map['status'] as String? ?? "PENDING",
        retryCount: (map['retryCount'] as num?)?.toInt() ?? 0,
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

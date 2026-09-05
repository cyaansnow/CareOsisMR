class Stockist {
  final String id;
  final String companyName;
  final String contactPerson;
  final String phone;
  final String address;
  final String territory;
  final String gstNumber;
  final double creditLimit;
  final double outstandingAmount;
  final String lastOrderDate;
  final double totalSales;
  final String status;
  final bool isSynced;

  const Stockist({
    required this.id,
    required this.companyName,
    required this.contactPerson,
    required this.phone,
    required this.address,
    required this.territory,
    required this.gstNumber,
    required this.creditLimit,
    required this.outstandingAmount,
    this.lastOrderDate = "",
    this.totalSales = 0.0,
    this.status = "Active",
    this.isSynced = true,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'companyName': companyName,
        'contactPerson': contactPerson,
        'phone': phone,
        'address': address,
        'territory': territory,
        'gstNumber': gstNumber,
        'creditLimit': creditLimit,
        'outstandingAmount': outstandingAmount,
        'lastOrderDate': lastOrderDate,
        'totalSales': totalSales,
        'status': status,
        'isSynced': isSynced ? 1 : 0,
      };

  factory Stockist.fromMap(Map<String, dynamic> map) => Stockist(
        id: map['id'] as String,
        companyName: map['companyName'] as String,
        contactPerson: map['contactPerson'] as String,
        phone: map['phone'] as String,
        address: map['address'] as String,
        territory: map['territory'] as String,
        gstNumber: map['gstNumber'] as String,
        creditLimit: (map['creditLimit'] as num?)?.toDouble() ?? 0.0,
        outstandingAmount: (map['outstandingAmount'] as num?)?.toDouble() ?? 0.0,
        lastOrderDate: map['lastOrderDate'] as String? ?? "",
        totalSales: (map['totalSales'] as num?)?.toDouble() ?? 0.0,
        status: map['status'] as String? ?? "Active",
        isSynced: map['isSynced'] == 1 || map['isSynced'] == true,
      );
}

class Retailer {
  final String id;
  final String shopName;
  final String ownerName;
  final String phone;
  final String address;
  final String stockistName;
  final String territory;
  final String productsStocked;
  final String lastOrderDate;
  final double outstandingAmount;
  final String notes;
  final bool isSynced;

  const Retailer({
    required this.id,
    required this.shopName,
    required this.ownerName,
    required this.phone,
    required this.address,
    required this.stockistName,
    required this.territory,
    required this.productsStocked,
    this.lastOrderDate = "",
    this.outstandingAmount = 0.0,
    this.notes = "",
    this.isSynced = true,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'shopName': shopName,
        'ownerName': ownerName,
        'phone': phone,
        'address': address,
        'stockistName': stockistName,
        'territory': territory,
        'productsStocked': productsStocked,
        'lastOrderDate': lastOrderDate,
        'outstandingAmount': outstandingAmount,
        'notes': notes,
        'isSynced': isSynced ? 1 : 0,
      };

  factory Retailer.fromMap(Map<String, dynamic> map) => Retailer(
        id: map['id'] as String,
        shopName: map['shopName'] as String,
        ownerName: map['ownerName'] as String,
        phone: map['phone'] as String,
        address: map['address'] as String,
        stockistName: map['stockistName'] as String,
        territory: map['territory'] as String,
        productsStocked: map['productsStocked'] as String,
        lastOrderDate: map['lastOrderDate'] as String? ?? "",
        outstandingAmount: (map['outstandingAmount'] as num?)?.toDouble() ?? 0.0,
        notes: map['notes'] as String? ?? "",
        isSynced: map['isSynced'] == 1 || map['isSynced'] == true,
      );
}

class OrderModel {
  final String id;
  final String customerId;
  final String customerName;
  final String customerType; // STOCKIST or RETAILER
  final String mrId;
  final String orderDate;
  final double subtotal;
  final double discountPercent;
  final double discountAmount;
  final double gstAmount;
  final double totalAmount;
  final String itemsSummary;
  final String status; // Draft, Submitted, Approved, Packed, Dispatched, Delivered
  final String notes;
  final bool isSynced;
  final int createdAt;

  const OrderModel({
    required this.id,
    required this.customerId,
    required this.customerName,
    required this.customerType,
    required this.mrId,
    required this.orderDate,
    required this.subtotal,
    this.discountPercent = 0.0,
    this.discountAmount = 0.0,
    required this.gstAmount,
    required this.totalAmount,
    required this.itemsSummary,
    this.status = "Submitted",
    this.notes = "",
    this.isSynced = false,
    required this.createdAt,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'customerId': customerId,
        'customerName': customerName,
        'customerType': customerType,
        'mrId': mrId,
        'orderDate': orderDate,
        'subtotal': subtotal,
        'discountPercent': discountPercent,
        'discountAmount': discountAmount,
        'gstAmount': gstAmount,
        'totalAmount': totalAmount,
        'itemsSummary': itemsSummary,
        'status': status,
        'notes': notes,
        'isSynced': isSynced ? 1 : 0,
        'createdAt': createdAt,
      };

  factory OrderModel.fromMap(Map<String, dynamic> map) => OrderModel(
        id: map['id'] as String,
        customerId: map['customerId'] as String,
        customerName: map['customerName'] as String,
        customerType: map['customerType'] as String,
        mrId: map['mrId'] as String,
        orderDate: map['orderDate'] as String,
        subtotal: (map['subtotal'] as num?)?.toDouble() ?? 0.0,
        discountPercent: (map['discountPercent'] as num?)?.toDouble() ?? 0.0,
        discountAmount: (map['discountAmount'] as num?)?.toDouble() ?? 0.0,
        gstAmount: (map['gstAmount'] as num?)?.toDouble() ?? 0.0,
        totalAmount: (map['totalAmount'] as num?)?.toDouble() ?? 0.0,
        itemsSummary: map['itemsSummary'] as String? ?? "",
        status: map['status'] as String? ?? "Submitted",
        notes: map['notes'] as String? ?? "",
        isSynced: map['isSynced'] == 1 || map['isSynced'] == true,
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

class OrderItemModel {
  final int? id;
  final String orderId;
  final String productId;
  final String productName;
  final int quantity;
  final double mrp;
  final double unitRate;
  final double discountPercent;
  final double totalAmount;

  const OrderItemModel({
    this.id,
    required this.orderId,
    required this.productId,
    required this.productName,
    required this.quantity,
    required this.mrp,
    required this.unitRate,
    this.discountPercent = 0.0,
    required this.totalAmount,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'orderId': orderId,
        'productId': productId,
        'productName': productName,
        'quantity': quantity,
        'mrp': mrp,
        'unitRate': unitRate,
        'discountPercent': discountPercent,
        'totalAmount': totalAmount,
      };

  factory OrderItemModel.fromMap(Map<String, dynamic> map) => OrderItemModel(
        id: (map['id'] as num?)?.toInt(),
        orderId: map['orderId'] as String,
        productId: map['productId'] as String,
        productName: map['productName'] as String,
        quantity: (map['quantity'] as num?)?.toInt() ?? 1,
        mrp: (map['mrp'] as num?)?.toDouble() ?? 0.0,
        unitRate: (map['unitRate'] as num?)?.toDouble() ?? 0.0,
        discountPercent: (map['discountPercent'] as num?)?.toDouble() ?? 0.0,
        totalAmount: (map['totalAmount'] as num?)?.toDouble() ?? 0.0,
      );
}

class ExpenseModel {
  final String id;
  final String date;
  final String category;
  final double amount;
  final String description;
  final String receiptPath;
  final String location;
  final String status;
  final bool isSynced;
  final int createdAt;

  const ExpenseModel({
    required this.id,
    required this.date,
    required this.category,
    required this.amount,
    required this.description,
    this.receiptPath = "",
    this.location = "",
    this.status = "Submitted",
    this.isSynced = false,
    required this.createdAt,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'date': date,
        'category': category,
        'amount': amount,
        'description': description,
        'receiptPath': receiptPath,
        'location': location,
        'status': status,
        'isSynced': isSynced ? 1 : 0,
        'createdAt': createdAt,
      };

  factory ExpenseModel.fromMap(Map<String, dynamic> map) => ExpenseModel(
        id: map['id'] as String,
        date: map['date'] as String,
        category: map['category'] as String,
        amount: (map['amount'] as num?)?.toDouble() ?? 0.0,
        description: map['description'] as String? ?? "",
        receiptPath: map['receiptPath'] as String? ?? "",
        location: map['location'] as String? ?? "",
        status: map['status'] as String? ?? "Submitted",
        isSynced: map['isSynced'] == 1 || map['isSynced'] == true,
        createdAt: (map['createdAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

class AttendanceModel {
  final String id;
  final String date;
  final String checkInTime;
  final String checkOutTime;
  final String workingHours;
  final int visitsCompleted;
  final String status;
  final String checkInLocation;
  final String checkOutLocation;
  final double checkInLatitude;
  final double checkInLongitude;
  final double checkOutLatitude;
  final double checkOutLongitude;
  final bool isSynced;

  const AttendanceModel({
    required this.id,
    required this.date,
    required this.checkInTime,
    this.checkOutTime = "",
    this.workingHours = "",
    this.visitsCompleted = 0,
    this.status = "Present",
    this.checkInLocation = "Assigned Field Territory",
    this.checkOutLocation = "",
    this.checkInLatitude = 0.0,
    this.checkInLongitude = 0.0,
    this.checkOutLatitude = 0.0,
    this.checkOutLongitude = 0.0,
    this.isSynced = false,
  });

  AttendanceModel copyWith({
    String? id,
    String? date,
    String? checkInTime,
    String? checkOutTime,
    String? workingHours,
    int? visitsCompleted,
    String? status,
    String? checkInLocation,
    String? checkOutLocation,
    double? checkInLatitude,
    double? checkInLongitude,
    double? checkOutLatitude,
    double? checkOutLongitude,
    bool? isSynced,
  }) {
    return AttendanceModel(
      id: id ?? this.id,
      date: date ?? this.date,
      checkInTime: checkInTime ?? this.checkInTime,
      checkOutTime: checkOutTime ?? this.checkOutTime,
      workingHours: workingHours ?? this.workingHours,
      visitsCompleted: visitsCompleted ?? this.visitsCompleted,
      status: status ?? this.status,
      checkInLocation: checkInLocation ?? this.checkInLocation,
      checkOutLocation: checkOutLocation ?? this.checkOutLocation,
      checkInLatitude: checkInLatitude ?? this.checkInLatitude,
      checkInLongitude: checkInLongitude ?? this.checkInLongitude,
      checkOutLatitude: checkOutLatitude ?? this.checkOutLatitude,
      checkOutLongitude: checkOutLongitude ?? this.checkOutLongitude,
      isSynced: isSynced ?? this.isSynced,
    );
  }

  Map<String, dynamic> toMap() => {
        'id': id,
        'date': date,
        'checkInTime': checkInTime,
        'checkOutTime': checkOutTime,
        'workingHours': workingHours,
        'visitsCompleted': visitsCompleted,
        'status': status,
        'checkInLocation': checkInLocation,
        'checkOutLocation': checkOutLocation,
        'checkInLatitude': checkInLatitude,
        'checkInLongitude': checkInLongitude,
        'checkOutLatitude': checkOutLatitude,
        'checkOutLongitude': checkOutLongitude,
        'isSynced': isSynced ? 1 : 0,
      };

  factory AttendanceModel.fromMap(Map<String, dynamic> map) => AttendanceModel(
        id: map['id'] as String,
        date: map['date'] as String,
        checkInTime: map['checkInTime'] as String,
        checkOutTime: map['checkOutTime'] as String? ?? "",
        workingHours: map['workingHours'] as String? ?? "",
        visitsCompleted: (map['visitsCompleted'] as num?)?.toInt() ?? 0,
        status: map['status'] as String? ?? "Present",
        checkInLocation: map['checkInLocation'] as String? ?? "Assigned Field Territory",
        checkOutLocation: map['checkOutLocation'] as String? ?? "",
        checkInLatitude: (map['checkInLatitude'] as num?)?.toDouble() ?? 0.0,
        checkInLongitude: (map['checkInLongitude'] as num?)?.toDouble() ?? 0.0,
        checkOutLatitude: (map['checkOutLatitude'] as num?)?.toDouble() ?? 0.0,
        checkOutLongitude: (map['checkOutLongitude'] as num?)?.toDouble() ?? 0.0,
        isSynced: map['isSynced'] == 1 || map['isSynced'] == true,
      );

  static String calculateWorkingDuration(DateTime checkIn, DateTime checkOut) {
    final diff = checkOut.difference(checkIn);
    final hours = diff.inHours;
    final minutes = diff.inMinutes % 60;
    if (hours == 0) {
      return "${minutes}m";
    }
    return "${hours}h ${minutes}m";
  }
}

class RoutePlanModel {
  final String id;
  final String date;
  final String title;
  final int doctorCount;
  final int retailerCount;
  final int stockistCount;
  final String stopsListText;
  final String status;

  const RoutePlanModel({
    required this.id,
    required this.date,
    required this.title,
    required this.doctorCount,
    required this.retailerCount,
    required this.stockistCount,
    required this.stopsListText,
    this.status = "In-Progress",
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'date': date,
        'title': title,
        'doctorCount': doctorCount,
        'retailerCount': retailerCount,
        'stockistCount': stockistCount,
        'stopsListText': stopsListText,
        'status': status,
      };

  factory RoutePlanModel.fromMap(Map<String, dynamic> map) => RoutePlanModel(
        id: map['id'] as String,
        date: map['date'] as String,
        title: map['title'] as String,
        doctorCount: (map['doctorCount'] as num?)?.toInt() ?? 0,
        retailerCount: (map['retailerCount'] as num?)?.toInt() ?? 0,
        stockistCount: (map['stockistCount'] as num?)?.toInt() ?? 0,
        stopsListText: map['stopsListText'] as String? ?? "",
        status: map['status'] as String? ?? "In-Progress",
      );
}

class FollowUpModel {
  final String id;
  final String personName;
  final String personType;
  final String relatedId;
  final String followUpDate;
  final String reason;
  final String priority;
  final String notes;
  final String status;
  final bool isSynced;

  const FollowUpModel({
    required this.id,
    required this.personName,
    required this.personType,
    required this.relatedId,
    required this.followUpDate,
    required this.reason,
    this.priority = "Medium",
    this.notes = "",
    this.status = "Pending",
    this.isSynced = true,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'personName': personName,
        'personType': personType,
        'relatedId': relatedId,
        'followUpDate': followUpDate,
        'reason': reason,
        'priority': priority,
        'notes': notes,
        'status': status,
        'isSynced': isSynced ? 1 : 0,
      };

  factory FollowUpModel.fromMap(Map<String, dynamic> map) => FollowUpModel(
        id: map['id'] as String,
        personName: map['personName'] as String,
        personType: map['personType'] as String,
        relatedId: map['relatedId'] as String,
        followUpDate: map['followUpDate'] as String,
        reason: map['reason'] as String,
        priority: map['priority'] as String? ?? "Medium",
        notes: map['notes'] as String? ?? "",
        status: map['status'] as String? ?? "Pending",
        isSynced: map['isSynced'] == 1 || map['isSynced'] == true,
      );
}

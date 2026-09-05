class ProductModel {
  final String id;
  final String name;
  final String brand;
  final String category;
  final double mrp;
  final double retailerRate;
  final String packaging;
  final String composition;
  final String indications;
  final String keyBenefits;
  final String mechanismOfAction;
  final String dosage;
  final String mrPitch;
  final String importantTalkingPoints;
  final String clinicalEvidence;
  final String competitorInfo;
  final String videoTitle;
  final String videoDuration;
  final bool isFocusProduct;

  const ProductModel({
    required this.id,
    required this.name,
    this.brand = "CareOsis",
    required this.category,
    required this.mrp,
    required this.retailerRate,
    required this.packaging,
    required this.composition,
    required this.indications,
    required this.keyBenefits,
    required this.mechanismOfAction,
    required this.dosage,
    required this.mrPitch,
    required this.importantTalkingPoints,
    required this.clinicalEvidence,
    required this.competitorInfo,
    this.videoTitle = "Product Masterclass",
    this.videoDuration = "4m 30s",
    this.isFocusProduct = false,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'name': name,
        'brand': brand,
        'category': category,
        'mrp': mrp,
        'retailerRate': retailerRate,
        'packaging': packaging,
        'composition': composition,
        'indications': indications,
        'keyBenefits': keyBenefits,
        'mechanismOfAction': mechanismOfAction,
        'dosage': dosage,
        'mrPitch': mrPitch,
        'importantTalkingPoints': importantTalkingPoints,
        'clinicalEvidence': clinicalEvidence,
        'competitorInfo': competitorInfo,
        'videoTitle': videoTitle,
        'videoDuration': videoDuration,
        'isFocusProduct': isFocusProduct ? 1 : 0,
      };

  factory ProductModel.fromMap(Map<String, dynamic> map) => ProductModel(
        id: map['id'] as String,
        name: map['name'] as String,
        brand: map['brand'] as String? ?? "CareOsis",
        category: map['category'] as String,
        mrp: (map['mrp'] as num?)?.toDouble() ?? 0.0,
        retailerRate: (map['retailerRate'] as num?)?.toDouble() ?? 0.0,
        packaging: map['packaging'] as String,
        composition: map['composition'] as String,
        indications: map['indications'] as String,
        keyBenefits: map['keyBenefits'] as String,
        mechanismOfAction: map['mechanismOfAction'] as String,
        dosage: map['dosage'] as String,
        mrPitch: map['mrPitch'] as String,
        importantTalkingPoints: map['importantTalkingPoints'] as String,
        clinicalEvidence: map['clinicalEvidence'] as String,
        competitorInfo: map['competitorInfo'] as String,
        videoTitle: map['videoTitle'] as String? ?? "Product Masterclass",
        videoDuration: map['videoDuration'] as String? ?? "4m 30s",
        isFocusProduct: map['isFocusProduct'] == 1 || map['isFocusProduct'] == true,
      );
}

class TrainingProgressModel {
  final String productId;
  final String productName;
  final String category;
  final bool dossierRead;
  final bool videoWatched;
  final int quizScore;
  final bool isCompleted;
  final int completionPercentage;
  final int lastAccessedAt;

  const TrainingProgressModel({
    required this.productId,
    required this.productName,
    required this.category,
    this.dossierRead = false,
    this.videoWatched = false,
    this.quizScore = 0,
    this.isCompleted = false,
    this.completionPercentage = 0,
    required this.lastAccessedAt,
  });

  Map<String, dynamic> toMap() => {
        'productId': productId,
        'productName': productName,
        'category': category,
        'dossierRead': dossierRead ? 1 : 0,
        'videoWatched': videoWatched ? 1 : 0,
        'quizScore': quizScore,
        'isCompleted': isCompleted ? 1 : 0,
        'completionPercentage': completionPercentage,
        'lastAccessedAt': lastAccessedAt,
      };

  factory TrainingProgressModel.fromMap(Map<String, dynamic> map) => TrainingProgressModel(
        productId: map['productId'] as String,
        productName: map['productName'] as String,
        category: map['category'] as String,
        dossierRead: map['dossierRead'] == 1 || map['dossierRead'] == true,
        videoWatched: map['videoWatched'] == 1 || map['videoWatched'] == true,
        quizScore: (map['quizScore'] as num?)?.toInt() ?? 0,
        isCompleted: map['isCompleted'] == 1 || map['isCompleted'] == true,
        completionPercentage: (map['completionPercentage'] as num?)?.toInt() ?? 0,
        lastAccessedAt: (map['lastAccessedAt'] as num?)?.toInt() ?? DateTime.now().millisecondsSinceEpoch,
      );
}

class AssessmentQuestionModel {
  final String id;
  final String productId;
  final String questionText;
  final String optionA;
  final String optionB;
  final String optionC;
  final String optionD;
  final int correctOptionIndex; // 0, 1, 2, 3
  final String explanation;

  const AssessmentQuestionModel({
    required this.id,
    required this.productId,
    required this.questionText,
    required this.optionA,
    required this.optionB,
    required this.optionC,
    required this.optionD,
    required this.correctOptionIndex,
    required this.explanation,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'productId': productId,
        'questionText': questionText,
        'optionA': optionA,
        'optionB': optionB,
        'optionC': optionC,
        'optionD': optionD,
        'correctOptionIndex': correctOptionIndex,
        'explanation': explanation,
      };

  factory AssessmentQuestionModel.fromMap(Map<String, dynamic> map) => AssessmentQuestionModel(
        id: map['id'] as String,
        productId: map['productId'] as String,
        questionText: map['questionText'] as String,
        optionA: map['optionA'] as String,
        optionB: map['optionB'] as String,
        optionC: map['optionC'] as String,
        optionD: map['optionD'] as String,
        correctOptionIndex: (map['correctOptionIndex'] as num?)?.toInt() ?? 0,
        explanation: map['explanation'] as String? ?? "",
      );
}

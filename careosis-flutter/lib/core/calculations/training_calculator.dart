class TrainingProgressCalculator {
  static (String, String) calculateMrLevel(int overallTrainingPercent) {
    if (overallTrainingPercent < 10) {
      return ("Newbie", "Getting Started");
    } else if (overallTrainingPercent < 25) {
      return ("Beginner", "Foundation Phase");
    } else if (overallTrainingPercent < 50) {
      return ("Intermediate", "Field Ready");
    } else if (overallTrainingPercent < 90) {
      return ("Expert MR", "High Performer");
    } else if (overallTrainingPercent < 100) {
      return ("Advanced MR", "Elite Representative");
    } else {
      return ("CareOsis Master MR", "Master Field Champion");
    }
  }

  static int calculateProductProgress({
    required bool dossierRead,
    required bool videoWatched,
    required int quizScore,
  }) {
    int score = 0;
    if (dossierRead) score += 40;
    if (videoWatched) score += 20;
    score += (quizScore.clamp(0, 100) * 0.40).toInt();
    return score.clamp(0, 100);
  }
}

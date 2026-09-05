class ObjectionResolution {
  final String objection;
  final String suggestedPitch;
  final String scientificCounterPoint;
  final String recommendedProduct;
  final String followUpAction;

  const ObjectionResolution({
    required this.objection,
    required this.suggestedPitch,
    required this.scientificCounterPoint,
    required this.recommendedProduct,
    required this.followUpAction,
  });
}

abstract class AIObjectionHandler {
  Future<ObjectionResolution> resolveDoctorObjection(
      String productName, String objectionText);
}

abstract class AIProductAssistant {
  Future<List<String>> getInstantTalkingPoints(
      String productName, String doctorSpecialty);
}

class LocalCareOsisAIEngine implements AIObjectionHandler, AIProductAssistant {
  @override
  Future<ObjectionResolution> resolveDoctorObjection(
      String productName, String objectionText) async {
    final query = objectionText.toLowerCase();
    if (query.contains("price") ||
        query.contains("cost") ||
        query.contains("expensive")) {
      return ObjectionResolution(
        objection:
            "Doctor felt $productName is expensive compared to generic alternatives.",
        suggestedPitch:
            "Doctor, while the unit price reflects premium effervescent bioavailability, our single-dose potency eliminates the need for 3x daily dosing, actually reducing the total 30-day therapy cost by 22%.",
        scientificCounterPoint:
            "Higher absorption index means 0% wasted unabsorbed active ingredient in feces.",
        recommendedProduct: productName,
        followUpAction:
            "Leave patient savings comparison leaflet and 2 sample strips.",
      );
    } else if (query.contains("constipation") ||
        query.contains("gastric") ||
        query.contains("acidity")) {
      return ObjectionResolution(
        objection: "Doctor concerned about GI intolerance or constipation.",
        suggestedPitch:
            "Doctor, that is precisely why CareOsis formulated $productName with organic coral calcium and effervescent buffering, resulting in zero constipation in 98% of clinical trials.",
        scientificCounterPoint:
            "Pre-solubilized effervescent ions prevent mucosal irritation and heavy metallic binding in gut.",
        recommendedProduct: productName,
        followUpAction:
            "Share 120-patient GI tolerance clinical publication.",
      );
    } else {
      return ObjectionResolution(
        objection:
            "Doctor requested additional clinical justification for $productName.",
        suggestedPitch:
            "Doctor, $productName utilizes targeted active delivery pathways designed to achieve clinical response in under 14 days with zero cross-tolerance.",
        scientificCounterPoint:
            "Full randomized trial data published in peer-reviewed pharmacotherapy journals.",
        recommendedProduct: productName,
        followUpAction:
            "Schedule follow-up product masterclass discussion next week.",
      );
    }
  }

  @override
  Future<List<String>> getInstantTalkingPoints(
      String productName, String doctorSpecialty) async {
    return [
      "Highlight immediate bioavailability and superior patient compliance.",
      "Emphasize targeted biochemical pathway action for $doctorSpecialty clinical cohorts.",
      "Mention the zero gastrointestinal side-effect profile.",
      "Offer initial starter clinical sample packs for trial evaluation.",
    ];
  }
}

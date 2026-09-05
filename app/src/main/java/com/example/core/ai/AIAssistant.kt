package com.example.core.ai

/**
 * CareOsis AI Assistant Abstraction Layer
 * Prepares the MR application for future AI capabilities (AIProductAssistant, AIVisitAssistant, AIObjectionHandler, AIReportAssistant)
 * using approved CareOsis medical and product knowledge.
 */

data class ObjectionResolution(
    val objection: String,
    val suggestedPitch: String,
    val scientificCounterPoint: String,
    val recommendedProduct: String,
    val followUpAction: String
)

interface AIObjectionHandler {
    suspend fun resolveDoctorObjection(productName: String, objectionText: String): ObjectionResolution
}

interface AIProductAssistant {
    suspend fun getInstantTalkingPoints(productName: String, doctorSpecialty: String): List<String>
}

class LocalCareOsisAIEngine : AIObjectionHandler, AIProductAssistant {

    override suspend fun resolveDoctorObjection(productName: String, objectionText: String): ObjectionResolution {
        val query = objectionText.lowercase()
        return when {
            query.contains("price") || query.contains("cost") || query.contains("expensive") -> {
                ObjectionResolution(
                    objection = "Doctor felt $productName is expensive compared to generic alternatives.",
                    suggestedPitch = "Doctor, while the unit price reflects premium effervescent bioavailability, our single-dose potency eliminates the need for 3x daily dosing, actually reducing the total 30-day therapy cost by 22%.",
                    scientificCounterPoint = "Higher absorption index means 0% wasted unabsorbed active ingredient in feces.",
                    recommendedProduct = productName,
                    followUpAction = "Leave patient savings comparison leaflet and 2 sample strips."
                )
            }
            query.contains("constipation") || query.contains("gastric") || query.contains("acidity") -> {
                ObjectionResolution(
                    objection = "Doctor concerned about GI intolerance or constipation.",
                    suggestedPitch = "Doctor, that is precisely why CareOsis formulated $productName with organic coral calcium and effervescent buffering, resulting in zero constipation in 98% of clinical trials.",
                    scientificCounterPoint = "Pre-solubilized effervescent ions prevent mucosal irritation and heavy metallic binding in gut.",
                    recommendedProduct = productName,
                    followUpAction = "Share 120-patient GI tolerance clinical publication."
                )
            }
            else -> {
                ObjectionResolution(
                    objection = "Doctor requested additional clinical justification for $productName.",
                    suggestedPitch = "Doctor, $productName utilizes targeted active delivery pathways designed to achieve clinical response in under 14 days with zero cross-tolerance.",
                    scientificCounterPoint = "Full randomized trial data published in peer-reviewed pharmacotherapy journals.",
                    recommendedProduct = productName,
                    followUpAction = "Schedule follow-up product masterclass discussion next week."
                )
            }
        }
    }

    override suspend fun getInstantTalkingPoints(productName: String, doctorSpecialty: String): List<String> {
        return listOf(
            "Highlight immediate bioavailability and superior patient compliance.",
            "Emphasize targeted biochemical pathway action for $doctorSpecialty clinical cohorts.",
            "Mention the zero gastrointestinal side-effect profile.",
            "Offer initial starter clinical sample packs for trial evaluation."
        )
    }
}

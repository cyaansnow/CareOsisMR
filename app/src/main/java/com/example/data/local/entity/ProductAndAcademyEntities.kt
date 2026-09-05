package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String = "CareOsis",
    val category: String, // Nutraceutical, Antibiotic, Gastro, Ortho, Cardio, Pain Management, General
    val mrp: Double,
    val retailerRate: Double,
    val packaging: String, // e.g., "10x1x10 Effervescent Tablets"
    val composition: String,
    val indications: String,
    val keyBenefits: String,
    val mechanismOfAction: String,
    val dosage: String,
    val mrPitch: String,
    val importantTalkingPoints: String,
    val clinicalEvidence: String,
    val competitorInfo: String,
    val videoTitle: String = "Product Masterclass",
    val videoDuration: String = "4m 30s",
    val isFocusProduct: Boolean = false
)

@Entity(tableName = "training_progress")
data class TrainingProgressEntity(
    @PrimaryKey val productId: String,
    val productName: String,
    val category: String,
    val dossierRead: Boolean = false,
    val videoWatched: Boolean = false,
    val quizScore: Int = 0,
    val isCompleted: Boolean = false,
    val completionPercentage: Int = 0,
    val lastAccessedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "assessment_questions")
data class AssessmentQuestionEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0, 1, 2, 3
    val explanation: String
)

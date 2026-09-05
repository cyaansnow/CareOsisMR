package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mr_profiles")
data class MRProfileEntity(
    @PrimaryKey val empId: String,
    val name: String,
    val phone: String,
    val email: String,
    val territory: String,
    val managerName: String,
    val joiningDate: String,
    val designation: String,
    val level: String, // Newbie, Beginner, Intermediate, Expert, Advanced, CareOsis Master MR
    val trainingProgressPercent: Int,
    val monthlyTarget: Double,
    val monthlySales: Double,
    val currentIncentive: Double,
    val photoUrl: String = "",
    val isCheckedInToday: Boolean = false,
    val checkInTime: String = "",
    val completedVisitsToday: Int = 0,
    val targetVisitsToday: Int = 15
)

@Entity(tableName = "doctors")
data class DoctorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val specialty: String,
    val qualification: String,
    val clinicHospital: String,
    val address: String,
    val phone: String,
    val email: String,
    val preferredVisitingTime: String,
    val birthday: String = "",
    val anniversary: String = "",
    val potentialCategory: String = "A", // A, B, C
    val priority: String = "High", // High, Medium, Low
    val notes: String = "",
    val lastVisitDate: String = "",
    val nextFollowUpDate: String = "",
    val productsDiscussed: String = "",
    val isSynced: Boolean = true
)

@Entity(tableName = "doctor_visits")
data class DoctorVisitEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val doctorName: String,
    val clinicName: String,
    val startTime: String,
    val endTime: String = "",
    val visitDate: String,
    val purpose: String, // New Product Introduction, Follow-up, Product Reminder, Sample Follow-up, Prescription Discussion, Relationship Visit, Complaint Resolution
    val doctorResponse: String = "Positive", // Positive, Neutral, Negative, Interested, Needs Follow-up
    val prescriptionPotential: String = "High", // High, Medium, Low
    val samplesGiven: String = "", // JSON or formatted text e.g. "Booster (2 strips), Calci Fizz (1 pack)"
    val productsDiscussed: String = "",
    val nextFollowUpDate: String = "",
    val notes: String = "",
    val status: String = "Completed", // Completed, In-Progress, Scheduled, Rescheduled
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

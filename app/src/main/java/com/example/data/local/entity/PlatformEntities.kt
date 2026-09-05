package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // Visit, FollowUp, Order, Expense, Incentive, Training, Announcement
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String,
    val isRead: Boolean = false,
    val actionRoute: String = ""
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconCategory: String, // Visits, Sales, Academy, Streak
    val progress: Int,
    val maxProgress: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: String = ""
)

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
    @PrimaryKey val id: String,
    val rank: Int,
    val mrName: String,
    val territory: String,
    val sales: Double,
    val achievementPercent: Double,
    val visitsCount: Int,
    val trainingPercent: Int,
    val points: Int,
    val period: String = "August 2026"
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String, // DOCTOR_VISIT, ORDER, EXPENSE, ATTENDANCE, DOCTOR
    val entityId: String,
    val action: String, // INSERT, UPDATE
    val payloadPreview: String,
    val status: String = "PENDING", // PENDING, SYNCING, SYNCED, FAILED
    val retryCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

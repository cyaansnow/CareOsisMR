package com.example.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        MRProfileEntity::class,
        DoctorEntity::class,
        DoctorVisitEntity::class,
        ProductEntity::class,
        TrainingProgressEntity::class,
        AssessmentQuestionEntity::class,
        StockistEntity::class,
        RetailerEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        ExpenseEntity::class,
        AttendanceEntity::class,
        RoutePlanEntity::class,
        FollowUpEntity::class,
        NotificationEntity::class,
        AchievementEntity::class,
        LeaderboardEntity::class,
        SyncQueueEntity::class,
        UserAccountEntity::class,
        RegionEntity::class,
        IncentiveRuleEntity::class,
        SalaryRuleEntity::class,
        AuditLogEntity::class,
        CalculationSnapshotEntity::class,
        IncentiveRecordEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CareOsisDatabase : RoomDatabase() {
    abstract fun mrProfileDao(): MRProfileDao
    abstract fun doctorDao(): DoctorDao
    abstract fun doctorVisitDao(): DoctorVisitDao
    abstract fun productDao(): ProductDao
    abstract fun academyDao(): AcademyDao
    abstract fun commercialDao(): CommercialDao
    abstract fun platformDao(): PlatformDao
    abstract fun adminAndSecurityDao(): AdminAndSecurityDao

    companion object {
        @Volatile
        private var INSTANCE: CareOsisDatabase? = null

        fun getDatabase(context: Context): CareOsisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CareOsisDatabase::class.java,
                    "careosis_mr_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

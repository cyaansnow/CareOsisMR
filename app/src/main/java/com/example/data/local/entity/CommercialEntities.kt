package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stockists")
data class StockistEntity(
    @PrimaryKey val id: String,
    val companyName: String,
    val contactPerson: String,
    val phone: String,
    val address: String,
    val territory: String,
    val gstNumber: String,
    val creditLimit: Double,
    val outstandingAmount: Double,
    val lastOrderDate: String = "",
    val totalSales: Double = 0.0,
    val status: String = "Active",
    val isSynced: Boolean = true
)

@Entity(tableName = "retailers")
data class RetailerEntity(
    @PrimaryKey val id: String,
    val shopName: String,
    val ownerName: String,
    val phone: String,
    val address: String,
    val stockistName: String,
    val territory: String,
    val productsStocked: String,
    val lastOrderDate: String = "",
    val outstandingAmount: Double = 0.0,
    val notes: String = "",
    val isSynced: Boolean = true
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val customerName: String,
    val customerType: String, // STOCKIST or RETAILER
    val mrId: String,
    val orderDate: String,
    val subtotal: Double,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val gstAmount: Double,
    val totalAmount: Double,
    val itemsSummary: String, // e.g. "Booster (10), Calci Fizz (5)"
    val status: String = "Submitted", // Draft, Submitted, Approved, Packed, Dispatched, Delivered
    val notes: String = "",
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val mrp: Double,
    val unitRate: Double,
    val discountPercent: Double = 0.0,
    val totalAmount: Double
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val date: String,
    val category: String, // Travel, Fuel, Public Transport, Food, Hotel, Parking, Other
    val amount: Double,
    val description: String,
    val receiptPath: String = "",
    val location: String = "",
    val status: String = "Submitted", // Draft, Submitted, Approved, Rejected, Paid
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey val id: String, // usually date e.g. "2026-08-19"
    val date: String,
    val checkInTime: String,
    val checkOutTime: String = "",
    val workingHours: String = "",
    val visitsCompleted: Int = 0,
    val status: String = "Present", // Present, Half-Day, On-Leave
    val checkInLocation: String = "Assigned Field Territory",
    val isSynced: Boolean = false
)

@Entity(tableName = "route_plans")
data class RoutePlanEntity(
    @PrimaryKey val id: String,
    val date: String,
    val title: String,
    val doctorCount: Int,
    val retailerCount: Int,
    val stockistCount: Int,
    val stopsListText: String, // Formatted list of visits for the day
    val status: String = "In-Progress" // Planned, In-Progress, Completed
)

@Entity(tableName = "follow_ups")
data class FollowUpEntity(
    @PrimaryKey val id: String,
    val personName: String,
    val personType: String, // Doctor, Retailer, Stockist
    val relatedId: String,
    val followUpDate: String,
    val reason: String,
    val priority: String = "Medium", // High, Medium, Low
    val notes: String = "",
    val status: String = "Pending", // Pending, Completed, Missed, Rescheduled
    val isSynced: Boolean = true
)

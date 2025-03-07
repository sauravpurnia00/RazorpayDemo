package com.online.razorpaydemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val razorpayPaymentID: String?,  // Nullable as it may be null in failure cases
    val amount: Int,
    val date: String,
    val status: String,
    val message: String
)

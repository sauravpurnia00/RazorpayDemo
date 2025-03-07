package com.online.razorpaydemo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.online.razorpaydemo.data.model.TransactionEntity

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>
}
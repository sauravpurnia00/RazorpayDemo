package com.online.razorpaydemo.ui.history

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.online.razorpaydemo.R
import com.online.razorpaydemo.data.database.AppDatabase
import com.online.razorpaydemo.data.model.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionHistoryActivity : AppCompatActivity() {

    private lateinit var transactionListView: ListView
    private lateinit var adapter: TransactionAdapter
    private val transactionList = mutableListOf<TransactionEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        transactionListView = findViewById(R.id.transactionListView)
        adapter = TransactionAdapter(this, transactionList)
        transactionListView.adapter = adapter

        loadTransactions()
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val transactions = AppDatabase.getInstance(this@TransactionHistoryActivity)
                .transactionDao()
                .getAllTransactions()

            withContext(Dispatchers.Main) {
                if (transactions.isNotEmpty()) {
                    transactionList.clear()
                    transactionList.addAll(transactions)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@TransactionHistoryActivity, "No transactions found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
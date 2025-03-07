package com.online.razorpaydemo.ui.history

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.online.razorpaydemo.R
import com.online.razorpaydemo.data.model.TransactionEntity

class TransactionAdapter(private val context: Context, private var transactions: List<TransactionEntity>) :
    BaseAdapter() {

    override fun getCount(): Int = transactions.size
    override fun getItem(position: Int): Any = transactions[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val transaction = transactions[position]
        println("TransactionAdapter Debug -> Position: $position, Payment ID: ${transaction.razorpayPaymentID}, Amount: ${transaction.amount}, Status: ${transaction.status}")

        holder.tvTransactionTitle.text = "Payment ID: ${transaction.razorpayPaymentID ?: "N/A"}"
        holder.tvTransactionDate.text = "Date: ${transaction.date}"
        holder.tvTransactionStatus.text = "Status: ${transaction.status}"
        holder.tvTransactionAmount.text = "₹${transaction.amount}"

        // Change text color based on status
        if (transaction.status.equals("failure", ignoreCase = true)) {
            holder.tvTransactionStatus.setTextColor(Color.RED)
        } else {
            holder.tvTransactionStatus.setTextColor(Color.GREEN)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val tvTransactionTitle: TextView = view.findViewById(R.id.tvTransactionTitle)
        val tvTransactionDate: TextView = view.findViewById(R.id.tvTransactionDate)
        val tvTransactionStatus: TextView = view.findViewById(R.id.tvTransactionStatus)
        val tvTransactionAmount: TextView = view.findViewById(R.id.tvTransactionAmount)
    }
}

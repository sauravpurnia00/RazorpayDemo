package com.online.razorpaydemo.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.online.razorpaydemo.R
import com.online.razorpaydemo.data.database.AppDatabase
import com.online.razorpaydemo.data.model.TransactionEntity
import com.online.razorpaydemo.utils.Constants
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var db: AppDatabase
    private lateinit var progressBar: ProgressBar
    private lateinit var btnPay: Button
    private lateinit var etAmount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getInstance(this)
        progressBar = findViewById(R.id.progressBar)
        btnPay = findViewById(R.id.btnPay)
        etAmount = findViewById(R.id.etAmount)

        etAmount.setText(getSavedAmount().takeIf { it > 0 }?.toString() ?: "")

        btnPay.setOnClickListener {
            val amount = etAmount.text.toString().trim()
            if (amount.isEmpty()) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            } else {
                saveAmountToPreferences(amount.toInt())

                progressBar.visibility = View.VISIBLE
                btnPay.isEnabled = false

                startPayment(amount.toInt())
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        progressBar.visibility = View.GONE
        btnPay.isEnabled = true

        clearSavedAmount()
        saveTransaction(razorpayPaymentID, etAmount.text.toString().toInt(), "Success", "Payment Successful")
        navigateToResultScreen("Payment Successful: $razorpayPaymentID", "Success")
    }

    override fun onPaymentError(code: Int, response: String?) {
        // Hide ProgressBar and enable button
        progressBar.visibility = View.GONE
        btnPay.isEnabled = true

        val errorMessage = response ?: "Unknown error"
        saveTransaction(null, etAmount.text.toString().toInt(), "Failure", errorMessage)

        val intent = Intent(this, FailureActivity::class.java).apply {
            putExtra("message", "Please Try Again")
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finishAffinity()
    }

    private fun saveTransaction(paymentID: String?, amount: Int, status: String, message: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        lifecycleScope.launch(Dispatchers.IO) {
            val transaction = TransactionEntity(
                razorpayPaymentID = paymentID ?: "N/A (Not Successful)",
                amount = amount,
                date = currentDate,
                status = status,
                message = message
            )
            db.transactionDao().insertTransaction(transaction)
        }
    }

    private fun navigateToResultScreen(message: String, status: String) {
        val intent = Intent(this, SuccessActivity::class.java).apply {
            putExtra("message", message)
            putExtra("status", status)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finishAffinity()
    }

    private fun saveAmountToPreferences(amount: Int) {
        val sharedPreferences = getSharedPreferences("PaymentPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putInt("lastAmount", amount).apply()
    }

    private fun getSavedAmount(): Int {
        val sharedPreferences = getSharedPreferences("PaymentPrefs", MODE_PRIVATE)
        return sharedPreferences.getInt("lastAmount", 0) // Default to 0 if not found
    }

    private fun clearSavedAmount() {
        val sharedPreferences = getSharedPreferences("PaymentPrefs", MODE_PRIVATE)
        sharedPreferences.edit().remove("lastAmount").apply()
    }

    private fun startPayment(amount: Int) {
        val checkout = Checkout()
        checkout.setKeyID(Constants.RAZORPAY_KEY_ID)

        val options = JSONObject().apply {
            put("name", "Demo Merchant")
            put("description", "Test Payment")
            put("currency", "INR")
            put("amount", amount * 100)
            put("method", "upi")
            put("send_sms_hash", true)
            put("allow_rotation", true)
            put("remember_customer", true)
            put("recurring", false)
        }

        val prefill = JSONObject().apply {
            put("email", "gaurav.kumar@example.com")
            put("contact", "7050671218")
        }

        options.put("prefill", prefill)

        try {
            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Payment failed: ${e.message}", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            btnPay.isEnabled = true
        }
    }
}

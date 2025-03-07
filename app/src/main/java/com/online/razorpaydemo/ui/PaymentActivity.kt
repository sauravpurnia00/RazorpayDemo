package com.online.razorpaydemo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.online.razorpaydemo.R
import com.online.razorpaydemo.utils.Constants
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnPay = findViewById<Button>(R.id.btnPay)

        btnPay.setOnClickListener {
            val amount = etAmount.text.toString().trim()
            if (amount.isEmpty()) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            } else {
                startPayment(amount.toInt())
            }
        }
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
        checkout.open(this, options)
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        navigateToSuccessScreen("Payment Successful: $razorpayPaymentID")
    }

    override fun onPaymentError(code: Int, response: String?) {
        if (code == Checkout.PAYMENT_CANCELED) {
            Toast.makeText(this, "Payment Cancelled by User", Toast.LENGTH_SHORT).show()
        } else {
            navigateToSuccessScreen("Payment Failed: $response")
        }
    }

    private fun navigateToSuccessScreen(message: String) {
        val intent = Intent(this, SuccessActivity::class.java).apply {
            putExtra("message", message)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        // Finish all previous activities to avoid flashing issue
        finishAffinity()
    }
}
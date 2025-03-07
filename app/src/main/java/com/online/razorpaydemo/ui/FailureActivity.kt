package com.online.razorpaydemo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.online.razorpaydemo.R

class FailureActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_failure)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val statusMessage = intent.getStringExtra("message") ?: "Payment Failed"
        findViewById<TextView>(R.id.tvFailureMessage).text = statusMessage
        val btnRetry = findViewById<Button>(R.id.btnRetry)

        btnRetry.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
            finish()
        }
        val btnMoveToHome = findViewById<Button>(R.id.btnMoveToHome)
        btnMoveToHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
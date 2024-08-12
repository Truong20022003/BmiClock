package com.example.bmi

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val bmiGaugeView = findViewById<BMIGaugeView>(R.id.bmi)
        bmiGaugeView.setBMI(10f)

        val edtNhap = findViewById<EditText>(R.id.edtNhap)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val bmiString = edtNhap.text.toString()
            if (bmiString.isNotEmpty()) {
                val bmi = bmiString.toFloat()
                bmiGaugeView.setBMI(bmi)
            }
        }
    }


}
package com.example.appconnect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main10.*

class MainActivity10 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main10)

        val sharedPreferences = getSharedPreferences("User_Info", Context.MODE_PRIVATE)

        val name =sharedPreferences.getString("NAME","")

        textView16.text = "Name: $name"

        button4.setOnClickListener {



        }

    }
}
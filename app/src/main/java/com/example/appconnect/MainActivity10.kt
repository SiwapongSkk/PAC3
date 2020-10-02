package com.example.appconnect

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main10.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity10 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main10)

        val sharedPreferences = getSharedPreferences("User_Info", Context.MODE_PRIVATE)

        val name =sharedPreferences.getString("NAME","")
        val password =sharedPreferences.getString("password","")
        val login =sharedPreferences.getBoolean("login",false)
        val thermo =sharedPreferences.getFloat("Thermo",0.0f)

        textView16.text = "Name : $name \npassword : $password \nStatus login : $login \nThermo : $thermo"

        button3.text="อุณหภูมิร่างกาย\n$thermo\nองศา"


        /*   set Date  */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
            var answer: String =  current.format(formatter)
            Log.d("answer",answer)
            textView17.text = "DATE : $answer"
        } else {
            var date = Date()
            val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
            val answer: String = formatter.format(date)
            Log.d("answer",answer)
            textView17.text = "DATE : $answer"

        }


        go()

    }



    fun go(){
        button3.setOnClickListener {

            val intent = Intent(this@MainActivity10, MainActivity11::class.java)
            startActivity(intent)

        }


        button4.setOnClickListener {


        }
    }
}
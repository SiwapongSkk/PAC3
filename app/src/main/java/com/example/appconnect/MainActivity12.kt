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
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main12.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity12 : AppCompatActivity() {

    val url = "https://ehr-system-project.herokuapp.com/api/examination"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main12)

        add()
    }

    fun add(){
        button8.setOnClickListener {
            val jsonobj1 = JSONObject()

            val sharedPreferences = getSharedPreferences("User_Info", Context.MODE_PRIVATE)

            val body_temperature: Float = editTextNumberDecimal.text.toString().toFloat()

            //---*
            val editor = sharedPreferences.edit()
            editor.putFloat("Thermo", body_temperature!!)
            editor.apply()

            /*   set Date  */
            var answerdate1 : String = "1111"
            var answertime2 : String = "2222"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
                val formatterdate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val formattertime = DateTimeFormatter.ofPattern("HH:mm:ss")

                //"dd.MM.yyyy. HH:mm:ss"
                var answer: String =  current.format(formatter)
                val answerdate: String =  current.format(formatterdate)
                val answertime: String =  current.format(formattertime)
                answerdate1 = answerdate
                answertime2 = answertime
                //Log.d("answer",answer)
            } else {
                var date = Date()
                val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
                val answer: String = formatter.format(date)
                Log.d("answer",answer)
            }

            val nameU =sharedPreferences.getString("NAME","")

            //**
            jsonobj1.put("user_name_patient",nameU)
            jsonobj1.put("body_temperature_patient",body_temperature)
            jsonobj1.put("date_add",answerdate1)
            jsonobj1.put("time_add",answertime2)

            val que = Volley.newRequestQueue(this)

            val request = JsonObjectRequest(
                Request.Method.POST,url,jsonobj1,
                Response.Listener { response ->
                    // Process the json

                    try {
                        val obj = response

                        //textView10.text = "Response: $response"
                        Toast.makeText(applicationContext, "Volley $response "  , Toast.LENGTH_SHORT).show()


                        val intent = Intent(this@MainActivity12, MainActivity10::class.java)
                        startActivity(intent)

                    }catch (e:Exception){
                        //textView10.text = "Exception: $e "
                    }

                }, Response.ErrorListener{
                    // Error in request
                   // textView10.text = "Volley error: $it "
                    Toast.makeText(applicationContext, "Volley error $it"  , Toast.LENGTH_SHORT).show()

                })
            que.add(request)
        }

        button7.setOnClickListener{
            val intent = Intent(this@MainActivity12, MainActivity13::class.java)
            startActivity(intent)
        }

    }

}
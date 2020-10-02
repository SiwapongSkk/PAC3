package com.example.appconnect

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main12.*
import org.json.JSONObject

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
            val nameU =sharedPreferences.getString("NAME","")


            //**
            jsonobj1.put("user_name_patient",nameU)
            jsonobj1.put("body_temperature_patient",body_temperature)

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
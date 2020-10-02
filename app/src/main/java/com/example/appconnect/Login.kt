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
import kotlinx.android.synthetic.main.activity_main10.*
import kotlinx.android.synthetic.main.activity_main9.*
import org.json.JSONObject

class Login : AppCompatActivity() {

    val url = "https://ehr-system-project.herokuapp.com/api/auth/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val jsonobj1 = JSONObject()

        val sharedPreferences = getSharedPreferences("User_Info", Context.MODE_PRIVATE)


        button2.setOnClickListener {

            val inputValue: String = editTextTextPersonName.text.toString()
            val inputValue2: String = editTextTextPassword.text.toString()

            //---*
            val editor = sharedPreferences.edit()



            jsonobj1.put("user_name_patient",inputValue)
            jsonobj1.put("password_patient",inputValue2)



            val que = Volley.newRequestQueue(this)

            val request = JsonObjectRequest(
                Request.Method.POST,url,jsonobj1,
                Response.Listener { response ->
                    // Process the json

                    try {
                        val obj = response

                        textView10.text = "Response: $response" + response.hashCode()
                        Toast.makeText(applicationContext, "Volley $response "  , Toast.LENGTH_SHORT).show()

                        textView12.text = "Response: " + obj.getString("user_name_patient")

                        val username = obj.getString("user_name_patient")
                        val passwordpatient = obj.getString("password_patient")


                        //***///**
                        editor.putString("NAME", username)
                        editor.putString("password", passwordpatient)
                        editor.putBoolean("login", true )

                        //**---**
                        editor.apply()

                        val nameU =sharedPreferences.getString("NAME","")
                        textView13.text = "Name: $nameU"

                        nexttopro()

                    }catch (e:Exception){
                        textView10.text = "Exception: $e "
                    }

                }, Response.ErrorListener{
                    // Error in request
                    textView10.text = "Volley error: $it "
                    Toast.makeText(applicationContext, "Volley error $it"  , Toast.LENGTH_SHORT).show()

                })
            que.add(request)
        }


    }

    fun nexttopro(){
            val intent = Intent(this@Login, MainActivity10::class.java)
            startActivity(intent)
    }


}
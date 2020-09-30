package com.example.appconnect


import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class MainActivity3 : AppCompatActivity() {

    private var mTextViewResult: TextView? = null
    private var mQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        Log.d("TAG", "message*--------------11" )
        mTextViewResult = findViewById(R.id.text_view_result)

       // val buttonParse: Button = findViewById(R.id.button_parse)
        Log.d("TAG", "message*--------------222" )
        mQueue = Volley.newRequestQueue(this)
        jsonParse();
    }
    private fun jsonParse() {
        val url = "https://ehr-system-project.herokuapp.com/api/patient/5f474e33d7734ba7bd2cca0a"
        Log.d("TAG", "message*--------------333" )
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject?>() {

                //Log.d("TAG", "message*--------------444" )

                fun onResponse(response: JSONObject) {
                    try {
                       // Log.d("TAG", "message*--------------" + response)
                        val jsonArray = response.getJSONArray("Patient")

                       // Log.d("TAG", "message*--------------*-*" + jsonArray)
                        for (i in 0 until jsonArray.length()) {
                            val employee = jsonArray.getJSONObject(i)
                            val firstName = employee.getString("user_name_patient")
                            val age = employee.getString("surname_patient")
                            val mail = employee.getString("createdAt")
                            mTextViewResult?.append("$firstName, $age, $mail\n\n")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }, Response.ErrorListener { error -> error.printStackTrace() })
        mQueue?.add(request)
    }
}
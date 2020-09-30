package com.example.appconnect

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.view.*
import org.json.JSONObject


class MainActivity2 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val textView1 = findViewById<TextView>(R.id.text)
        val textView2 = findViewById<TextView>(R.id.text3)
// ...

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://ehr-system-project.herokuapp.com/api/patient/5f474e33d7734ba7bd2cca0a"
        Log.d("TAG", "message*--------------" )
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                try {
                    val obj = JSONObject(response)
                    Log.d("My App", obj.toString())

                   textView1.text = "Response is: ${ obj.getString("user_name_patient")}"
                    textView2.text = "Response is: ${ obj.getString("surname_patient")}"
                } catch (t: Throwable) {
                    Log.e(
                        "My App",
                        "Could not parse malformed JSON: "
                    )
                }
            },
            Response.ErrorListener { textView1.text = "That didn't work!" })

// Add the request to the RequestQueue.
         queue.add(stringRequest)
    }


}
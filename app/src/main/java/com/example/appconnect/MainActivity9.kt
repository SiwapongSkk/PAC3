package com.example.appconnect

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main4.*
import kotlinx.android.synthetic.main.activity_main5.*
import kotlinx.android.synthetic.main.activity_main9.*
import org.json.JSONObject
import java.time.temporal.ValueRange


class MainActivity9 : AppCompatActivity() {

    val url = "https://ehr-system-project.herokuapp.com/api/examination"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main9)

       val jsonobj = JSONObject()

        button.setOnClickListener {

            val inputValue: String = inputValue.text.toString()
            val inputValue2: String = inputValue2.text.toString()



            jsonobj.put("user_name_patient",inputValue)
            jsonobj.put("weight_patient",inputValue2)


            val que = Volley.newRequestQueue(this)

            val request = JsonObjectRequest(
                Request.Method.POST,url,jsonobj,
                Response.Listener { response ->
                    // Process the json

                    try {
                        textView4.text = "Response: $response" + response.hashCode()
                        Toast.makeText(applicationContext, "Volley $response "  , Toast.LENGTH_SHORT).show()
                    }catch (e:Exception){
                        textView4.text = "Exception: $e "
                    }

                }, Response.ErrorListener{
                    // Error in request
                    textView4.text = "Volley error: $it "
                    Toast.makeText(applicationContext, "Volley error $it"  , Toast.LENGTH_SHORT).show()

                })
            que.add(request)
        }


    }




}
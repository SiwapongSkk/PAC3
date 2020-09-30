package com.example.appconnect

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main4.btn
import kotlinx.android.synthetic.main.activity_main4.textView
import kotlinx.android.synthetic.main.activity_main5.*
import org.json.JSONObject


class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)



        // Run volley
        btn.setOnClickListener {

            val inputValue: String = editTextName.text.toString()
            val inputValue2: String = editTextsurname.text.toString()



            if (inputValue == null || inputValue.trim()=="" || inputValue2 == null || inputValue2.trim()==""){
                Toast.makeText(this,"please input data, edit text cannot be blank",Toast.LENGTH_LONG).show()
            }else{

                editTextName.setText(inputValue).toString()
                //val url = "https://postman-echo.com/post"
                val url = "https://ehr-system-project.herokuapp.com/api/examination"
                textView.text = ""

                // Post parameters
                // Form fields and values
                val params = HashMap<String,String>()
                /*
                params["id_data_update_patient"] = "00"
                */
                params["user_name_patient"] = inputValue
                /*
                params["id_patient"] = "00"
                params["date_of_update_patient"] = "00"
                 */
                params["weight_patient"] = inputValue2
                /*
                params["height_patient"] = "00"
                params["heart_rate_patient"] = "00"
                params["systolic_blood_pressure_patient"] = "00"
                params["diastolic_blood_pressure_patient"] = "00"
                params["body_temperature_patient"] = "00"
                */

                val jsonObject = JSONObject(params as Map<*, *>)

                // Volley post request with parameters
                val request = JsonObjectRequest(
                    Request.Method.POST,url,jsonObject,
                    Response.Listener { response ->
                        // Process the json

                        try {
                            textView.text = "Response: $response "


                        }catch (e:Exception){
                            textView.text = "Exception: $e"
                        }

                    }, Response.ErrorListener{
                        // Error in request
                        textView.text = "Volley error: $it "
                        Toast.makeText(applicationContext, "Volley error"  , Toast.LENGTH_SHORT).show()

                    })



                // Volley request policy, only one time request to avoid duplicate transaction
                request.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    // 0 means no retry
                    0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                    1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )

                // Add the volley post request to the request queue
                VolleySingleton.getInstance(this).addToRequestQueue(request)


            }



        }


    }
}
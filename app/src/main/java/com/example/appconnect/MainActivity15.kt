package com.example.appconnect

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main15.*
import org.json.JSONObject

class MainActivity15 : AppCompatActivity() {

    private val list = ArrayList<Anime>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main15)

        callVolley()


    }

    fun callVolley(){

        val jsonobj1 = JSONObject()
        val url = "https://ehr-system-project.herokuapp.com/api/examination/list/bodyt/"
        val sharedPreferences = getSharedPreferences("User_Info", Context.MODE_PRIVATE)

        //---*
        val editor = sharedPreferences.edit()

        jsonobj1.put("user_name_patient","repo")
        jsonobj1.put("password_patient","123456")

        val request = JsonArrayRequest(Request.Method.GET,url + "repo",null,
            Response.Listener {
                for(i in 0 until it.length()){
                    val jsonObj = it.getJSONObject(i)

                    val name = jsonObj.getString("user_name_patient")
                    val description = jsonObj.getString("body_temperature_patient")
                    //val rating = jsonObj.getString("Rating")
                    val category = jsonObj.getString("body_temperature_patient")
                    val studio = jsonObj.getString("updatedAt")
                    val img = jsonObj.getString("user_name_patient")

                    val data = Anime(name,description,category,studio,img)
                    list.add(data)
                    //create adapter
                    recycler_view.setHasFixedSize(true)
                    recycler_view.layoutManager = LinearLayoutManager(this)
                    val adapter = RvAdapter(this,list)
                    recycler_view.adapter = adapter

                }
            },
            Response.ErrorListener { Toast.makeText(this, "Something went wrong!! $it", Toast.LENGTH_SHORT).show()
            Log.e("eeee","$it")
            })

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

}

package com.example.appconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main11.*

class MainActivity11 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main11)


        go()
    }


    fun go(){

        button5.setOnClickListener {

            val intent = Intent(this@MainActivity11, MainActivity12::class.java)
            startActivity(intent)

        }



    }
}
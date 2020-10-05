package com.example.appconnect

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {


    private val LocationPermission = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val Location_Request = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


      pa();


    }



    fun pa(){
        val buttonp2 = findViewById<Button>(R.id.buttonp2);
        val buttonp3 = findViewById<Button>(R.id.buttonp3);
        val buttonp4 = findViewById<Button>(R.id.buttonp4);
        val buttonp5 = findViewById<Button>(R.id.buttonp5);
        val buttonp6 = findViewById<Button>(R.id.buttonlogin);
        val buttonp7 = findViewById<Button>(R.id.buttonp7);
        val buttonp8 = findViewById<Button>(R.id.buttonp8);
        val buttonp9 = findViewById<Button>(R.id.buttonp9);
        val buttonp10 = findViewById<Button>(R.id.buttonp10);
        val buttonp11 = findViewById<Button>(R.id.buttonp11);
        val buttonp12 = findViewById<Button>(R.id.buttonp12);


        buttonp2.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }

        buttonp3.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity3::class.java)
            startActivity(intent)
        }

        buttonp4.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity4::class.java)
            startActivity(intent)
        }

        buttonp5.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity5::class.java)
            startActivity(intent)
        }
        buttonp6.setOnClickListener{
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
        }
        buttonp7.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity6::class.java)
            startActivity(intent)
        }
        buttonp8.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity8::class.java)
            startActivity(intent)
        }
        buttonp9.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity9::class.java)
            startActivity(intent)
        }
        buttonp10.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity10::class.java)
            startActivity(intent)
        }
        buttonp11.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity14::class.java)
            startActivity(intent)
        }
        buttonp12.setOnClickListener{
            val intent = Intent(this@MainActivity, MainActivity15::class.java)
            startActivity(intent)
        }


    }



}
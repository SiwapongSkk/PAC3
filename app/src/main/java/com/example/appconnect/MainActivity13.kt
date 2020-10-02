package com.example.appconnect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ideabus.model.bluetooth.MyBluetoothLE
import com.ideabus.model.data.ThermoMeasureData
import com.ideabus.model.protocol.ThermoProtocol
import kotlinx.android.synthetic.main.activity_main12.*
import kotlinx.android.synthetic.main.activity_main13.*
import org.json.JSONObject

class MainActivity13 : AppCompatActivity(), ThermoProtocol.OnDataResponseListener,
    ThermoProtocol.OnConnectStateListener, ThermoProtocol.OnNotifyStateListener,
    MyBluetoothLE.OnWriteStateListener {

    private  var textView1: TextView? = null
    private  var textView2:TextView? = null
    private  var textView3:TextView? = null
    private  var textView4:TextView? = null
    private  var textView5:TextView? = null

    private var datathermo :Float?= null

    private var isConnecting = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main13)



        textView1 = findViewById<View>(R.id.textView23) as TextView
        textView2 = findViewById<View>(R.id.textView24) as TextView
        textView3 = findViewById<View>(R.id.textView25) as TextView
        textView4 = findViewById<View>(R.id.textView26) as TextView
        textView5 = findViewById<View>(R.id.textView27) as TextView

        initParam()

        b()
        //return
    }

    private fun initParam() {

        //Initialize the connection SDK
        Global.thermoProtocol = ThermoProtocol.getInstance(this, false, true, Global.sdkid_BT)
        //toolbar.setSubtitle("Body Temperature " + Global.thermoProtocol.getSDKVersion());

    }

    override fun onStart() {
        super.onStart()
        Global.thermoProtocol.setOnDataResponseListener(this)
        Global.thermoProtocol.setOnConnectStateListener(this)
        Global.thermoProtocol.setOnNotifyStateListener(this)
        Global.thermoProtocol.setOnWriteStateListener(this)

        //Start scan
        startScan()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Global.thermoProtocol.isConnected) Global.thermoProtocol.disconnect()
        Global.thermoProtocol.stopScan()
    }

    private fun startScan() {
        if (!Global.thermoProtocol.isSupportBluetooth(this)) {
            return
        }
        textView1!!.text = "start scan"
        Toast.makeText(applicationContext, "start scan", Toast.LENGTH_SHORT).show()
        //logListAdapter.addLog("start scan");
        Global.thermoProtocol.startScan(10)
    }

    override fun onStop() {
        super.onStop()
        Global.thermoProtocol.disconnect()
        Global.thermoProtocol.stopScan()
    }

    override fun onResponseDeviceInfo(
        macAddress: String?,
        workMode: Int,
        batteryVoltage: Float
    ) {
        /* logListAdapter.addLog("THERMO : DeviceInfo -> macAddress = " + macAddress +
                " , workMode = " + workMode + " , batteryVoltage = " + batteryVoltage);
        */
    }

    override fun onResponseUploadMeasureData(data: ThermoMeasureData) {

        val Thermo = data.toString()

        //*---**-
        val datacut1: String? = Thermo.substringAfterLast("measureTemperature=")
        val datacut2: Float? = datacut1?.substringBeforeLast(", mod").toString().toFloat()

        datathermo = datacut2

        textView3!!.text = "THERMO : UploadMeasureData -> ThermoMeasureData =  \n $datathermo"
        //logListAdapter.addLog("THERMO : UploadMeasureData -> ThermoMeasureData = " + data);


    }

    override fun onBtStateChanged(isEnable: Boolean) {
        //BLE will be returned when it is turned enable or disable
        if (isEnable) {
            Toast.makeText(this, "BLE is enable!!", Toast.LENGTH_SHORT).show()
            startScan()
        } else Toast.makeText(this, "BLE is disable!!", Toast.LENGTH_SHORT).show()
    }

    override fun onScanResult(mac: String, name: String, rssi: Int) {
        //Temperature
        if (!name.startsWith("n/a")) {
            textView2!!.text = "onScanResult：$name mac:$mac rssi:$rssi"
            //logListAdapter.addLog("onScanResult："+name+" mac:"+mac+" rssi:"+rssi);
        }
        if (isConnecting) return
        isConnecting = true

        //Stop scanning before connecting
        Global.thermoProtocol.stopScan()
        //Connection
        Global.thermoProtocol.connect(mac)
    }

    override fun onConnectionState(state: ThermoProtocol.ConnectState?) {
        //BLE connection status return, used to judge connection or disconnection
        when (state) {
            ThermoProtocol.ConnectState.Connected -> {
                isConnecting = false
                textView4!!.text = "Connected"
            }
            ThermoProtocol.ConnectState.ConnectTimeout -> isConnecting = false
            ThermoProtocol.ConnectState.Disconnect -> {
                isConnecting = false
                textView5!!.text = "Disconnected"
                //logListAdapter.addLog("Disconnected");
                startScan()
            }
            ThermoProtocol.ConnectState.ScanFinish ->                // logListAdapter.addLog("ScanFinish");
                startScan()
        }
    }


    override fun onWriteMessage(isSuccess: Boolean, message: String?) {
        // logListAdapter.addLog("WRITE : " + message);
    }

    override fun onNotifyMessage(message: String?) {
        //logListAdapter.addLog("NOTIFY : " + message);
    }





    //**------------**



    fun b(){
        button6.setOnClickListener{

//            datathermo?.let { it1 ->
//                //connect(it1)
//
//            }

            if(datathermo != null){

//                val sharedPreferences = getSharedPreferences("User_Info", Context.MODE_PRIVATE)
//                val editor = sharedPreferences.edit()
//                editor.putFloat("Thermo", datathermo!!)
//                editor.apply()

                connect(datathermo!!)

                val intent = Intent(this@MainActivity13, MainActivity10::class.java)
                startActivity(intent)

            }
            else{
                Toast.makeText(applicationContext, "No data", Toast.LENGTH_SHORT).show()
            }

            //initParam()
            /*
            if (datathermo != null) {
                connect(datathermo)
            }
             */

        }
    }

    //** connect to data ba


    fun connect(dataThermo : Float){
        val url = "https://ehr-system-project.herokuapp.com/api/examination"
        val sharedPreferences = getSharedPreferences("User_Info", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("Thermo", datathermo!!)
        editor.apply()

            val jsonobj1 = JSONObject()

            val body_temperature: Float = dataThermo


            val nameU =sharedPreferences.getString("NAME","")

            jsonobj1.put("user_name_patient",nameU)
            jsonobj1.put("body_temperature_patient",body_temperature)

            val que = Volley.newRequestQueue(this)

            val request = JsonObjectRequest(
                Request.Method.POST,url,jsonobj1,
                Response.Listener { response ->
                    /* Process the json*/

                    try {
                        val obj = response

                        /*textView10.text = "Response: $response"*/
                        Toast.makeText(applicationContext, "Volley $response "  , Toast.LENGTH_SHORT).show()

                        /*nexttopro()*/

                    }catch (e:Exception){
                        /*textView10.text = "Exception: $e "*/
                    }

                }, Response.ErrorListener{
                    /* Error in request*/
                    /* textView10.text = "Volley error: $it "*/
                    Toast.makeText(applicationContext, "Volley error $it"  , Toast.LENGTH_SHORT).show()

                })
            que.add(request)

    }




}
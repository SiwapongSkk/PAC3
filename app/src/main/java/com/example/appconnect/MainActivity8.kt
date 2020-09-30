package com.example.appconnect

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ideabus.model.bluetooth.MyBluetoothLE
import com.ideabus.model.data.ThermoMeasureData
import com.ideabus.model.protocol.Global
import com.ideabus.model.protocol.ThermoProtocol
import kotlinx.android.synthetic.main.activity_main8.*


class MainActivity8 : AppCompatActivity(), ThermoProtocol.OnDataResponseListener,
    ThermoProtocol.OnConnectStateListener  /*, ThermoProtocol.OnNotifyStateListener,
    MyBluetoothLE.OnWriteStateListener */{

    private var isConnecting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main8)

        val textViewSDKVersion = findViewById<TextView>(R.id.textViewSDKVersion)
        val textView5 = findViewById<TextView>(R.id.textView5)
        val textView6 = findViewById<TextView>(R.id.textView6)
        val textView7 = findViewById<TextView>(R.id.textView7)
        val textView8 = findViewById<TextView>(R.id.textView8)
        val textView9 = findViewById<TextView>(R.id.textView9)


        initParam()

    }

    private fun initParam(){

        //Initialize the connection SDK
        Global.thermoProtocol = ThermoProtocol.getInstance(this, false, true, com.example.appconnect.Global.sdkid_BT)

        textViewSDKVersion.text = "Response is: "+ Global.sdkVersion

        textView5.text = ("Body Temperature " + Global.thermoProtocol.getSDKVersion());

        //Toast.makeText(this@MainActivity8, "start scan", Toast.LENGTH_SHORT).show()

    }

    override fun onStart() {
        super.onStart()
        Global.thermoProtocol.setOnDataResponseListener(this)
        Global.thermoProtocol.setOnConnectStateListener(this)
        //Global.thermoProtocol.setOnNotifyStateListener(this)
        //Global.thermoProtocol.setOnWriteStateListener(this)

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
        textView5.text = ("111 " );
        Toast.makeText(this@MainActivity8, "start scan1", Toast.LENGTH_SHORT).show()
        textView6.text = ("start scan1" );
        Log.i("Info", "start scan1")
        //logListAdapter.addLog("start scan")
        Global.thermoProtocol.startScan(10)
    }

    override fun onStop() {
        super.onStop()
        Global.thermoProtocol.disconnect()
        Global.thermoProtocol.stopScan()
    }


    override fun onResponseDeviceInfo(
        macAddress: String,
        workMode: Int,
        batteryVoltage: Float
    ) {
        //Toast.makeText(this@MainActivity8, "THERMO : DeviceInfo -> macAddress = " +" , workMode = " + workMode + " , batteryVoltage = " + batteryVoltage , Toast.LENGTH_SHORT).show()

        /*logListAdapter.addLog(
            "THERMO : DeviceInfo -> macAddress = " +" , workMode = " + workMode + " , batteryVoltage = " + batteryVoltage
        )
        */
    }

    override fun onResponseUploadMeasureData(data: ThermoMeasureData) {

        textView7.text = ("THERMO : UploadMeasureData -> ThermoMeasureData = $data" );
        Log.i("Info", "THERMO : UploadMeasureData -> ThermoMeasureData = $data" )
        //Toast.makeText(this@MainActivity8, "THERMO : UploadMeasureData -> ThermoMeasureData = $data", Toast.LENGTH_SHORT).show()
        /*logListAdapter.addLog("THERMO : UploadMeasureData -> ThermoMeasureData = $data")*/
    }

    override fun onBtStateChanged(isEnable: Boolean) {
        //BLE will be returned when it is turned enable or disable
        if (isEnable) {
            //Toast.makeText(this, "BLE is enable!!", Toast.LENGTH_SHORT).show()
            startScan()
        }
        //else
        //Toast.makeText(this, "BLE is disable!!", Toast.LENGTH_SHORT).show()

    }

    override fun onScanResult(mac: String, name: String, rssi: Int) {
        //Temperature
        if (!name.startsWith("n/a")) {
            textView8.text = ("onScanResult：$name mac:$mac rssi:$rssi" );
            Log.i("Info", "onScanResult：$name mac:$mac rssi:$rssi"  )
            //Toast.makeText(this@MainActivity8, "onScanResult：$name mac:$mac rssi:$rssi", Toast.LENGTH_SHORT).show()
           /* logListAdapter.addLog("onScanResult：$name mac:$mac rssi:$rssi") */
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
                Toast.makeText(this@MainActivity8, "Connected", Toast.LENGTH_SHORT).show()
                /*logListAdapter.addLog("Connected") */
            }
            ThermoProtocol.ConnectState.ConnectTimeout -> {
                isConnecting = false
                Toast.makeText(this@MainActivity8, "ConnectTimeout", Toast.LENGTH_SHORT).show()
                /*logListAdapter.addLog("ConnectTimeout")*/
            }
            ThermoProtocol.ConnectState.Disconnect -> {
                isConnecting = false
                Toast.makeText(this@MainActivity8, "Disconnected", Toast.LENGTH_SHORT).show()
                /*logListAdapter.addLog("Disconnected")*/
                startScan()
            }
            ThermoProtocol.ConnectState.ScanFinish -> {
                textView9.text = ("ScanFinish" );
                Toast.makeText(this@MainActivity8, "ScanFinish", Toast.LENGTH_SHORT).show()
                /*logListAdapter.addLog("ScanFinish")*/
                startScan()
            }
        }
    }

/*
    override fun onWriteMessage(isSuccess: Boolean, message: String) {
        //Toast.makeText(this@MainActivity8, "WRITE : $message", Toast.LENGTH_SHORT).show()
        /*logListAdapter.addLog("WRITE : $message")*/
    }

    override fun onNotifyMessage(message: String) {
        //Toast.makeText(this@MainActivity8, "NOTIFY : $message", Toast.LENGTH_SHORT).show()
        /*logListAdapter.addLog("NOTIFY : $message")*/
    }

*/

}


package com.ideabus.sdk_test;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import com.ideabus.model.bluetooth.MyBluetoothLE;
import com.ideabus.model.data.ThermoMeasureData;
import com.ideabus.model.protocol.ThermoProtocol;


public class BtTestActivity extends AppCompatActivity implements ThermoProtocol.OnConnectStateListener, ThermoProtocol.OnNotifyStateListener,
        ThermoProtocol.OnDataResponseListener,  MyBluetoothLE.OnWriteStateListener {

    private ListView btListView;
    private Toolbar toolbar;
    private LogListAdapter logListAdapter;
    private boolean isConnecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize the body ester machine Bluetooth module

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_test);

        initView();
        initParam();
    }


    /**
     * 初始化View
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        btListView = (ListView) findViewById(R.id.bt_list_view);

    }

    /**
     * 初始化
     */
    private void initParam() {
        setSupportActionBar(toolbar);

        //Initialize the connection SDK
        Global.thermoProtocol = ThermoProtocol.getInstance(this, false, true, Global.sdkid_BT);
        toolbar.setSubtitle("Body Temperature " + Global.thermoProtocol.getSDKVersion());

        logListAdapter = new LogListAdapter(this);
        btListView.setAdapter(logListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Global.thermoProtocol.setOnDataResponseListener(this);
        Global.thermoProtocol.setOnConnectStateListener(this);
        Global.thermoProtocol.setOnNotifyStateListener(this);
        Global.thermoProtocol.setOnWriteStateListener(this);

        //Start scan
        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Global.thermoProtocol.isConnected()) Global.thermoProtocol.disconnect();
        Global.thermoProtocol.stopScan();
    }

    private void startScan() {
        if (!Global.thermoProtocol.isSupportBluetooth(this)) {
            return;
        }
        logListAdapter.addLog("start scan");
        Global.thermoProtocol.startScan(10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Global.thermoProtocol.disconnect();
        Global.thermoProtocol.stopScan();
    }

    @Override
    public void onResponseDeviceInfo(String macAddress, int workMode, float batteryVoltage) {
        logListAdapter.addLog("THERMO : DeviceInfo -> macAddress = " + macAddress +
                " , workMode = " + workMode + " , batteryVoltage = " + batteryVoltage);
    }

    @Override
    public void onResponseUploadMeasureData(ThermoMeasureData data) {
        logListAdapter.addLog("THERMO : UploadMeasureData -> ThermoMeasureData = " + data);
    }

    @Override
    public void onBtStateChanged(boolean isEnable) {
        //BLE will be returned when it is turned enable or disable
        if (isEnable) {
            Toast.makeText(this, "BLE is enable!!", Toast.LENGTH_SHORT).show();
            startScan();
        } else
            Toast.makeText(this, "BLE is disable!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScanResult(String mac, String name, int rssi) {
        //Temperature
        if (!name.startsWith("n/a")) {
            logListAdapter.addLog("onScanResult："+name+" mac:"+mac+" rssi:"+rssi);
        }
        if (isConnecting)
            return;
        isConnecting = true;

        //Stop scanning before connecting
        Global.thermoProtocol.stopScan();
        //Connection
        Global.thermoProtocol.connect(mac);
    }

    @Override
    public void onConnectionState(ThermoProtocol.ConnectState state) {
        //BLE connection status return, used to judge connection or disconnection
        switch (state) {
            case Connected:
                isConnecting = false;
                logListAdapter.addLog("Connected");
                break;
            case ConnectTimeout:
                isConnecting = false;
                logListAdapter.addLog("ConnectTimeout");
                break;
            case Disconnect:
                isConnecting = false;
                logListAdapter.addLog("Disconnected");
                startScan();
                break;
            case ScanFinish:
                logListAdapter.addLog("ScanFinish");
                startScan();
                break;
        }
    }



    @Override
    public void onWriteMessage(boolean isSuccess, String message) {
        logListAdapter.addLog("WRITE : " + message);
    }

    @Override
    public void onNotifyMessage(String message) {
        logListAdapter.addLog("NOTIFY : " + message);
    }
}

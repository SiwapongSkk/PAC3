package com.ideabus.sdk_test;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ideabus.ideabuslibrary.util.BaseUtils;
import com.ideabus.model.bluetooth.MyBluetoothLE;
import com.ideabus.model.data.CurrentAndMData;
import com.ideabus.model.data.DRecord;
import com.ideabus.model.data.DeviceInfo;
import com.ideabus.model.data.EBodyMeasureData;
import com.ideabus.model.data.OscillationData;
import com.ideabus.model.data.PulseInfo;
import com.ideabus.model.data.ThermoMeasureData;
import com.ideabus.model.data.User;
import com.ideabus.model.data.VersionData;
import com.ideabus.model.protocol.BPMProtocol;
import com.ideabus.model.protocol.BaseProtocol;
import com.ideabus.model.protocol.EBodyProtocol;
import com.ideabus.model.protocol.ThermoProtocol;


public abstract class MainActivity extends AppCompatActivity implements
        BaseProtocol.OnConnectStateListener, View.OnClickListener, BaseProtocol.OnDataResponseListener, BaseProtocol.OnNotifyStateListener, MyBluetoothLE.OnWriteStateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView logList;
    private LogListAdapter logListAdapter;

    private boolean isSendPersonParam;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_test);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_CMD_Button);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(false);


        initFragment();
        initView();
        initParam();
        initListener();

//        Intent intent = getIntent();
//        String action = intent.getAction();
        ab.setSubtitle("Teat");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Global.protocol.setOnConnectStateListener(this);
        Global.protocol.startScan(10,BaseProtocol.DeviceType.DEVICE_TYPE_ALL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Global.protocol.isConnected(BaseProtocol.DeviceType.DEVICE_TYPE_ALL))
            Global.protocol.disconnect(BaseProtocol.DeviceType.DEVICE_TYPE_ALL);
    }

    protected void initFragment() {

    }

    protected void initView() {
        this.logList = (ListView) findViewById(R.id.logList);

    }


    protected void initParam() {
        //Initialize the connection SDK
        Global.protocol.setOnDataResponseListener(this);
        Global.protocol.setOnNotifyStateListener(this);
        Global.protocol.setOnWriteStateListener(this);

        logListAdapter = new LogListAdapter(this);
        logList.setAdapter(logListAdapter);

//        editText = (EditText) findViewById(R.id.editText);
    }

    protected void initListener() {
//        findViewById(R.id.button1).setOnClickListener(this);
//        findViewById(R.id.button2).setOnClickListener(this);
//        findViewById(R.id.button3).setOnClickListener(this);
//        findViewById(R.id.button4).setOnClickListener(this);
//        findViewById(R.id.button5).setOnClickListener(this);
//        findViewById(R.id.button6).setOnClickListener(this);
//        findViewById(R.id.button7).setOnClickListener(this);
//        findViewById(R.id.button9).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.button1:
//                Global.protocol.readHistorysOrCurrDataAndSyncTiming();
//                break;
//            case R.id.button2:
//                Global.protocol.clearAllHistorys();
//                break;
//            case R.id.button3://04h
//                Global.protocol.disconnectBPM();
//                break;
//            case R.id.button4://05h
//                Global.protocol.readUserAndVersionData();
//                break;
//            case R.id.button5:
//                String userId = "918375537VB";
////                if (!editText.getText().toString().equals("")) userId = editText.getText().toString();
//                Global.protocol.writeUserData(userId, 71);//06h
//                break;
//            case R.id.button6:
//                Global.protocol.readLastData();
//                break;
//            case R.id.button7:
//                Global.protocol.clearLastData();
//                break;
//            case R.id.button9:
//                Global.protocol.readDeviceInfo();
//                break;
//            default:
//        }
    }

    @Override
    public void onBtStateChanged(boolean isEnable) {
        //BLE will be returned when it is turned enable or disable
        if (isEnable) {
            Toast.makeText(this, "BLE is enable!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "BLE is disable!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScanResult(String mac, String name, int rssi, BaseProtocol.DeviceType deviceType) {
        //When scanning BLE, it will be sent back, and then added to the list.
        if (!name.startsWith("n/a")) {
            logListAdapter.addLog("onScanResult mac = " + mac + " name = " + name);
        }
        if (deviceType == BaseProtocol.DeviceType.DEVICE_TYPE_THERMO) {
            Global.protocol.stopScan(BaseProtocol.DeviceType.DEVICE_TYPE_THERMO);
            //Connection
            Global.protocol.connect(mac,BaseProtocol.DeviceType.DEVICE_TYPE_THERMO);
        }
        if (deviceType == BaseProtocol.DeviceType.DEVICE_TYPE_BPM) {
            //Stop scanning before connecting
            Global.protocol.stopScan(BaseProtocol.DeviceType.DEVICE_TYPE_BPM);
            //Connection
            if (name.startsWith("A")) {
                Global.protocol.connect(mac,BaseProtocol.DeviceType.DEVICE_TYPE_BPM);
            } else {
                Global.protocol.bond(mac,BaseProtocol.DeviceType.DEVICE_TYPE_BPM);
            }
        }
    }

    @Override
    public void onScanResult(BluetoothDevice device, BaseProtocol.DeviceType deviceType) {
        //When scanning BLE, it will be sent back, and then added to the list.
        logListAdapter.addLog("onScanResult mac = " + device.getAddress() + " name = " + device.getName());
        Global.protocol.stopScan(BaseProtocol.DeviceType.DEVICE_TYPE_EBODY);
        //Connection
        Global.protocol.connect(device,BaseProtocol.DeviceType.DEVICE_TYPE_EBODY);
    }

    @Override
    public void onConnectionState(BaseProtocol.ConnectState state, BaseProtocol.DeviceType deviceType) {
        //BLE connection status return, used to judge connection or disconnection
        logListAdapter.addLog("onConnectionState = " + state + " DeviceType = " + deviceType);
    }

//==============================================================


    @Override
    public void onResponseReadHistory(DRecord dRecord) {
        BaseUtils.printLog("d", TAG, "MainActivity.onResponseReadHistory");
        BaseUtils.printLog("d", TAG, "dRecord = [" + dRecord + "]");
        logListAdapter.addLog("BPM : ReadHistory -> DRecord = " + dRecord);
    }

    @Override
    public void onResponseClearHistory(boolean isSuccess) {
        BaseUtils.printLog("d", TAG, "MainActivity.onResponseClearHistory");
        BaseUtils.printLog("d", TAG, "isSuccess = [" + isSuccess + "]");
        logListAdapter.addLog("BPM : ClearHistory -> isSuccess = " + isSuccess);
    }


    @Override
    public void onResponseReadUserAndVersionData(User user, VersionData versionData) {
        BaseUtils.printLog("d", TAG, "MainActivity.onResponseReadUserAndVersionData");
        logListAdapter.addLog("BPM : ReadUserAndVersionData -> user = " + user +
                " , versionData = " + versionData);
    }

    @Override
    public void onResponseWriteUser(boolean isSuccess) {
        BaseUtils.printLog("d", TAG, "MainActivity.onResponseWriteUser");
        BaseUtils.printLog("d", TAG, "isSuccess = [" + isSuccess + "]");
        logListAdapter.addLog("BPM : WriteUser -> isSuccess = " + isSuccess);
    }

    @Override
    public void onResponseReadLastData(CurrentAndMData dRecord, int historyMeasuremeNumber, int userNumber, int MAMState, boolean isNoData) {
        logListAdapter.addLog("BPM : ReadLastData -> DRecord = " + dRecord + " historyMeasuremeNumber = " + historyMeasuremeNumber + " userNumber = " + userNumber + " MAMState = " + MAMState + " isNoData = " + isNoData);

    }

    @Override
    public void onResponseClearLastData(boolean isSuccess) {
        logListAdapter.addLog("BPM : ClearLastData -> isSuccess = " + isSuccess);

    }

    @Override
    public void onResponseReadDeviceInfo(DeviceInfo deviceInfo) {
        BaseUtils.printLog("d", TAG, "MainActivity.onResponseReadDeviceInfo");
        BaseUtils.printLog("d", TAG, "deviceInfo = [" + deviceInfo + "]");
        logListAdapter.addLog("BPM : ReadDeviceInfo -> DeviceInfo = " + deviceInfo);
    }

    @Override
    public void onResponseWriteDeviceTime(boolean isSuccess) {
        logListAdapter.addLog("BPM : ResponseWriteDeviceTime -> isSuccess = " + isSuccess);

    }

    @Override
    public void onResponseReadDeviceTime(DeviceInfo deviceInfo) {
        logListAdapter.addLog("BPM : ResponseReadDeviceTime -> DeviceInfo = " + deviceInfo);

    }

    //====================================
    //  Temperature

    @Override
    public void onResponseDeviceInfo(String macAddress, int workMode, float batteryVoltage) {
        BaseUtils.printLog("d", TAG, "MainActivity.onResponseDeviceInfo");
        BaseUtils.printLog("d", TAG, "macAddress = [" + macAddress + "], workMode = [" + workMode +
                "], batteryVoltage = [" + batteryVoltage + "]");
        logListAdapter.addLog("THERMO : DeviceInfo -> macAddress = " + macAddress +
                " , workMode = " + workMode + " , batteryVoltage = " + batteryVoltage);
    }

    @Override
    public void onResponseUploadMeasureData(ThermoMeasureData data) {
        BaseUtils.printLog("d", TAG, "MainActivity.onResponseUploadMeasureData");
        BaseUtils.printLog("d", TAG, "data = [" + data + "]");
        logListAdapter.addLog("THERMO : UploadMeasureData -> ThermoMeasureData = " + data);

    }


    //====================================
    //  Weight scale

    @Override
    public void onUserInfoUpdateSuccess() {
        logListAdapter.addLog("EBODY : MeasureResult2 -> onUserInfoUpdateSuccess");

    }

    @Override
    public void onDeleteAllUsersSuccess() {
        logListAdapter.addLog("EBODY : MeasureResult2 -> onDeleteAllUsersSuccess");

    }

    @Override
    public void onResponseMeasureResult2(EBodyMeasureData data, float impedance) {
        BaseUtils.printLog("d", TAG, "ConnectionActivity.onResponseMeasureResult2");
        BaseUtils.printLog("d", TAG, "impedance = " + impedance);
        BaseUtils.printLog("d", TAG, "data = [" + data + "]");
        logListAdapter.addLog("EBODY : MeasureResult2 -> EBodyMeasureData = " + data);
    }


    //====================================
    //  Weight scale
    @Override
    public void onNotifyMessage(String message) {
        logListAdapter.addLog("NOTIFY : " + message);
    }

    @Override
    public void onWriteMessage(boolean isSuccess, final String message) {
        logListAdapter.addLog("WRITE : " + message);
    }

}

package com.ideabus.sdk_test;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ideabus.ideabuslibrary.util.BaseUtils;
import com.ideabus.model.bluetooth.MyBluetoothLE;
import com.ideabus.model.data.CurrentAndMData;
import com.ideabus.model.data.DRecord;
import com.ideabus.model.data.DeviceInfo;
import com.ideabus.model.data.User;
import com.ideabus.model.data.VersionData;
import com.ideabus.model.protocol.BPMProtocol;

import java.util.Random;


public class BPMTestActivity extends AppCompatActivity implements
        BPMProtocol.OnConnectStateListener, View.OnClickListener, BPMProtocol.OnDataResponseListener, BPMProtocol.OnNotifyStateListener,  MyBluetoothLE.OnWriteStateListener {

    private String TAG = "BPMTestActivity";

    private ListView bpmList;
    private LogListAdapter logListAdapter;

    private boolean isSendPersonParam;
    private Toolbar toolbar;
    private boolean isConnecting;

    private String userID = "123456789AB";
    private Integer age = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize the body ester machine Bluetooth module

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpmtest);

        initView();
        initParam();
        initListener();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        bpmList = (ListView) findViewById(R.id.bpm_list_view);
        findViewById(R.id.buttonView).setVisibility(View.GONE);
    }

    private void initParam() {
        setSupportActionBar(toolbar);

        //Initialize the connection SDK
        Global.bpmProtocol = BPMProtocol.getInstance
                (this, false, true, Global.sdkid_BPM);
        toolbar.setSubtitle("Blood Pressure " + Global.bpmProtocol.getSDKVersion());

        logListAdapter = new LogListAdapter(this);
        bpmList.setAdapter(logListAdapter);

    }

    private void initListener() {
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.button10).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "1026 onStart");
        super.onStart();
        Global.bpmProtocol.setOnConnectStateListener(this);
        Global.bpmProtocol.setOnDataResponseListener(this);
        Global.bpmProtocol.setOnNotifyStateListener(this);
        Global.bpmProtocol.setOnWriteStateListener(this);

        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Global.bpmProtocol.isConnected()) Global.bpmProtocol.disconnect();
        Global.bpmProtocol.stopScan();
    }

    private void startScan() {
        if (!Global.bpmProtocol.isSupportBluetooth(this)) {
            Log.d(TAG, "1026 not support Bluetooth");
            return;
        }
        logListAdapter.addLog("start scan");
        Global.bpmProtocol.startScan(10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Global.bpmProtocol.disconnect();
        Global.bpmProtocol.stopScan();
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button)findViewById(v.getId());
        logListAdapter.addLog("WRITE : " + btn.getText().toString());
        switch (v.getId()) {
            case R.id.button1:
                Global.bpmProtocol.readHistorysOrCurrDataAndSyncTiming();
                break;
            case R.id.button2:
                Global.bpmProtocol.clearAllHistorys();
                break;
            case R.id.button3://04h
                Global.bpmProtocol.disconnectBPM();
                break;
            case R.id.button4://05h
                Global.bpmProtocol.readUserAndVersionData();
                break;
            case R.id.button5:
                setUserInfo();
                logListAdapter.addLog("WRITE : userID:" + userID + " age:" + age);
                Global.bpmProtocol.writeUserData(userID,age);//06h
                break;
            case R.id.button6:
                Global.bpmProtocol.readLastData();
                break;
            case R.id.button7:
                Global.bpmProtocol.clearLastData();
                break;
            case R.id.button8:
                Global.bpmProtocol.readDeviceTime();
                break;
            case R.id.button9:
                Global.bpmProtocol.readDeviceInfo();
                break;
            case R.id.button10:
                Global.bpmProtocol.syncTiming();
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

    @Override
    public void onResponseReadHistory(DRecord dRecord) {
        logListAdapter.addLog("BPM : ReadHistory -> DRecord = " + dRecord);
    }

    @Override
    public void onResponseClearHistory(boolean isSuccess) {
        logListAdapter.addLog("BPM : ClearHistory -> isSuccess = " + isSuccess);
    }

    @Override
    public void onResponseReadUserAndVersionData(User user, VersionData versionData) {
        logListAdapter.addLog("BPM : ReadUserAndVersionData -> user = " + user +
                " , versionData = " + versionData);
    }

    @Override
    public void onResponseWriteUser(boolean isSuccess) {
        logListAdapter.addLog("BPM : WriteUser -> isSuccess = " + isSuccess);
    }

    @Override
    public void onResponseReadLastData(CurrentAndMData dRecord, int historyMeasuremeNumber, int userNumber, int MAMState, boolean isNoData) {
        logListAdapter.addLog("BPM : ReadLastData -> DRecord = " + dRecord +
                " historyMeasuremeNumber = " + historyMeasuremeNumber +
                " userNumber = " + userNumber + " MAMState = " + MAMState +
                " isNoData = " + isNoData);
    }

    @Override
    public void onResponseClearLastData(boolean isSuccess) {
        logListAdapter.addLog("BPM : ClearLastData -> isSuccess = " + isSuccess);
    }

    @Override
    public void onResponseReadDeviceInfo(DeviceInfo deviceInfo) {
        logListAdapter.addLog("BPM : ReadDeviceInfo -> DeviceInfo = " + deviceInfo);
    }

    @Override
    public void onResponseWriteDeviceTime(boolean isSuccess) {
        logListAdapter.addLog("BPM : Write -> DeviceTime = " + isSuccess);
    }

    @Override
    public void onResponseReadDeviceTime(DeviceInfo deviceInfo) {
        logListAdapter.addLog("BPM : Read -> DeviceTime = " + deviceInfo);
    }


    @Override
    public void onBtStateChanged(boolean isEnable) {
        //BLE will be returned when it is turned enable or disable
        if (isEnable) {
            Toast.makeText(this, "BLE is enable!!", Toast.LENGTH_SHORT).show();
            startScan();
        } else {
            Toast.makeText(this, "BLE is disable!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScanResult(String mac, String name, int rssi) {
        BaseUtils.printLog("d",TAG, "1026 onScanResult:"+name);
        //Blood pressure machine
        if (!name.startsWith("n/a")) {
            logListAdapter.addLog("onScanResult："+name+" mac:"+mac+" rssi:"+rssi);
        }
        if (isConnecting)
            return;
        isConnecting = true;
        //Stop scanning before connecting
        Global.bpmProtocol.stopScan();
        //Connection
        if (name.startsWith("A")) {
            logListAdapter.addLog("3G Model！" );
            Global.bpmProtocol.connect(mac);
            findViewById(R.id.button6).setVisibility(View.VISIBLE);
            findViewById(R.id.button7).setVisibility(View.VISIBLE);
            findViewById(R.id.button8).setVisibility(View.GONE);
            findViewById(R.id.button10).setVisibility(View.GONE);
        } else {
            logListAdapter.addLog("4G Model！" );
            Global.bpmProtocol.bond(mac);
            findViewById(R.id.button6).setVisibility(View.GONE);
            findViewById(R.id.button7).setVisibility(View.GONE);
            findViewById(R.id.button8).setVisibility(View.VISIBLE);
            findViewById(R.id.button10).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnectionState(BPMProtocol.ConnectState state) {
        //BLE connection status return, used to judge connection or disconnection
        switch (state) {
            case Connected:
                isConnecting = false;
                findViewById(R.id.buttonView).setVisibility(View.VISIBLE);
                logListAdapter.addLog("Connected");
                break;
            case ConnectTimeout:
                isConnecting = false;
                findViewById(R.id.buttonView).setVisibility(View.GONE);
                logListAdapter.addLog("ConnectTimeout");
                break;
            case Disconnect:
                isConnecting = false;
                findViewById(R.id.buttonView).setVisibility(View.GONE);
                logListAdapter.addLog("Disconnected");
                startScan();
                break;
            case ScanFinish:
                findViewById(R.id.buttonView).setVisibility(View.GONE);
                logListAdapter.addLog("ScanFinish");
                startScan();
                break;
        }
    }

    public void  setUserInfo() {
        Random ran = new Random();
        int[] A = new int[11];
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            if (i < 9) {
                A[i] = (int) ((Math.random() * 10) + 48);
            } else {
                A[i] = (int) ((Math.random() * 26) + 65);
            }
            stringBuilder.append((char)A[i]);
        }
        userID = String.valueOf(stringBuilder);
        age = 18+ran.nextInt(62);
    }
}

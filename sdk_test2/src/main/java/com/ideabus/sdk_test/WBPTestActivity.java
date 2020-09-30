package com.ideabus.sdk_test;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ideabus.ideabuslibrary.util.BaseUtils;
import com.ideabus.model.bluetooth.MyBluetoothLE;
import com.ideabus.model.data.DRecord;
import com.ideabus.model.data.DeviceInfo;
import com.ideabus.model.data.DiagnosticDRecord;
import com.ideabus.model.data.NocturnalModeDRecord;
import com.ideabus.model.data.OscillationData;
import com.ideabus.model.data.PulseInfo;
import com.ideabus.model.data.User;
import com.ideabus.model.data.VersionData;
import com.ideabus.model.protocol.WBPProtocol;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Random;


public class WBPTestActivity extends AppCompatActivity implements
        WBPProtocol.OnConnectStateListener, View.OnClickListener, WBPProtocol.OnDataResponseListener, WBPProtocol.OnNotifyStateListener,  MyBluetoothLE.OnWriteStateListener {

    private String TAG = "WBPTestActivity";

    private ListView bpmList;
    private LogListAdapter logListAdapter;

    private boolean isSendPersonParam;
    private Toolbar toolbar;
    private boolean isConnecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize the body ester machine Bluetooth module

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wbptest);

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
        Global.wbpProtocol = WBPProtocol.getInstance(this, false, true, Global.sdkid_WBP);
        toolbar.setSubtitle("Watch Blood Pressure "+Global.wbpProtocol.getSDKVersion());

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
        findViewById(R.id.button11).setOnClickListener(this);
        findViewById(R.id.button12).setOnClickListener(this);
        findViewById(R.id.button13).setOnClickListener(this);
        findViewById(R.id.button14).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "1026 onStart: " + Global.wbpProtocol);
        super.onStart();
        Global.wbpProtocol.setOnConnectStateListener(this);
        Global.wbpProtocol.setOnDataResponseListener(this);
        Global.wbpProtocol.setOnNotifyStateListener(this);
        Global.wbpProtocol.setOnWriteStateListener(this);

        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Global.wbpProtocol.isConnected()) Global.wbpProtocol.disconnect();
        Global.wbpProtocol.stopScan();
    }

    private void startScan() {
        if (!Global.wbpProtocol.isSupportBluetooth(this)) {
            Log.d(TAG, "1026 not support Bluetooth");
            return;
        }
        Log.d(TAG, "1026 start scan");
        logListAdapter.addLog("start scan");
        Global.wbpProtocol.startScan(10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Global.wbpProtocol.disconnect();
        Global.wbpProtocol.stopScan();
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button)findViewById(v.getId());
        logListAdapter.addLog("WRITE : " + btn.getText().toString());
        if (Global.wbpProtocol.isConnected()) {
            switch (v.getId()) {
                case R.id.button1:
                    Global.wbpProtocol.readUsualModeHistoryData();
                    break;
                case R.id.button2:
                    Global.wbpProtocol.readDiagnosticModeHistoryData();
                    break;
                case R.id.button3://04h
                    Boolean clearUsualMode = selectedBOOL();
                    Boolean clearDiagnosticlMode = selectedBOOL();
                    Boolean clearNocturnalMode = selectedBOOL();
                    logListAdapter.addLog("WRITE : " + "clearHistoryData\nclearUsualMode：" + clearUsualMode + "\nclearDiagnosticlMode：" + clearDiagnosticlMode +"\nclearNocturnalMode：" + clearNocturnalMode);
                    Global.wbpProtocol.clearHistoryData(clearUsualMode,clearDiagnosticlMode,clearNocturnalMode);
                    break;
                case R.id.button4://05h
                    Global.wbpProtocol.clearCurrentModeHistoryData();
                    break;
                case R.id.button5:
                    Global.wbpProtocol.disconnectWBP();
                    break;
                case R.id.button6:
                    Global.wbpProtocol.writeDeviceTime();
                    break;
                case R.id.button7:
                    String userID = produceUserId();
                    logListAdapter.addLog("WRITE : " + "writeUserID：" + userID);
                    Global.wbpProtocol.writeUserID(userID);
                    break;
                case R.id.button8:
                    Global.wbpProtocol.readNocturnalModeSetting();
                    break;
                case R.id.button9:
                    Boolean openNocturnal = selectedBOOL();
                    Calendar today = Calendar.getInstance();
                    int YEAR = today.get(Calendar.YEAR);
                    int MONTH = today.get(Calendar.MONTH);
                    int DATE = today.get(Calendar.DATE);
                    int HOUR = selectedhour();
                    logListAdapter.addLog("WRITE : " + "changeNocturnalModeSetting open Nocturnal：" + openNocturnal + " YEAR：" + YEAR+ " MONTH：" + MONTH+ " DATE：" + DATE+ " HOUR：" + HOUR);
                    Global.wbpProtocol.changeNocturnalModeSetting(openNocturnal,YEAR,MONTH,DATE,HOUR);
                    break;
                case R.id.button11:
                    Global.wbpProtocol.readDeviceIDAndInfo();
                    break;
                case R.id.button12:
                    Global.wbpProtocol.readDeviceTime();
                    break;
                case R.id.button13:
                    Global.wbpProtocol.readUserAndVersionData();
                    break;
                case R.id.button14:
                    Global.wbpProtocol.readNocturnalModeHistoryData();
                    break;
            }
        }
    }

    public static String produceUserId() {
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
        return String.valueOf(stringBuilder);
    }

    public static  Boolean selectedBOOL() {
        Random ran = new Random();
        Boolean selectedBool = ran.nextInt(2) == 1;
        return selectedBool;
    }

    public static  int selectedhour() {
        int[] hours = {0,1,2,3,4,5,6,22,23};
        Random ran = new Random();
        int h = ran.nextInt(hours.length);
        return hours[h];
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
        BaseUtils.printLog("d",TAG, "1026 onScanResult:"+name+" mac:"+mac+" rssi:"+rssi);

        if (!name.startsWith("n/a")) {
            logListAdapter.addLog("onScanResult："+name+" mac:"+mac+" rssi:"+rssi);
        }

        //Blood pressure machine
        if (name.startsWith("WatchBP Home")) {
            if (isConnecting)
                return;
            isConnecting = true;

            //Stop scanning before connecting
            Global.wbpProtocol.stopScan();
            //Connection
            Global.wbpProtocol.connect(mac);
            logListAdapter.addLog("connect："+mac);

        }

    }

    @Override
    public void onConnectionState(WBPProtocol.ConnectState state) {
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

    @Override
    public void onResponseReadUsualModeHistory(DRecord dRecord, boolean isNullData) {
        logListAdapter.addLog("onResponseReadUsualModeHistory：" + dRecord + "\nis Null Data：" + isNullData);

    }

    @Override
    public void onResponseReadDiagnosticModeHistory(DiagnosticDRecord dRecord, boolean isNullData) {
        logListAdapter.addLog("onResponseReadDiagnosticModeHistory：" + dRecord + "\nis Null Data：" + isNullData);

    }

    @Override
    public void onResponseClearSelectedModeHistory(boolean isSuccess) {
        logListAdapter.addLog("onResponseClearSelectedModeHistory：" + isSuccess);

    }

    @Override
    public void onResponseClearCurrentModeHistory(boolean isSuccess) {
        logListAdapter.addLog("onResponseClearCurrentModeHistory：" + isSuccess);

    }

    @Override
    public void onResponseWriteDeviceTime(boolean isSuccess) {
        logListAdapter.addLog("onResponseWriteDeviceTime：" + isSuccess);

    }

    @Override
    public void onResponseWriteUserID(boolean isSuccess) {
        logListAdapter.addLog("onResponseWriteUserID：" + isSuccess);

    }

    @Override
    public void onResponseReadNocturnalModeSetting(DeviceInfo deviceInfo) {
        logListAdapter.addLog("onResponseReadNocturnalModeSetting：" + deviceInfo);

    }

    @Override
    public void onResponseChangeNocturnalModeSetting(boolean isSuccess) {
        logListAdapter.addLog("onResponseChangeNocturnalModeSetting：" + isSuccess);

    }

    @Override
    public void onResponseReadDeviceInfo(DeviceInfo deviceInfo) {
        logListAdapter.addLog("onResponseReadDeviceInfo：" + deviceInfo);

    }

    @Override
    public void onResponseReadDeviceTime(DeviceInfo deviceInfo) {
        logListAdapter.addLog("onResponseReadDeviceTime：" + deviceInfo);

    }

    @Override
    public void onResponseReadUserAndVersionData(User user, VersionData versionData) {
        logListAdapter.addLog("onResponseReadUser:：" + user + "\nVersionData：" + versionData);

    }

    @Override
    public void onResponseReadNocturnalPatternHistory(NocturnalModeDRecord dRecord, boolean isNullData) {
        logListAdapter.addLog("onResponseReadNocturnalPatternHistory：" + dRecord + "\nis Null Data：" + isNullData);

    }
}

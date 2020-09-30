package com.ideabus.sdk_test;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.ebelter.scaleblesdk.ScaleBleManager;
//import com.ideabus.model.protocol.BaseProtocol;
import com.ideabus.model.bluetooth.MyBluetoothLE;
import com.ideabus.model.data.CurrentAndMData;
import com.ideabus.model.data.DRecord;
import com.ideabus.model.data.DeviceInfo;
import com.ideabus.model.data.EBodyMeasureData;
import com.ideabus.model.data.ThermoMeasureData;
import com.ideabus.model.data.User;
import com.ideabus.model.data.VersionData;
import com.ideabus.model.protocol.BaseProtocol;
import com.ideabus.model.protocol.WBPProtocol;

import java.util.Map;
import java.util.Random;

public class ConnectionActivity extends AppCompatActivity implements BaseProtocol.OnConnectStateListener, BaseProtocol.OnDataResponseListener, BaseProtocol.OnNotifyStateListener, MyBluetoothLE.OnWriteStateListener {

    private static final String TAG = ConnectionActivity.class.getSimpleName();
//    private DeviceListAdapter deviceListAdapter;
//    private ListView deviceList;
    private TextView statusText;

    private ListView logList;
    private LogListAdapter logListAdapter;

    private boolean isConnecting;
    //If it is to MainActivity, then BLE not disconnect
    private boolean isGoMain;

    private String[] LocationPermission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int Location_Request = 1;

    private String btId = "00000000000";
    private String userName = "willys";
    private Integer age = 18;
    private Integer sex = 0;
    private float weight = 20f;
    private Integer height = 100;
    private float userImpedance = 244.0f;
    private int roleType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize the body ester machine Bluetooth module
//        ScaleBleManager.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        setUserInfo();

        initFragment();
        initView();
        initParam();
        initListener();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (logListAdapter == null && logList != null) {
            logListAdapter = new LogListAdapter(this);
            logList.setAdapter(logListAdapter);
        }

        //Start scan
        startScan();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //If it is to MainActivity, then BLE not disconnect
        if (Global.protocol.isConnected(BaseProtocol.DeviceType.DEVICE_TYPE_ALL))
            Global.protocol.disconnect(BaseProtocol.DeviceType.DEVICE_TYPE_ALL);
        if (logListAdapter != null) {
            logListAdapter.clear();
        }
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("需要給予權限，否則不能連接設備");
                builder.setPositiveButton("是",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ConnectionActivity.this, LocationPermission, Location_Request);
                    }
                });
                builder.setNeutralButton("否", null);
            } else {
                ActivityCompat.requestPermissions(ConnectionActivity.this, LocationPermission, Location_Request);
            }
        } else {
            initParam();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Global.protocol.isConnected(Global.protocol.deviceType.DEVICE_TYPE_BPM)) Global.protocol.disconnectBPM();
        Global.protocol.unbindDevice(Global.protocol.deviceType.DEVICE_TYPE_EBODY);
        Global.protocol.disconnect(Global.protocol.deviceType.DEVICE_TYPE_ALL);
        Global.protocol.stopScan(Global.protocol.deviceType.DEVICE_TYPE_ALL);
    }

    protected void initFragment() {

    }

    protected void initView() {

        logList = (ListView) findViewById(R.id.logList);
        statusText = (TextView) findViewById(R.id.statusText);
    }

    protected void initParam() {
        logListAdapter = new LogListAdapter(this);
        logList.setAdapter(logListAdapter);
        //Initialize the connection SDK
        Global.protocol = BaseProtocol.getInstance(this, false, true, Global.sdkid_BPM);
        Global.protocol.setOnConnectStateListener(this);
        Global.protocol.setOnDataResponseListener(this);
        Global.protocol.setOnNotifyStateListener(this);
        Global.protocol.setOnWriteStateListener(this);
    }


    protected void initListener() {
        logList.setOnItemClickListener(mOnItemClick);
        statusText.setOnClickListener(mOnClick);

    }


    private OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewID = v.getId();
            if (viewID == R.id.statusText) {
                if (Global.protocol.isScanning(BaseProtocol.DeviceType.DEVICE_TYPE_ALL)) {
                    Global.protocol.stopScan(BaseProtocol.DeviceType.DEVICE_TYPE_ALL);
                } else if (Global.protocol.isConnected(BaseProtocol.DeviceType.DEVICE_TYPE_BPM)) {
                    Global.protocol.disconnect(BaseProtocol.DeviceType.DEVICE_TYPE_BPM);
                }
                logListAdapter.clear();
                startScan();
            }
        }
    };

    private OnItemClickListener mOnItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    private void startScan() {
        if (!Global.protocol.isSupportBluetooth(BaseProtocol.DeviceType.DEVICE_TYPE_ALL)) {
            return;
        }
        statusText.setText("掃描中");
        Global.protocol.startScan(10,BaseProtocol.DeviceType.DEVICE_TYPE_ALL);
    }

    @Override
    public void onBtStateChanged(boolean isEnable) {
        if (isEnable) {
            Toast.makeText(this, "BLE is enable!!", Toast.LENGTH_SHORT).show();
            startScan();
        } else {
            Toast.makeText(this, "BLE is disable!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScanResult(String mac, String name, int rssi, BaseProtocol.DeviceType deviceType) {
        //When scanning BLE, it will be sent back, and then added to the list.
        Log.d(TAG, "onScanResult name = " + name);
        if (!name.startsWith("n/a")) {
            logListAdapter.addLog("onScanResult mac = " + mac + "\nname = " + name+ "\nrssi = " + rssi);
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
        logListAdapter.addLog("onScanResult mac = " + device.getAddress() + "\nname = " + device.getName());
        Global.protocol.stopScan(BaseProtocol.DeviceType.DEVICE_TYPE_EBODY);
        //Connection
        Global.protocol.connect(device,BaseProtocol.DeviceType.DEVICE_TYPE_EBODY);
    }

    @Override
    public void onConnectionState(BaseProtocol.ConnectState state, BaseProtocol.DeviceType deviceType) {
        //BLE connection status return, used to judge connection or disconnection
        logListAdapter.addLog("onConnectionState = " + state + "\nDeviceType = " + deviceType);
        switch (state) {
            case Connected:
                statusText.setText(deviceType + "：成功");
                break;
            case ConnectTimeout:
            case Disconnect:
                statusText.setText(deviceType + "：斷線");
                startScan();
                break;
            case ScanFinish:
                statusText.setText(deviceType + "：結束");
                startScan();
                break;
            case ScaleSleep:
                statusText.setText(deviceType + "：睡眠");
                startScan();
                break;
            case ScaleWake:
                statusText.setText(deviceType + "：啟動");
                sendUserInfoToScale();
                break;
        }
    }

    public void  setUserInfo() {
        Random ran = new Random();
        int[] name = new int[ran.nextInt(7)+3];
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < name.length; i++) {
            name[i] = (int) ((Math.random() * 26) + 65);
            nameBuilder.append((char)name[i]);
        }
        userName = String.valueOf(nameBuilder);
        int[] Id = new int[11];
        StringBuilder IdBuilder = new StringBuilder();
        for (int i = 0; i < name.length; i++) {
            Id[i] = (int) ((Math.random() * 10) + 48);
            IdBuilder.append((char)Id[i]);
        }
        btId = String.valueOf(IdBuilder);
        sex = ran.nextInt(2);
        age = 18+ran.nextInt(62);
        weight = 20+ran.nextInt(130);
        height = 100+ran.nextInt(120);
        roleType = ran.nextInt(2);
    }

    public void sendUserInfoToScale() {
        btId = Global.protocol.sendUserInfoToScale(userName, btId, age, sex, weight, height, userImpedance, roleType);
        logListAdapter.addLog("WRITE : sendUserInfoToScale >>\nName = "+ userName + "\nbtId = " + btId + "\nage = " + age + "\nsex = " + sex + "\nweight = " + weight + "\nheight = " + height + "\nImpedance = " + userImpedance + "\nroleType = " + roleType);
    }

    public void requestOfflineData() {
        Global.protocol.requestOfflineData(btId);
        logListAdapter.addLog("WRITE : requestOfflineData >> "+ btId);
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
        Global.protocol.disconnectBPM();
    }

    @Override
    public void onResponseClearHistory(boolean isSuccess) {
        logListAdapter.addLog("BPM : ClearHistory -> isSuccess = " + isSuccess);
    }

    @Override
    public void onResponseReadUserAndVersionData(User user, VersionData versionData) {
        logListAdapter.addLog("BPM : ReadUserAndVersionData -> user = " + user + " , versionData = " + versionData);
        Global.protocol.readHistorysOrCurrDataAndSyncTiming();
    }

    @Override
    public void onResponseWriteUser(boolean isSuccess) {
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
        logListAdapter.addLog("BPM : ReadDeviceInfo -> DeviceInfo = " + deviceInfo);
    }

    @Override
    public void onResponseReadDeviceTime(DeviceInfo deviceInfo) {
        logListAdapter.addLog("BPM : ResponseReadDeviceTime -> DeviceInfo = " + deviceInfo);
    }

    @Override
    public void onResponseWriteDeviceTime(boolean isSuccess) {
        logListAdapter.addLog("BPM : ResponseWriteDeviceTime -> isSuccess = " + isSuccess);
    }

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
        logListAdapter.addLog("EBODY : MeasureResult2 -> EBodyMeasureData = " + data);
    }

    @Override
    public void onResponseDeviceInfo(String macAddress, int workMode, float batteryVoltage) {
        logListAdapter.addLog("THERMO : DeviceInfo -> macAddress = " + macAddress + " , workMode = " + workMode + " , batteryVoltage = " + batteryVoltage);
    }

    @Override
    public void onResponseUploadMeasureData(ThermoMeasureData data) {
        logListAdapter.addLog("THERMO : UploadMeasureData -> ThermoMeasureData = " + data);
    }
}

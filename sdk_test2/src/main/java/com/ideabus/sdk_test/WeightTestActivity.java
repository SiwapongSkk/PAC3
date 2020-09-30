package com.ideabus.sdk_test;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ideabus.model.data.EBodyMeasureData;
import com.ideabus.model.protocol.EBodyProtocol;

import java.util.Calendar;
import java.util.Random;

public class WeightTestActivity extends AppCompatActivity implements EBodyProtocol.OnConnectStateListener,
        EBodyProtocol.OnDataResponseListener,  View.OnClickListener{

    private static final String TAG = "WeightTestActivity";

    private ListView weightListView;
    private Toolbar toolbar;
    private LogListAdapter logListAdapter;
    private boolean isConnecting;

    private Button btn1;
    private Button btn2;
    private Button btn3;

    private boolean isSendPersonParam;

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_test);
        setUserInfo();
        initView();
        initParam();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        weightListView = (ListView) findViewById(R.id.weight_list_view);

        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);

    }

    private void initParam() {
        setSupportActionBar(toolbar);

        //Initialize the connection SDK
        Global.eBodyProtocol = EBodyProtocol.getInstance(this, false, true, Global.sdkid_WEI);
        toolbar.setSubtitle("Body Composition "+Global.eBodyProtocol.getSDKVersion());

        logListAdapter = new LogListAdapter(this);
        weightListView.setAdapter(logListAdapter);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Global.eBodyProtocol.setOnConnectStateListener(this);
        Global.eBodyProtocol.setOnDataResponseListener(this);
        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (Global.eBodyProtocol.isConnected()) Global.eBodyProtocol.disconnect();
        Global.eBodyProtocol.stopScan();
    }

    private void startScan() {
        if (!Global.eBodyProtocol.isSupportBluetooth(this)) {
            return;
        }
        Global.eBodyProtocol.startScan();

        logListAdapter.addLog("startScan");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Global.eBodyProtocol.stopScan();
//        Global.eBodyProtocol.disconnect();
    }


    @Override
    public void onUserInfoUpdateSuccess() {
        logListAdapter.addLog("onUserInfoUpdateSuccess");

    }

    @Override
    public void onDeleteAllUsersSuccess() {
        logListAdapter.addLog("onDeleteAllUsersSuccess");

    }

    @Override
    public void onResponseMeasureResult2(EBodyMeasureData data, float impedance) {
        userImpedance = impedance;
        logListAdapter.addLog("EBODY : MeasureResult2 -> EBodyMeasureData = " + data);
        logListAdapter.addLog("EBODY : MeasureResult2 -> impedance = " + userImpedance);
    }

    @Override
    public void onScanResult(BluetoothDevice device) {
        logListAdapter.addLog("onScanResult："+device.getName()+" mac:"+device.getAddress());
        Global.eBodyProtocol.connect(device);
    }

    @Override
    public void onConnectionState(EBodyProtocol.ConnectState state) {
        //BLE connection status return, used to judge connection or disconnection
        switch (state) {
            case Connected:
                isConnecting = false;
                logListAdapter.addLog("Connected");
                break;
            case Disconnect:
                isConnecting = false;
                logListAdapter.addLog("Disconnected");
                isSendPersonParam = false;
                break;
            case ScaleSleep:
                logListAdapter.addLog("ScaleSleep");
                break;
            case ScaleWake:
                logListAdapter.addLog("ScaleWake");
                sendUserInfoToScale();
                requestOfflineData();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button)findViewById(v.getId());
        logListAdapter.addLog("WRITE : " + btn.getText().toString());
        switch (v.getId()) {
            case R.id.button1:
                sendUserInfoToScale();
                break;
            case R.id.button2:
                sendDelAllUser();
                break;
            case R.id.button3:
                requestOfflineData();
                break;
        }
    }

    public void sendUserInfoToScale() {
        if (Global.eBodyProtocol.isConnected()) {
            btId = Global.eBodyProtocol.sendUserInfoToScale(userName, btId, age, sex, weight, height, userImpedance, roleType);
        }
        logListAdapter.addLog("WRITE : sendUserInfoToScale >>\nName = "+ userName + "\nbtId = " + btId + "\nage = " + age + "\nsex = " + sex + "\nweight = " + weight + "\nheight = " + height + "\nImpedance = " + userImpedance + "\nroleType = " + roleType);
    }

    public void sendDelAllUser() {
        if (Global.eBodyProtocol.isConnected()) {
            Global.eBodyProtocol.sendDelAllUser();
        }
        setUserInfo();
        logListAdapter.addLog("WRITE : sendDelAllUser");
    }

    public void requestOfflineData() {
        if (Global.eBodyProtocol.isConnected()) {
            Global.eBodyProtocol.requestOfflineData(btId);
        }
        logListAdapter.addLog("WRITE : requestOfflineData >> "+ btId);
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

}

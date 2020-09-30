package com.ideabus.sdk_test;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.ideabus.ideabuslibrary.util.BaseUtils;
import com.ideabus.model.bluetooth.MyBluetoothLE;
import com.ideabus.model.data.CBPdataAndCalCBP;
import com.ideabus.model.data.DRecord;
import com.ideabus.model.data.DeviceInfo;
import com.ideabus.model.data.FunctionSettingValues;
import com.ideabus.model.data.SettingValues;
import com.ideabus.model.data.User;
import com.ideabus.model.data.VersionData;
import com.ideabus.model.protocol.WBO3Protocol;

public class WBO3TestActivity extends AppCompatActivity implements
        WBO3Protocol.OnConnectStateListener, View.OnClickListener, WBO3Protocol.OnDataResponseListener, WBO3Protocol.OnNotifyStateListener,  MyBluetoothLE.OnWriteStateListener {

    private String TAG = "WBO3TestActivity";

    private ListView bpmList;
    private LogListAdapter logListAdapter;

    private boolean isSendPersonParam;
    private Toolbar toolbar;
    private boolean isConnecting;

    private SettingValues aSettingValues = new SettingValues();


    EditText editUserID;
    EditText editCBPindex;
    EditText editABPMStart;
    EditText editABPMEnd;
    EditText editABPMInt_first;
    EditText editABPMInt_second;
    EditText editHI_infPressure;
    EditText editCBPInt_first;
    EditText editCBPInt_second;

    Switch SW_checkhide;
    Switch SW_SEL_silent;
    Switch SW_CBP_zone1_meas_off;
    Switch SW_CBP_zone2_meas_off;

    Spinner editCBPRaw;

    final String[] lunchCBPRaw = {"No CBP Raw", "Low CBP Raw", "Full CBP Raw"};

    CBPdataAndCalCBP.Dformat aDformat = CBPdataAndCalCBP.Dformat.NoCBPRaw;


    int SWCBPindexMax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize the body ester machine Bluetooth module

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wbo3test);

        initView();
        initParam();
        initListener();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        bpmList = (ListView) findViewById(R.id.bpm_list_view);

        editUserID = (EditText)findViewById(R.id.user_ID);

        editCBPindex = (EditText)findViewById(R.id.readCBP_index);

        editABPMStart = (EditText)findViewById(R.id.ABPMStart);
        editABPMEnd = (EditText)findViewById(R.id.ABPMEnd);
        editABPMInt_first = (EditText)findViewById(R.id.ABPMInt_first);
        editABPMInt_second = (EditText)findViewById(R.id.ABPMInt_second);
        editHI_infPressure = (EditText)findViewById(R.id.HI_infPressure);
        editCBPInt_first = (EditText)findViewById(R.id.CBPInt_first);
        editCBPInt_second = (EditText)findViewById(R.id.CBPInt_second);
        SW_checkhide = (Switch)findViewById(R.id.SW_checkhide);
        SW_SEL_silent = (Switch)findViewById(R.id.SW_SEL_silent);
        SW_CBP_zone1_meas_off = (Switch)findViewById(R.id.SW_CBP_zone1_meas_off);
        SW_CBP_zone2_meas_off = (Switch)findViewById(R.id.SW_CBP_zone2_meas_off);

        editCBPRaw = (Spinner)findViewById(R.id.readCBP_Raw);
        ArrayAdapter<String> lunchCBPRawList = new ArrayAdapter<>(WBO3TestActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                lunchCBPRaw);
        editCBPRaw.setAdapter(lunchCBPRawList);

        findViewById(R.id.buttonView).setVisibility(View.GONE);

    }

    private void initParam() {
        setSupportActionBar(toolbar);

        //Initialize the connection SDK
        Global.wbo3Protocol = WBO3Protocol.getInstance(this, false, true, Global.sdkid_WBP);

        toolbar.setSubtitle("WatchBP O3 " + Global.wbo3Protocol.getSDKVersion());


        logListAdapter = new LogListAdapter(this);
        bpmList.setAdapter(logListAdapter);

    }

    private void setSV() {
        editABPMStart.setText(String.valueOf(aSettingValues.getABPMStart()));
        editABPMEnd.setText(String.valueOf(aSettingValues.getABPMEnd()));
        editABPMInt_first.setText(String.valueOf(aSettingValues.getABPMInt_first()));
        editABPMInt_second.setText(String.valueOf(aSettingValues.getABPMInt_second()));
        editHI_infPressure.setText(String.valueOf(aSettingValues.getHI_infPressure()));
        editCBPInt_first.setText(String.valueOf(aSettingValues.getCBPInt_first()));
        editCBPInt_second.setText(String.valueOf(aSettingValues.getCBPInt_second()));
        SW_checkhide.setChecked(aSettingValues.isSW_checkhide());
        SW_SEL_silent.setChecked(aSettingValues.isSW_SEL_silent());
        SW_CBP_zone1_meas_off.setChecked(aSettingValues.isCBP_zone1_meas_off());
        SW_CBP_zone2_meas_off.setChecked(aSettingValues.isCBP_zone2_meas_off());
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
        findViewById(R.id.button11).setOnClickListener(this);
        findViewById(R.id.button12).setOnClickListener(this);
        findViewById(R.id.button13).setOnClickListener(this);
        findViewById(R.id.SW_CBP_zone2_meas_off).setOnClickListener(this);
        findViewById(R.id.SW_CBP_zone1_meas_off).setOnClickListener(this);
        findViewById(R.id.SW_SEL_silent).setOnClickListener(this);
        findViewById(R.id.SW_checkhide).setOnClickListener(this);
        editCBPRaw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        aDformat = CBPdataAndCalCBP.Dformat.NoCBPRaw;
                        break;
                    case 1:
                        aDformat = CBPdataAndCalCBP.Dformat.LowCBPRaw;
                        break;
                    case 2:
                        aDformat = CBPdataAndCalCBP.Dformat.FullCBPRaw;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "1026 onStart: " + Global.wbo3Protocol);
        super.onStart();
        Global.wbo3Protocol.setOnConnectStateListener(this);
        Global.wbo3Protocol.setOnDataResponseListener(this);
        Global.wbo3Protocol.setOnNotifyStateListener(this);
        Global.wbo3Protocol.setOnWriteStateListener(this);
        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Global.wbo3Protocol.isConnected()) Global.wbo3Protocol.disconnect();
        Global.wbo3Protocol.stopScan();
    }

    private void startScan() {
        if (!Global.wbo3Protocol.isSupportBluetooth(this)) {
            return;
        }
        logListAdapter.addLog("start scan");
        Global.wbo3Protocol.startScan(10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Global.wbo3Protocol.disconnect();
        Global.wbo3Protocol.stopScan();
    }

    @Override
    public void onClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //強制隱藏鍵盤
        Button btn = (Button)findViewById(view.getId());
        if (Global.wbo3Protocol.isConnected()) {
            switch (view.getId()) {
                case R.id.button1:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.readAllHistorys();
                    break;
                case R.id.button2:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.readSettingValues();
                    break;
                case R.id.button3:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.readBTModuleName();
                    break;
                case R.id.button4:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.readFunctionSettingValue();
                    break;
                case R.id.button5:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.disconnectWBO3();
                    break;
                case R.id.button6:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.readDeviceIDAndInfo();
                    break;
                case R.id.button7:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    if (SWCBPindexMax > 0) {
                        if (SWCBPindexMax >= Integer.parseInt(editCBPindex.getText().toString()) && Integer.parseInt(editCBPindex.getText().toString()) > 0) {
                            Global.wbo3Protocol.readCBPData(Integer.parseInt(editCBPindex.getText().toString()), aDformat);
                        } else {
                            Toast.makeText(WBO3TestActivity.this, "The number of errors is optional.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WBO3TestActivity.this, "Please read all history data first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.button8:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.clearAllHistorys();
                    break;
                case R.id.button9:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.writeUserID(editUserID.getText().toString());
                    break;
                case R.id.button10:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    aSettingValues.setABPMStart(Integer.parseInt(editABPMStart.getText().toString()));
                    aSettingValues.setABPMEnd(Integer.parseInt(editABPMEnd.getText().toString()));
                    aSettingValues.setABPMInt_first(Integer.parseInt(editABPMInt_first.getText().toString()));
                    aSettingValues.setABPMInt_second(Integer.parseInt(editABPMInt_second.getText().toString()));
                    aSettingValues.setHI_infPressure(Integer.parseInt(editHI_infPressure.getText().toString()));
                    aSettingValues.setCBPInt_first(Integer.parseInt(editCBPInt_first.getText().toString()));
                    aSettingValues.setCBPInt_second(Integer.parseInt(editCBPInt_second.getText().toString()));
                    Global.wbo3Protocol.writeSettingValues(aSettingValues);
                    break;
                case R.id.button11:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.readDeviceTime();
                    break;
                case R.id.button12:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.writeDeviceTime();
                    break;
                case R.id.button13:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wbo3Protocol.readUserAndVersionData();
                    break;
                case R.id.SW_CBP_zone2_meas_off:
                    aSettingValues.setCBP_zone2_meas_off(!aSettingValues.isCBP_zone2_meas_off());
                    logListAdapter.addLog("WRITE : setCBP_zone2_meas_off : " + aSettingValues.isCBP_zone2_meas_off());
                    break;
                case R.id.SW_CBP_zone1_meas_off:
                    aSettingValues.setCBP_zone1_meas_off(!aSettingValues.isCBP_zone1_meas_off());
                    logListAdapter.addLog("WRITE : setCBP_zone1_meas_off : " + aSettingValues.isCBP_zone1_meas_off());
                    break;
                case R.id.SW_SEL_silent:
                    aSettingValues.setSW_SEL_silent(!aSettingValues.isSW_SEL_silent());
                    logListAdapter.addLog("WRITE : setSW_SEL_silent : " + aSettingValues.isSW_SEL_silent());
                    break;
                case R.id.SW_checkhide:
                    aSettingValues.setSW_checkhide(!aSettingValues.isSW_checkhide());
                    logListAdapter.addLog("WRITE : setSW_checkhide : " + aSettingValues.isSW_checkhide());
                    break;
            }
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
        if (name.startsWith("WatchBP O3")) {
            if (isConnecting)
                return;
            isConnecting = true;
            //Stop scanning before connecting
            Global.wbo3Protocol.stopScan();
            //Connection
            Global.wbo3Protocol.bond(mac);
            logListAdapter.addLog("bond："+mac);
        }
    }

    @Override
    public void onConnectionState(WBO3Protocol.ConnectState state) {
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
                SWCBPindexMax = 0;
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
    public void onResponseReadAllHistorys(DRecord dRecord) {
        logListAdapter.addLog("onResponseReadAllHistorys：" + dRecord);
        SWCBPindexMax = dRecord.getMData().size();
        editCBPindex.setText(String.valueOf(SWCBPindexMax));
    }

    @Override
    public void onResponseReadCBPData(CBPdataAndCalCBP cRecord,boolean isNullData) {
        logListAdapter.addLog("onResponseReadCBPData：" + cRecord + "\nis Null Data：" + isNullData);
    }

    @Override
    public void onResponseClearHistorys(boolean isSuccess) {
        logListAdapter.addLog("onResponseClearHistorys：" + isSuccess);
    }

    @Override
    public void onResponseReadUserAndVersionData(User user, VersionData versionData) {
        logListAdapter.addLog("onResponseReadUser:：" + user + "\nVersionData：" + versionData);
        editUserID.setText(user.getID());
    }

    @Override
    public void onResponseWriteUserID(boolean isSuccess) {
        logListAdapter.addLog("onResponseWriteUserID：" + isSuccess);
    }

    @Override
    public void onResponseReadSettingValues(SettingValues settingValues) {
        logListAdapter.addLog("onResponseReadSettingValues：" + settingValues);
        aSettingValues.setABPMStart(settingValues.getABPMStart());
        aSettingValues.setABPMEnd(settingValues.getABPMEnd());
        aSettingValues.setABPMInt_first(settingValues.getABPMInt_first());
        aSettingValues.setABPMInt_second(settingValues.getABPMInt_second());
        aSettingValues.setHI_infPressure(settingValues.getHI_infPressure());
        aSettingValues.setCBP_zone2_meas_off(settingValues.isCBP_zone2_meas_off());
        aSettingValues.setCBP_zone1_meas_off(settingValues.isCBP_zone1_meas_off());
        aSettingValues.setSW_SEL_silent(settingValues.isSW_SEL_silent());
        aSettingValues.setSW_checkhide(settingValues.isSW_checkhide());
        aSettingValues.setCBPInt_first(settingValues.getCBPInt_first());
        aSettingValues.setCBPInt_second(settingValues.getCBPInt_second());
        setSV();
    }

    @Override
    public void onResponseWriteSettingValues(boolean isSuccess) {
        logListAdapter.addLog("onResponseWriteSettingValues：" + isSuccess);
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
    public void onResponseWriteDeviceTime(boolean isSuccess) {
        logListAdapter.addLog("onResponseWriteDeviceTime：" + isSuccess);
    }

    @Override
    public void onResponseReadFunctionSettingValues(FunctionSettingValues functionSettingValues) {
        logListAdapter.addLog("onResponseReadFunctionSettingValues：" + functionSettingValues);
    }

    @Override
    public void onResponseReadBTModuleName(String BTModuleName) {
        logListAdapter.addLog("onResponseReadBTModuleName：" + BTModuleName);
    }
}

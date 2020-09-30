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
import com.ideabus.model.data.CurrentAndMData;
import com.ideabus.model.data.DRecord;
import com.ideabus.model.data.DeviceInfo;
import com.ideabus.model.data.FunctionSettingValues;
import com.ideabus.model.data.SettingValues;
import com.ideabus.model.data.User;
import com.ideabus.model.data.VersionData;
import com.ideabus.model.protocol.WBOProtocol;

public class WBOTestActivity extends AppCompatActivity implements
        WBOProtocol.OnConnectStateListener, View.OnClickListener, WBOProtocol.OnDataResponseListener, WBOProtocol.OnNotifyStateListener,  MyBluetoothLE.OnWriteStateListener {

    private String TAG = "WBOTestActivity";

    private ListView bpmList;
    private LogListAdapter logListAdapter;

    private boolean isSendPersonParam;
    private Toolbar toolbar;
    private boolean isConnecting;

    private SettingValues aSettingValues = new SettingValues();

    EditText editUserID;
    EditText editCBPindex;
    EditText editAUS_HI_infPressure;
    EditText editHI_infPressure;
    EditText editRestTime;
    EditText editIntervalTime;
    EditText editAutoMeasureNumber;

    Switch SW_AUTO_hide;
    Switch SW_SEL_silent;
    Switch SW_AUS_Hide;
    Switch SW_AVG_no_include_first;
    Switch SW_CBP;
    Switch SW_AFib;
    Switch SW_AMPM;
    Switch SW_Kpa;

    Spinner editCBPRaw;

    final String[] lunchCBPRaw = {"No CBP Raw", "Low CBP Raw", "Full CBP Raw"};

    CBPdataAndCalCBP.Dformat aDformat = CBPdataAndCalCBP.Dformat.NoCBPRaw;

    int SWCBPindexMax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize the body ester machine Bluetooth module

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wbotest);

        initView();
        initParam();
        initListener();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        bpmList = (ListView) findViewById(R.id.bpm_list_view);

        editUserID = (EditText)findViewById(R.id.user_ID);

        editCBPindex = (EditText)findViewById(R.id.readCBP_index);

        editAUS_HI_infPressure = (EditText)findViewById(R.id.AUS_HI_infPressure);
        editHI_infPressure = (EditText)findViewById(R.id.HI_infPressure);
        SW_AUTO_hide = (Switch)findViewById(R.id.SW_AUTO_hide);
        SW_SEL_silent = (Switch)findViewById(R.id.SW_SEL_silent);
        SW_AUS_Hide = (Switch)findViewById(R.id.SW_AUS_Hide);
        SW_AVG_no_include_first = (Switch)findViewById(R.id.SW_AVG_no_include_first);
        SW_CBP = (Switch)findViewById(R.id.SW_CBP);
        SW_AFib = (Switch)findViewById(R.id.SW_AFib);
        SW_AMPM = (Switch)findViewById(R.id.SW_AMPM);
        SW_Kpa = (Switch)findViewById(R.id.SW_Kpa);
        editRestTime = (EditText)findViewById(R.id.RestTime);
        editIntervalTime = (EditText)findViewById(R.id.IntervalTime);
        editAutoMeasureNumber = (EditText)findViewById(R.id.AutoMeasureNumber);

        editCBPRaw = (Spinner)findViewById(R.id.readCBP_Raw);
        ArrayAdapter<String> lunchCBPRawList = new ArrayAdapter<>(WBOTestActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                lunchCBPRaw);
        editCBPRaw.setAdapter(lunchCBPRawList);

        findViewById(R.id.buttonView).setVisibility(View.GONE);

    }

    private void initParam() {
        setSupportActionBar(toolbar);

        //Initialize the connection SDK
        Global.wboProtocol = WBOProtocol.getInstance(this, false, true, Global.sdkid_WBP);

        toolbar.setSubtitle("WatchBP Office " + Global.wboProtocol.getSDKVersion());

        logListAdapter = new LogListAdapter(this);
        bpmList.setAdapter(logListAdapter);

    }

    private void setSV() {
        editAUS_HI_infPressure.setText(String.valueOf(aSettingValues.getAUS_HI_infPressure()));
        editHI_infPressure.setText(String.valueOf(aSettingValues.getHI_infPressure()));

        SW_AUTO_hide.setChecked(aSettingValues.isSW_AUTO_hide());
        SW_SEL_silent.setChecked(aSettingValues.isSW_SEL_silent());
        SW_AUS_Hide.setChecked(aSettingValues.isSW_AUS_Hide());
        SW_AVG_no_include_first.setChecked(aSettingValues.isSW_AVG_no_include_first());
        SW_CBP.setChecked(aSettingValues.isSW_CBP());
        SW_AFib.setChecked(aSettingValues.isSW_AFib());
        SW_AMPM.setChecked(aSettingValues.isSW_AMPM());
        SW_Kpa.setChecked(aSettingValues.isSW_Kpa());

        editRestTime.setText(String.valueOf(aSettingValues.getRestTime()));
        editIntervalTime.setText(String.valueOf(aSettingValues.getIntervalTime()));
        editAutoMeasureNumber.setText(String.valueOf(aSettingValues.getAutoMeasureNumber()));

    }

    private boolean checkSV() {
        aSettingValues.setAUS_HI_infPressure(Integer.parseInt(editAUS_HI_infPressure.getText().toString()));
        aSettingValues.setHI_infPressure(Integer.parseInt(editHI_infPressure.getText().toString()));
        
        aSettingValues.setRestTime(Integer.parseInt(editRestTime.getText().toString()));
        aSettingValues.setIntervalTime(Integer.parseInt(editIntervalTime.getText().toString()));
        aSettingValues.setAutoMeasureNumber(Integer.parseInt(editAutoMeasureNumber.getText().toString()));

        if (!aSettingValues.checkSettingValues()) {
            Toast.makeText(WBOTestActivity.this, "Wrong setting value.", Toast.LENGTH_SHORT).show();
        }
        return aSettingValues.checkSettingValues();
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
        findViewById(R.id.button25).setOnClickListener(this);
        findViewById(R.id.button26).setOnClickListener(this);

        findViewById(R.id.SW_AUTO_hide).setOnClickListener(this);
        findViewById(R.id.SW_SEL_silent).setOnClickListener(this);
        findViewById(R.id.SW_AUS_Hide).setOnClickListener(this);
        findViewById(R.id.SW_AVG_no_include_first).setOnClickListener(this);
        findViewById(R.id.SW_CBP).setOnClickListener(this);
        findViewById(R.id.SW_AFib).setOnClickListener(this);
        findViewById(R.id.SW_AMPM).setOnClickListener(this);
        findViewById(R.id.SW_Kpa).setOnClickListener(this);

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
        Log.d(TAG, "1026 onStart: " + Global.wboProtocol);
        super.onStart();
        Global.wboProtocol.setOnConnectStateListener(this);
        Global.wboProtocol.setOnDataResponseListener(this);
        Global.wboProtocol.setOnNotifyStateListener(this);
        Global.wboProtocol.setOnWriteStateListener(this);
        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Global.wboProtocol.isConnected()) Global.wboProtocol.disconnect();
        Global.wboProtocol.stopScan();
    }

    private void startScan() {
        if (!Global.wboProtocol.isSupportBluetooth(this)) {
            return;
        }
        logListAdapter.addLog("start scan");
        Global.wboProtocol.startScan(10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Global.wboProtocol.disconnect();
        Global.wboProtocol.stopScan();
    }

    @Override
    public void onClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //強制隱藏鍵盤
        Button btn = (Button)findViewById(view.getId());
        if (Global.wboProtocol.isConnected()) {
            switch (view.getId()) {
                case R.id.button1:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.readAllHistorys();
                    break;
                case R.id.button2:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.readSettingValues();
                    break;
                case R.id.button3:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.readBTModuleName();
                    break;
                case R.id.button4:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.readFunctionSettingValue();
                    break;
                case R.id.button5:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.disconnectWBO();
                    break;
                case R.id.button6:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.readDeviceIDAndInfo();
                    break;
                case R.id.button7:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    if (SWCBPindexMax > 0) {
                        if (SWCBPindexMax >= Integer.parseInt(editCBPindex.getText().toString()) && Integer.parseInt(editCBPindex.getText().toString()) > 0) {
                            Global.wboProtocol.readCBPData(Integer.parseInt(editCBPindex.getText().toString()), aDformat);
                        } else {
                            Toast.makeText(WBOTestActivity.this, "The number of errors is optional.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WBOTestActivity.this, "Please read all history data first", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.button8:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.clearAllHistorys();
                    break;
                case R.id.button9:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.writeUserID(editUserID.getText().toString());
                    break;
                case R.id.button10:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    if (checkSV()) {
                        Global.wboProtocol.writeSettingValues(aSettingValues);
                    }
                    break;
                case R.id.button11:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.readDeviceTime();
                    break;
                case R.id.button12:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.writeDeviceTime();
                    break;
                case R.id.button13:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.readUserAndVersionData();
                    break;
                case R.id.button25:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.startRemoteMeasurement(aDformat);
                    break;
                case R.id.button26:
                    logListAdapter.addLog("WRITE : " + btn.getText().toString());
                    Global.wboProtocol.stopRemoteMeasurement();
                    break;
                case R.id.SW_AUTO_hide:
                    aSettingValues.setSW_AUTO_hide(!aSettingValues.isSW_AUTO_hide());
                    checkSV();
                    logListAdapter.addLog("WRITE : setSW_AUTO_hide : " + aSettingValues.isSW_AUTO_hide());
                    break;
                case R.id.SW_SEL_silent:
                    aSettingValues.setSW_SEL_silent(!aSettingValues.isSW_SEL_silent());
                    checkSV();
                    logListAdapter.addLog("WRITE : setSW_SEL_silent : " + aSettingValues.isSW_SEL_silent());
                    break;
                case R.id.SW_AUS_Hide:
                    aSettingValues.setSW_AUS_Hide(!aSettingValues.isSW_AUS_Hide());
                    checkSV();
                    logListAdapter.addLog("WRITE : setSW_AUS_Hide : " + aSettingValues.isSW_AUS_Hide());
                    break;
                case R.id.SW_AVG_no_include_first:
                    aSettingValues.setSW_AVG_no_include_first(!aSettingValues.isSW_AVG_no_include_first());
                    checkSV();
                    logListAdapter.addLog("WRITE : setSW_AVG_no_include_first : " + aSettingValues.isSW_AVG_no_include_first());
                    break;
                case R.id.SW_CBP:
                    aSettingValues.setSW_CBP(!aSettingValues.isSW_CBP());
                    checkSV();
                    logListAdapter.addLog("WRITE : setSW_CBP : " + aSettingValues.isSW_CBP());
                    break;
                case R.id.SW_AFib:
                    aSettingValues.setSW_AFib(!aSettingValues.isSW_AFib());
                    checkSV();
                    logListAdapter.addLog("WRITE : setSW_AFib : " + aSettingValues.isSW_AFib());
                    break;
                case R.id.SW_AMPM:
                    aSettingValues.setSW_AMPM(!aSettingValues.isSW_AMPM());
                    checkSV();
                    logListAdapter.addLog("WRITE : setSW_AMPM : " + aSettingValues.isSW_AMPM());
                    break;
                case R.id.SW_Kpa:
                    aSettingValues.setSW_Kpa(!aSettingValues.isSW_Kpa());
                    checkSV();
                    logListAdapter.addLog("WRITE : setSW_Kpa : " + aSettingValues.isSW_Kpa());
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
        if (name.startsWith("WatchBP Office")) {
            if (isConnecting)
                return;
            isConnecting = true;
            //Stop scanning before connecting
            Global.wboProtocol.stopScan();
            //Connection
            Global.wboProtocol.bond(mac);
            logListAdapter.addLog("bond："+mac);
        }
    }

    @Override
    public void onConnectionState(WBOProtocol.ConnectState state) {
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
    public void onResponseReadCBPData(CBPdataAndCalCBP cRecord, boolean isNullData) {
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
        aSettingValues.setAUS_HI_infPressure(settingValues.getAUS_HI_infPressure());
        aSettingValues.setHI_infPressure(settingValues.getHI_infPressure());

        aSettingValues.setSW_AUTO_hide(settingValues.isSW_AUTO_hide());
        aSettingValues.setSW_SEL_silent(settingValues.isSW_SEL_silent());
        aSettingValues.setSW_AUS_Hide(settingValues.isSW_AUS_Hide());
        aSettingValues.setSW_AVG_no_include_first(settingValues.isSW_AVG_no_include_first());
        aSettingValues.setSW_CBP(settingValues.isSW_CBP());
        aSettingValues.setSW_AFib(settingValues.isSW_AFib());
        aSettingValues.setSW_AMPM(settingValues.isSW_AMPM());
        aSettingValues.setSW_Kpa(settingValues.isSW_Kpa());

        aSettingValues.setRestTime(settingValues.getRestTime());
        aSettingValues.setIntervalTime(settingValues.getIntervalTime());
        aSettingValues.setAutoMeasureNumber(settingValues.getAutoMeasureNumber());

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

    @Override
    public void onResponseStartRemoteMeasurement(CBPdataAndCalCBP.Dformat dformat) {
        logListAdapter.addLog("onResponseStartRemoteMeasurement：" + dformat);
    }

    @Override
    public void onResponseRemoteMeasurementStatusEvery5seconds(WBOProtocol.STATUS status, int measurementNumber, int totalMeasurementNumber, int countdown, int totalMeasuretime) {
        logListAdapter.addLog("onResponseRemoteMeasurementStatusEvery5seconds：" + status + "\nmeasurementNumber：" + measurementNumber + "\ntotalMeasurementNumber：" + totalMeasurementNumber + "\ncountdown：" + countdown + "\ntotalMeasuretime：" + totalMeasuretime);

    }

    @Override
    public void onResponseMeasurementResultsForEachMeasurement(CurrentAndMData dRecord, int historyMeasuremeNumber, int currentMeasurementTimes, boolean isAverage) {
        logListAdapter.addLog("onResponseMeasurementResultsForEachMeasurement：" + dRecord + "\nhistoryMeasuremeNumber：" + historyMeasuremeNumber + "\ncurrentMeasurementTimes：" + currentMeasurementTimes + "\nisAverage：" + isAverage);

    }
}

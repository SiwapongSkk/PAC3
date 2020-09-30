package com.ideabus.sdk_test;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import com.ideabus.model.XlogUtils;
import com.ideabus.model.protocol.BaseProtocol;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ChoseActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_bpm;
    private Button btn_weight;
    private Button btn_bt;
    private Button btn_wbp;
    private Button btn_wbo3;
    private Button btn_wbo;
    private Button btn_sp;
    private Toolbar toolbar;

    private String[] LocationPermission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int Location_Request = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose);
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("需要給予權限，否則不能連接設備");
                builder.setPositiveButton("是",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ChoseActivity.this, LocationPermission, Location_Request);
                    }
                });
                builder.setNeutralButton("否", null);

/*
                Snackbar.make(coordinatorLayout, "需要給予權限，否則不能連接設備", Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString("確定"), v -> {
                            ActivityCompat.requestPermissions(ChoseActivity.this, LocationPermission, Location_Request);
                        }).show();
*/
            } else {
                ActivityCompat.requestPermissions(ChoseActivity.this, LocationPermission, Location_Request);
            }

        } else {
            initParam();
        }
    }

    private void initParam() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        btn_bpm = (Button) findViewById(R.id.btn_bpm);
        btn_weight = (Button) findViewById(R.id.btn_eBody);
        btn_bt = (Button) findViewById(R.id.btn_bt);
        btn_wbp = (Button) findViewById(R.id.btn_wbp);
        btn_wbo3 = (Button) findViewById(R.id.btn_wbo3);
        btn_wbo = (Button) findViewById(R.id.btn_wbo);
        btn_sp = (Button) findViewById(R.id.btn_sp);

        btn_bpm.setOnClickListener(this);
        btn_weight.setOnClickListener(this);
        btn_bt.setOnClickListener(this);
        btn_wbp.setOnClickListener(this);
        btn_wbo3.setOnClickListener(this);
        btn_wbo.setOnClickListener(this);
        btn_sp.setOnClickListener(this);

        toolbar.setSubtitle("V" + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")");


        setSupportActionBar(toolbar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Location_Request && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            initParam();
        } else {
            finish();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bpm:
                startActivity(new Intent(this, BPMTestActivity.class));
                break;
            case R.id.btn_eBody:
                startActivity(new Intent(this, WeightTestActivity.class));
                break;
            case R.id.btn_bt:
                startActivity(new Intent(this, BtTestActivity.class));
                break;
            case R.id.btn_wbp:
                startActivity(new Intent(this, WBPTestActivity.class));
                break;
            case R.id.btn_wbo3:
                startActivity(new Intent(this, WBO3TestActivity.class));
                break;
            case R.id.btn_wbo:
                startActivity(new Intent(this, WBOTestActivity.class));
                break;
            case R.id.btn_sp:
                String SDK_Version = "V" +BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")";
                Uri logUri = null;
                if (Global.bpmProtocol != null) {
                    logUri = Global.bpmProtocol.getLogZip(BuildConfig.APPLICATION_ID);
                } else if (Global.thermoProtocol != null) {
                    logUri = Global.thermoProtocol.getLogZip(BuildConfig.APPLICATION_ID);
                } else if (Global.eBodyProtocol != null) {
                    logUri =Global.eBodyProtocol.getLogZip(BuildConfig.APPLICATION_ID);
                } else if (Global.wbpProtocol != null) {
                    logUri = Global.wbpProtocol.getLogZip(BuildConfig.APPLICATION_ID);
                } else if (Global.wboProtocol != null) {
                    logUri = Global.wboProtocol.getLogZip(BuildConfig.APPLICATION_ID);
                    SDK_Version = Global.wboProtocol.getSDKVersion();
                } else if (Global.wbo3Protocol != null) {
                    logUri = Global.wbo3Protocol.getLogZip(BuildConfig.APPLICATION_ID);
                }
                if (logUri != null) {
                    Log.d("logUri", String.valueOf(logUri));
                    //send Support Mail to Microlife
                    if (Global.bpmProtocol != null) {
                        Global.bpmProtocol.sendSupportMail("SDK DEMO",SDK_Version,BuildConfig.APPLICATION_ID);
                    } else if (Global.thermoProtocol != null) {
                        Global.thermoProtocol.sendSupportMail("SDK DEMO",SDK_Version,BuildConfig.APPLICATION_ID);
                    } else if (Global.eBodyProtocol != null) {
                        Global.eBodyProtocol.sendSupportMail("SDK DEMO",SDK_Version,BuildConfig.APPLICATION_ID);
                    } else if (Global.wbpProtocol != null) {
                        Global.wbpProtocol.sendSupportMail("SDK DEMO",SDK_Version,BuildConfig.APPLICATION_ID);
                    } else if (Global.wboProtocol != null) {
                        Global.wboProtocol.sendSupportMail("SDK DEMO",SDK_Version,BuildConfig.APPLICATION_ID);
                    } else if (Global.wbo3Protocol != null) {
                        Global.wbo3Protocol.sendSupportMail("SDK DEMO",SDK_Version,BuildConfig.APPLICATION_ID);
                    }
                }
                break;
        }
    }
}

package com.example.wifisimpleapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.graphics.Color.RED;

public class MainActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 123;

    private WifiManager wifiManager;

    private WifiBroadcastReceiver wifiReceiver;

    private TextView tvWifiState;
    private Button btnScan;
    private Button btnCheckWifiState;

    private RecyclerView rvScanList;
    private MyAdapter myAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvScanList = findViewById(R.id.rvScanList);
        tvWifiState =  findViewById(R.id.tvWifiState);
        btnScan =  findViewById(R.id.btnScan);
        btnCheckWifiState =  findViewById(R.id.btnCheckWifiState);

        btnCheckWifiState.setBackgroundColor(Color.CYAN);
        btnCheckWifiState.setTextColor(Color.WHITE);

        btnScan.setClickable(Boolean.FALSE);
        btnScan.setBackgroundColor(Color.GRAY);
        btnScan.setTextColor(Color.BLACK);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiBroadcastReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }


    @Override
    protected void onStop()  {
        this.unregisterReceiver(this.wifiReceiver);
        super.onStop();
    }

    public void checkWifiState(View view){
        String state = showWifiState();
        if(state.equalsIgnoreCase("Enabled")){
            tvWifiState.setText("ON");
            tvWifiState.setBackgroundColor(Color.GREEN);

            btnScan.setClickable(Boolean.TRUE);
            btnScan.setBackgroundColor(Color.CYAN);
            btnScan.setTextColor(Color.WHITE);
        }
        else if(state.equalsIgnoreCase("Disabled")){
            tvWifiState.setText("OFF");
            tvWifiState.setBackgroundColor(RED);

            btnScan.setClickable(Boolean.FALSE);
            btnScan.setBackgroundColor(Color.GRAY);
            btnScan.setTextColor(Color.BLACK);
        }
        else{
            tvWifiState.setText("UNKNOWN");
            tvWifiState.setBackgroundColor(Color.GRAY);
        }
    }

    private String showWifiState()  {
        int state = this.wifiManager.getWifiState();
        String statusInfo = "Unknown";

        switch (state)  {
            case WifiManager.WIFI_STATE_DISABLED:
                statusInfo = "Disabled";
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                statusInfo = "Enabled";
                break;
            default:
                statusInfo = "Unknown";
                break;
        }
        return statusInfo;
    }


    public void scanWifi(View view){
        askAndStartScanWifi();
    }

    private void askAndStartScanWifi()  {
        // With Android Level >= 23, you have to ask the user
        // for permission to Call.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23
            int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            // Check for permissions
            if (permission1 != PackageManager.PERMISSION_GRANTED) {
                // Request permissions
                ActivityCompat.requestPermissions(this,
                        new String[] {
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE
                        }, MY_REQUEST_CODE);
                return;
            }
        }
        this.doStartScanWifi();
    }

    private void doStartScanWifi()  {
        this.wifiManager.startScan();
    }


    class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)   {
            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (ok)  {
                List<ScanResult> listScan = wifiManager.getScanResults();
                myAdapter = new MyAdapter(MainActivity.this, listScan);
                rvScanList.setAdapter(myAdapter);
                rvScanList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

            }  else {
                Toast.makeText(MainActivity.this, "Can't find any Wifi: " , Toast.LENGTH_LONG).show();
            }

        }
    }

}
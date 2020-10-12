package com.example.wifisimpleapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    private LinearLayout llScan;
    private TextView tvWifiState;
    private Button btnScan;
    private Button btnCheckWifiState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        this.wifiReceiver = new WifiBroadcastReceiver();

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        this.tvWifiState =  this.findViewById(R.id.tvWifiState);
        this.btnScan =  this.findViewById(R.id.btnScan);
        this.btnCheckWifiState =  this.findViewById(R.id.btnCheckWifiState);
        this.llScan = this.findViewById(R.id.llScan);

    }

    class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)   {
            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (ok)  {
                List<ScanResult> list = wifiManager.getScanResults();
                MainActivity.this.showNetworks(list);
            }  else {
                Toast.makeText(MainActivity.this, "Can't find any Wifi: " , Toast.LENGTH_LONG).show();
            }

        }
    }

    private void showNetworks(List<ScanResult> results) {
        this.llScan.removeAllViews();

        for( final ScanResult result: results)  {
            final String networkSSID = result.SSID; // Network Name.
            //
            Button button = new Button(this );

            button.setText(networkSSID);
            this.llScan.addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //connect to network
                }
            });
        }
    }



    @Override
    protected void onStop()  {
        this.unregisterReceiver(this.wifiReceiver);
        super.onStop();
    }

    public void check(View view){
        String state = showWifiState();
        if(state.equalsIgnoreCase("Enabled")){
            tvWifiState.setText("ON");
            tvWifiState.setBackgroundColor(Color.GREEN);
        }
        else if(state.equalsIgnoreCase("Disabled")){
            tvWifiState.setText("OFF");
            tvWifiState.setBackgroundColor(RED);
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


    public void scan(View view){
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

//    private void connectToNetwork(String networkCapabilities, String networkSSID)  {
//        Toast.makeText(this, "Connecting to network: " + networkSSID, Toast.LENGTH_SHORT).show();
//
//        String networkPass = this.editTextPassword.getText().toString();
//        //
//        WifiConfiguration wifiConfig = new WifiConfiguration();
//        wifiConfig.SSID = "\"" + networkSSID + "\"";
//
//        if (networkCapabilities.toUpperCase().contains("WEP")) { // WEP Network.
//            Toast.makeText(this, "WEP Network", Toast.LENGTH_SHORT).show();
//
//            wifiConfig.wepKeys[0] = "\"" + networkPass + "\"";
//            wifiConfig.wepTxKeyIndex = 0;
//            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//        } else if (networkCapabilities.toUpperCase().contains("WPA")) { // WPA Network
//            Toast.makeText(this, "WPA Network", Toast.LENGTH_SHORT).show();
//            wifiConfig.preSharedKey = "\"" + networkPass + "\"";
//        } else { // OPEN Network.
//            Toast.makeText(this, "OPEN Network", Toast.LENGTH_SHORT).show();
//            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        }
//
//        this.wifiManager.addNetwork(wifiConfig);
//
//        List<WifiConfiguration> list = this.wifiManager.getConfiguredNetworks();
//        for (WifiConfiguration config : list) {
//            if (config.SSID != null && config.SSID.equals("\"" + networkSSID + "\"")) {
//                this.wifiManager.disconnect();
//                this.wifiManager.enableNetwork(config.networkId, true);
//                this.wifiManager.reconnect();
//                break;
//            }
//        }
//    }
}
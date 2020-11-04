package com.example.wifisimpleapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import static android.provider.Settings.Global.getString;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.WifiListItemViewHolder> {
    private Activity activity;
    private Context context;
    private List<ScanResult> listScan;
    private Button btnOK;
    private TextView tvWifiNameDetail;
    private TextView tvWifiSignalDetail;
    private EditText etPassword;

    private boolean isConnected = false;

    public MyAdapter(Context context, List<ScanResult> listScan) {
        this.context = context;
        this.listScan = listScan;
    }

    //create new view
    @NonNull
    @Override
    public WifiListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View wifiView = inflater.inflate(R.layout.item_recycler, parent, false);
        return new WifiListItemViewHolder(wifiView);
    }

    //replace content
    @Override
    public void onBindViewHolder(@NonNull final WifiListItemViewHolder holder, int position) {
        final ScanResult result = listScan.get(position);
        holder.tvWifiName.setText(result.SSID);


        /*
        Excellent >-50 dBm
        Good -50 to -60 dBm
        Fair -60 to -70 dBm
        Weak < -70 dBm
        */
        final int signal = result.level;
        if (signal > -50) {
            holder.imvWifiStatus.setImageResource(R.drawable.wifi_strong);
        } else if ((signal <= -50) && (signal > -60)) {
            holder.imvWifiStatus.setImageResource(R.drawable.wifi_medium);
        } else {
            holder.imvWifiStatus.setImageResource(R.drawable.wifi_weak);
        }

        holder.setItemClickListener(new WifiItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                String wifiName = holder.tvWifiName.getText().toString();

                // function : dialog show wifi details : wifi name & signal strength
                //showWifiDetailDialog(wifiName, signal);

                connectWifiDialog(result, wifiName);

                if(isConnected == Boolean.TRUE){
//                    holder.tvWifiName.setBackgroundColor(Color.CYAN);
//                    holder.tvWifiName.setTextColor(Color.BLACK);
                    view.setSelected(true);
                }
                else{
                    holder.tvWifiName.setBackgroundColor(Color.BLACK);
                    holder.tvWifiName.setTextColor(Color.WHITE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listScan.size();
    }

    public class WifiListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvWifiName;
        private ImageView imvWifiStatus;
        private WifiItemClickListener wifiItemClickListener;

        public WifiListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWifiName = itemView.findViewById(R.id.tvWifiName);
            imvWifiStatus = itemView.findViewById(R.id.imvWifiStatus);
            tvWifiName.setBackgroundColor(Color.BLACK);
            tvWifiName.setTextColor(Color.WHITE);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(WifiItemClickListener wifiItemClickListener) {
            this.wifiItemClickListener = wifiItemClickListener;
        }

        @Override
        public void onClick(View v) {
            wifiItemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    public void connectWifiDialog(final ScanResult result, String wifiName) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.connect_selected_wifi);

        btnOK = dialog.findViewById(R.id.btnok);

        Window view = dialog.getWindow();
        view.setBackgroundDrawableResource(R.drawable.red_border);

        tvWifiNameDetail = dialog.findViewById(R.id.tvSelectedWifiName);
        etPassword = dialog.findViewById(R.id.etPassword);

        tvWifiNameDetail.setText(wifiName);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectSelectedWifi(result, etPassword.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //--------------using WifiConfiguration -->> deprecated in API lvl 29------------------------
    //----------------------targeted API in this project is 28-----------------------------------
    private void connectSelectedWifi(ScanResult res, String password) {
        Context context = this.context.getApplicationContext();
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", res.SSID);
        wifiConfig.BSSID = res.BSSID;
        wifiConfig.priority = 40;
        if (res.capabilities.toLowerCase().contains("wep")) {
            wifiConfig.wepKeys[0] = String.format("\"%s\"", password);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        } else if (res.capabilities.toLowerCase().contains("wpa")) {
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.preSharedKey = String.format("\"%s\"", password);
        }


        if (password.equalsIgnoreCase(""))
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean isConfigured = false;

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + res.SSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    isConfigured = true;
                    isConnected = Boolean.TRUE;
                    break;
                }
            }
            //adding the network
            if (!isConfigured) {
                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.saveConfiguration();
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);

            }
        }



    }

    public void showWifiDetailDialog(String wifiName, int signal) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.wifi_custom_dialog);

        btnOK = dialog.findViewById(R.id.btnok);

        dialog.setCanceledOnTouchOutside(false);
        Window view = dialog.getWindow();
        view.setBackgroundDrawableResource(R.drawable.red_border);

        tvWifiNameDetail = dialog.findViewById(R.id.tvWifiNameDetail);
        tvWifiSignalDetail = dialog.findViewById(R.id.tvWifiSignalDetail);

        tvWifiNameDetail.setText(wifiName);
        tvWifiSignalDetail.setText(String.valueOf(signal));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}

package com.example.wifisimpleapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import static android.provider.Settings.Global.getString;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.WifiListItemViewHolder> {
    private Context context;
    private List<ScanResult> listScan;
    private Button btnOK;
    private TextView tvWifiNameDetail;
    private TextView tvWifiSignalDetail;

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
        ScanResult result = listScan.get(position);
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
//                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//
//                dialog.setTitle(context.getString(R.string.arlertTitle));
//                dialog.setMessage(Html.fromHtml("<b>" + context.getString(R.string.wifiName) + "</b>" + holder.tvWifiName.getText() + "<br>"
//                        + "<b>" + context.getString(R.string.wifiSignal) + "</b>" + signal));
//
//                dialog.setPositiveButton(context.getString(R.string.arlertOK), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//
//                AlertDialog wifiDialog = dialog.create();
//                wifiDialog.show();\
                String wifiName = holder.tvWifiName.getText().toString();
                showWifiDetailDialog(wifiName, signal);
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

    public void showWifiDetailDialog(String wifiName, int signal){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.wifi_custom_dialog);

        btnOK = (Button) dialog.findViewById(R.id.btnok);

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

package com.example.wifisimpleapplication;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context context;
    private List<ScanResult> list;

    public MyAdapter(Context context, List<ScanResult> list) {
        this.context = context;
        this.list = list;
    }


    //create new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View wifiView = inflater.inflate(R.layout.item_recycler, parent, false);
        ViewHolder viewHolder = new ViewHolder(wifiView);
        return viewHolder;
    }

    //replace content
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult result = list.get(position);
        String networkSSID = result.SSID;
        holder.btnWifi.setText(networkSSID);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button btnWifi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnWifi = itemView.findViewById(R.id.btnWifi);
            btnWifi.setBackgroundColor(Color.WHITE);
            btnWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnWifi.setBackgroundColor(Color.BLUE);
                }
            });

        }
    }
}

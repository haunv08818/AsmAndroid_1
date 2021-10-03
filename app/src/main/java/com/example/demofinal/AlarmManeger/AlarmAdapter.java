package com.example.demofinal.AlarmManeger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demofinal.R;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {


    private ArrayList<AlarmEntity> alarmList;
    public AlarmAdapter(ArrayList<AlarmEntity> alarmList){
        this.alarmList = alarmList;
    }
    private OnItemClickListener mListener;


    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.item_alarm, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmEntity alarmEntity = alarmList.get(position);

        holder.tv_title.setText(alarmEntity.getMessage());
        holder.tv_time.setText(alarmEntity.getTime());
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title, tv_time;
        private View v_status;
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_time = itemView.findViewById(R.id.tv_time);
            v_status = itemView.findViewById(R.id.v_status);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

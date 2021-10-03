package com.example.demofinal.AlarmManeger;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.demofinal.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class AlarmFrgList extends Fragment {
    private static final String TAG = AlarmFrgList.class.getName();
    private Context mContext;
    private ArrayList<AlarmEntity> alarmList;
    private RecyclerView mRecyclerView;
    private AlarmAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);
        RecyclerView rvAlarm = view.findViewById(R.id.rv_alarm);

        loadData();
        buildRecyclerView(view);
        return view;
    }

    private void buildRecyclerView(View view) {
            mRecyclerView = view.findViewById(R.id.rv_alarm);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(mContext);
            mAdapter = new AlarmAdapter(alarmList);

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    openDialog(Gravity.CENTER, view, position);

                }
            });

    }

    private void openDialog(int gravity, View view, int position) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_details);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if (Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        }else {
            dialog.setCancelable(false);
        }
        TextView tvMessage = window.findViewById(R.id.tv_message);

        String mMessage = alarmList.get(position).getMessage();
        int id = (int) alarmList.get(position).getId();
        tvMessage.setText(mMessage);
        Button btDone = window.findViewById(R.id.bt_done);
        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button btRemove = window.findViewById(R.id.bt_remove);
        btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmList.remove(position);
                mAdapter.notifyItemRemoved(position);

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared preferences", mContext.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(alarmList);
                editor.putString("task list", json);
                editor.apply();

                loadData();
                JobScheduler jobScheduler =
                        null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    jobScheduler.cancel((int) id);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void loadData() {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared preferences", mContext.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("task list", null);
            Type type = new TypeToken<ArrayList<AlarmEntity>>() {}.getType();
            alarmList = gson.fromJson(json, type);

            if (alarmList == null) {
                alarmList = new ArrayList<>();
            }
    }
}
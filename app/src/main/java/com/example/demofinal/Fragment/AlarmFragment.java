package com.example.demofinal.Fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.example.demofinal.AlarmManeger.AlarmAdapter;
import com.example.demofinal.AlarmManeger.AlarmEntity;
import com.example.demofinal.PendingService;
import com.example.demofinal.R;
import com.example.demofinal.databinding.FragmentAlarmBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class AlarmFragment extends Fragment {
    private static final String TAG = AlarmFragment.class.getName();
    private final Calendar CAL = Calendar.getInstance();
    private final Random rd = new Random();
    private FragmentAlarmBinding binding;
    private Context mContext;
    private AlarmAdapter mAdapter;
    private ArrayList<AlarmEntity> alarmList = new ArrayList<>();
    long NewId = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        binding = FragmentAlarmBinding.bind(view);
        loadData();
        initViews();
        return view;
    }

    private void initViews() {
        binding.btSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    NewId = rd.nextInt(Integer.MAX_VALUE);
                    setupAlarm();
                    insertItem(binding.etInputMessage.getText().toString(), binding.tvDateTime.getText().toString());
                }
            }
        });
        binding.tvDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDate();
            }
        });
    }

    private void insertItem(String message, String time) {
        alarmList.add(new AlarmEntity(NewId,message, time, CAL.getTimeInMillis()));
        saveData();
    }

    private void initDate(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                CAL.set(Calendar.YEAR, year);
                CAL.set(Calendar.MONTH, month);
                CAL.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        CAL.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        CAL.set(Calendar.MINUTE, minute);
                        Log.i(TAG, "updateLaabel...");
                        String myFormat = "MM/dd/yy HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                        binding.tvDateTime.setText(sdf.format(CAL.getTime()));
                        binding.tvDateTime.setTag(CAL.getTimeInMillis());
                    }
                };
                new TimePickerDialog(mContext, timeSetListener, CAL.get(Calendar.HOUR_OF_DAY), CAL.get(Calendar.MINUTE), false).show();
            }
        };
        new DatePickerDialog(mContext, dateSetListener, CAL.get(Calendar.YEAR), CAL.get(Calendar.MONTH), CAL.get(Calendar.DAY_OF_MONTH)).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupAlarm() {
        if (binding.etInputMessage.getText().toString().isEmpty()
                || binding.etInputMessage.getText().toString().isEmpty()) {
            Toast.makeText(mContext, "Please input + message first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.tvDateTime.getText().toString().isEmpty()){
            Toast.makeText(mContext, "Please set time first!", Toast.LENGTH_SHORT).show();
            return;
        }
        //Khoi tao doi tuong PendingService lam nhiem vu thuc thi tac vu ngam
        ComponentName serviceComponent = new ComponentName(mContext, PendingService.class);
        //Tao mot lenh thuc thi
        JobInfo.Builder builder = new JobInfo.Builder(rd.nextInt(Integer.MAX_VALUE), serviceComponent);
        //Quy dinh thoi gian tre
        builder.setMinimumLatency(1000);
        //Quy dinh thoi gian tre toi da
        builder.setOverrideDeadline(2 * 1000);
        builder.setRequiresCharging(false);
        //Tao doi tuong gui thong so
        PersistableBundle bundle = new PersistableBundle();
        //Loai tac vu
        bundle.putString("TYPE", PendingService.TYPE_ALARM);
        //Thong tin tin nhan
        bundle.putString("MSG", binding.etInputMessage.getText().toString());

        bundle.putLong("ID", NewId);
        //Thoi gian gui
        bundle.putLong("TIME", (long) binding.tvDateTime.getTag());
        builder.setExtras(bundle);
        //Khoi tao JobScheduler
        JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
        Toast.makeText(mContext, "A message will be sent sometime", Toast.LENGTH_SHORT).show();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared preferences", mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmList);
        editor.putString("task list", json);
        editor.apply();
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
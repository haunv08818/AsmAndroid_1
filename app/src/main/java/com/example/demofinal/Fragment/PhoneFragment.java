package com.example.demofinal.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.example.demofinal.PendingService;
import com.example.demofinal.R;
import com.example.demofinal.databinding.FragmentPhoneBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class PhoneFragment extends Fragment {

    private static final String TAG = PhoneFragment.class.getName();
    private final Calendar CAL = Calendar.getInstance();
    private final Random rd = new Random();
    private FragmentPhoneBinding binding;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone, container, false);
        binding = FragmentPhoneBinding.bind(view);
        initViews();
        return view;
    }

    private void initViews() {
        binding.btSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setupAlarm();
                }
            }
        });
        binding.tvTimeSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDate();
            }
        });
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    Manifest.permission.CALL_PHONE
            }, 101);
        }
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
                        binding.tvTimeSetup.setText(sdf.format(CAL.getTime()));
                        binding.tvTimeSetup.setTag(CAL.getTimeInMillis());
                    }
                };
                new TimePickerDialog(mContext, timeSetListener, CAL.get(Calendar.HOUR_OF_DAY), CAL.get(Calendar.MINUTE), false).show();
            }
        };
        new DatePickerDialog(mContext, dateSetListener, CAL.get(Calendar.YEAR), CAL.get(Calendar.MONTH), CAL.get(Calendar.DAY_OF_MONTH)).show();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupAlarm(){
        if (binding.etInputPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(mContext, "Please input phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.tvTimeSetup.getText().toString().isEmpty()){
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
        //Loai tac vu nhan tin
        bundle.putString("TYPE", PendingService.TYPE_PHONE);
        //Thong tin nguoi gui
        bundle.putString("PHONE", binding.etInputPhoneNumber.getText().toString());
        //Thoi gian gui
        bundle.putLong("TIME", (long) binding.tvTimeSetup.getTag());
        builder.setExtras(bundle);

        //Khoi tao JobScheduler
        JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
        Toast.makeText(mContext, "A message will be sent sometime", Toast.LENGTH_SHORT).show();

    }
}
package com.example.demofinal;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PendingService extends JobService {
    public static final String TYPE_ALARM = "TYPE_ALARM";
    public static final String TYPE_SMS = "TYPE_SMS";
    public static final String TYPE_PHONE = "TYPE_PHONE";
    public static final String KEY_TYPE = "KEY_TYPE";
    public static final String KEY_MSG = "KEY_MSG";
    public static final String KEY_ID = "KEY_ID";
    private static final String TAG = PendingService.class.getName();
    Map<Integer, JobAsyncTask> jobAsyncTasks = new HashMap<Integer, JobAsyncTask>();


    //Tao mot handler de thuc thi tac vu goi dien
    private JobAsyncTask mJobAsyncTask;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        JobAsyncTask j = new JobAsyncTask();
        j.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        jobAsyncTasks.put(params.getJobId(), j);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job stop");
        JobAsyncTask j = jobAsyncTasks.get(params.getJobId());
        j.cancel(true);
        return false;
    }

    private void makeCall(JobParameters param) {
        String phone = param.getExtras().getString("PHONE");
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel: " + phone));
        startActivity(intent);
    }

    private void doPendingTask(JobParameters param) {
        long timeDelay = param.getExtras().getLong("TIME");
        String TYPE = param.getExtras().getString("TYPE");
        long timeNow = Calendar.getInstance().getTimeInMillis();
        try {
            Thread.sleep(timeDelay - timeNow);
        } catch (Exception exception) {
        }
        if (TYPE.equals(TYPE_SMS)) {
            sendSMS(param);
        } else if (TYPE.equals(TYPE_PHONE)) {
            makeCall(param);
        } else {
            makeAnAlarm(param);
        }
        jobFinished(param, false);
        Log.d(TAG, "Job stop " + param.getJobId());
    }

    private void makeAnAlarm(JobParameters param) {
        String msg = param.getExtras().getString("MSG");
        long id = param.getExtras().getLong("ID");

        Intent intent = new Intent(this, AlarmActNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_TYPE, TYPE_ALARM);
        intent.putExtra(KEY_MSG, msg);
        intent.putExtra(KEY_ID, id);
        startActivity(intent);
    }

    private void sendSMS(JobParameters param) {
        String Phone = param.getExtras().getString("SENDER");
        String Message = param.getExtras().getString("MSG");
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(Phone, null, Message, null, null);
    }

    @SuppressLint("StaticFieldLeak")
    private class JobAsyncTask extends AsyncTask<JobParameters, String, JobParameters> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JobParameters doInBackground(JobParameters... jobParameters) {
            Log.i(TAG, "start");
            if(!isCancelled())
            {
                doPendingTask(jobParameters[0]);
            }

            return null;
        }

        @Override
        protected void onPostExecute(JobParameters parameters) {
            super.onPostExecute(parameters);
        }
    }
}

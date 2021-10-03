package com.example.demofinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActNotification extends AppCompatActivity {

    private TextView tv_inputMessage;
    private Button bn_cancel;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_act_notification);

        tv_inputMessage = findViewById(R.id.tv_inputMessage);
        Intent intent = getIntent();
        String message = intent.getStringExtra("KEY_MSG");
        String id = intent.getStringExtra("KEY_ID");
        tv_inputMessage.setText(message);

        bn_cancel = findViewById(R.id.bn_cancel);
        bn_cancel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                JobScheduler jobScheduler =
                        (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.cancel(Integer.parseInt(id));

                Intent intent1 = new Intent(AlarmActNotification.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }
}
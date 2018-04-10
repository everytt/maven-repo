package com.evevry.tt.ttsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.every.tt.GlobalT;
import com.every.tt.model.Call;
import com.every.tt.model.Sms;

public class MainActivity extends AppCompatActivity {
    private static final String UID = "test_uid";
    private static final String PHONE_NUMBER = "01000000000";

    private GlobalTReceiver mGlobalTReceiver = new GlobalTReceiver();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerReceiver();
    }

    private void unregisterReceiver() {
        unregisterReceiver(mGlobalTReceiver);
    }

    public void onInitWithIndonesia(View view) {
        GlobalT.with(this)
                .nation(GlobalT.Nation.INDONESIA)
                .save(this);
    }

    public void onStart(View view) {
        GlobalT.getInstance().start();
    }

    public void onRegister(View view) {
        GlobalT.getInstance().register(UID);
    }

    public void onSendCall(View view) {
        GlobalT.call(this, PHONE_NUMBER, new GlobalT.Failure() {
            @Override
            public void onFail(String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSendSms(View view) {
        GlobalT.sendSms(this, PHONE_NUMBER, new GlobalT.Failure() {
            @Override
            public void onFail(String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onShowChatRoom(View view) {
        GlobalT.showChatRoom(this, PHONE_NUMBER, new GlobalT.Failure() {
            @Override
            public void onFail(String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onStop(View view) {
        if (GlobalT.getInstance().isServiceTurnOn()) {
            GlobalT.getInstance().stop();
        }
    }


    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalT.ACTION_CHECK_SERVICE);
        intentFilter.addAction(GlobalT.ACTION_START_SERVICE);
        intentFilter.addAction(GlobalT.ACTION_REGISTER_RESPONSE);
        intentFilter.addAction(GlobalT.ACTION_INSERT_CALL_LOG);
        intentFilter.addAction(GlobalT.ACTION_INSERT_SMS_LOG);
        intentFilter.addAction(GlobalT.ACTION_UPDATE_SMS_LOG);
        registerReceiver(mGlobalTReceiver, intentFilter);
    }

    private class GlobalTReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (GlobalT.ACTION_CHECK_SERVICE.equals(intent.getAction())) {
                GlobalT.handleCheckingBroadcast(context, intent);
            } else if (GlobalT.ACTION_START_SERVICE.equals(intent.getAction())) {
                GlobalT.handleRestartBroadcast(context, intent);
            } else if (GlobalT.ACTION_REGISTER_RESPONSE.equals(intent.getAction())) {
                boolean isSuccess = intent.getBooleanExtra(GlobalT.EXTRA_KEY_SUCCESS, false);
                Toast.makeText(getApplicationContext(),
                        "ACTION_REGISTER_RESPONSE: " + isSuccess,
                        Toast.LENGTH_SHORT).show();
                if (isSuccess) {
                    String userResponse = intent.getStringExtra(GlobalT.EXTRA_KEY_USER_JSON);
                    Toast.makeText(getApplicationContext(), userResponse, Toast.LENGTH_SHORT).show();
                }
            } else if (GlobalT.ACTION_INSERT_CALL_LOG.equals(intent.getAction())) {
                Call call = GlobalT.parseCallLog(intent);
                //Do something with call..
            } else if (GlobalT.ACTION_INSERT_SMS_LOG.equals(intent.getAction())) {
                Sms sms = GlobalT.parseSmsLog(intent);
                //Do something with sms..
            } else if (GlobalT.ACTION_UPDATE_SMS_LOG.equals(intent.getAction())) {
                Sms updatedSms = GlobalT.parseSmsLog(intent);
                //Do something with sms..
            }
        }
    }
}

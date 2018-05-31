package com.evevry.tt.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.every.tt.GlobalT;
import com.every.tt.model.Call;
import com.every.tt.model.Sms;

public class MainActivity extends AppCompatActivity {
    private static final String UID = "test_uid";
    private static final String PHONE_NUMBER = "01000000000";

    private GlobalTReceiver mGlobalTReceiver = new GlobalTReceiver();

    private TextView mTextUserSerial;

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

        mTextUserSerial = findViewById(R.id.text_user_serial);

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

    public void onShowPromo(View view) {
        GlobalT.showPromo(this, new GlobalT.Failure() {
            @Override
            public void onFail(String s) {
                Toast.makeText(getApplicationContext(), "Error : " + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onShowCharge(View view) {
        GlobalT.showCharge(this, new GlobalT.Failure() {
            @Override
            public void onFail(String s) {
                Toast.makeText(getApplicationContext(), "Error : " + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onShowEtc(View view) {
        GlobalT.showEtc(this, new GlobalT.Failure() {
            @Override
            public void onFail(String s) {
                Toast.makeText(getApplicationContext(), "Error : " + s, Toast.LENGTH_SHORT).show();
            }
        });
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

    public void onUserSerial(View view) {
        String userSerial = GlobalT.getInstance().userSerial();
        mTextUserSerial.setText(TextUtils.isEmpty(userSerial) ? "No serial" : userSerial);
    }


    public void onShowChatRoomList(View view) {
        GlobalT.showChatRoomList(this, new GlobalT.Failure() {
            @Override
            public void onFail(String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onCallList(View view) {
        GlobalT.showRecentCallList(this, new GlobalT.Failure() {
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

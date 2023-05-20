package com.example.currentweatherforecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.currentweatherforecast.util.PermissionUtil;

public class SplashActivity extends AppCompatActivity {
    private static final String LOG_TAG = "SplashActivity";

    private TextView counterTextView;
    /**
     * Number of seconds to count down before showing the app open ad. This simulates the time needed
     * to load the app.
     */
    private static final long COUNTER_TIME = 3;

    private long secondsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        counterTextView=findViewById(R.id.timer);

        checkPermissions();
    }

    //检查应用所需要的权限
    private void checkPermissions(){
        String[] permissions={
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.INTERNET
        };
        if(PermissionUtil.checkMultiPermission(this,permissions,0)){
            // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
            createTimer(COUNTER_TIME);
        }
    }

    // 权限请求回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                // 已获取到所有权限，继续应用的正常逻辑

                // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
                createTimer(COUNTER_TIME);
            } else {
                // 未完全启用权限，向用户解释为何应用需要这些权限，并提供引导用户手动打开权限的选项
                final TextView tv_hint=findViewById(R.id.tv_hint);
                tv_hint.setVisibility(View.VISIBLE);
                tv_hint.setText(String.format(
                        "Please navigate to \"Settings\" -> \"Apps & notifications\" > \"%s\" and access the \"Permissions\" menu.",
                        getResources().getString(R.string.app_name)));
                //finish();
            }
        }
    }

    /**
     * Create the countdown timer, which counts down to zero and show the app open ad.
     *
     * @param seconds the number of seconds that the timer counts down from
     */
    private void createTimer(long seconds) {
        counterTextView = findViewById(R.id.timer);

        CountDownTimer countDownTimer =
                new CountDownTimer(seconds * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsRemaining = ((millisUntilFinished / 1000) + 1);
                        counterTextView.setText("App is done loading in: " + secondsRemaining);
                    }

                    @Override
                    public void onFinish() {
                        secondsRemaining = 0;
                        counterTextView.setText("Done.");

                        Application application = getApplication();

                        // If the application is not an instance of MyApplication, log an error message and
                        // start the MainActivity without showing the app open ad.
                        if (!(application instanceof MainApplication)) {
                            Log.e(LOG_TAG, "Failed to cast application to MyApplication.");
                            startMainActivity();
                            return;
                        }

                        // Show the app open ad.
                        ((MainApplication) application)
                                .showAdIfAvailable(
                                        SplashActivity.this,
                                        new MainApplication.OnShowAdCompleteListener() {
                                            @Override
                                            public void onShowAdComplete() {
                                                startMainActivity();
                                            }
                                        });
                    }
                };
        countDownTimer.start();
    }

    /**
     * Start the MainActivity.
     */
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}
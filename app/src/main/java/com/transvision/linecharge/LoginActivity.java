package com.transvision.linecharge;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.transvision.linecharge.fragments.LoginFragment;
import com.transvision.linecharge.values.GetSetValues;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "LineChargeFile";
    public static final int RequestPermissionCode = 1;

    Toolbar toolbar;
    FragmentTransaction fragmentTransaction;
    GetSetValues getSetValues;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        getSetValues = new GetSetValues();

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();
        editor.apply();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermissionsMandAbove();
            }
        }, 1000);

        if (settings.getBoolean("login", false)) {
            getSetValues.setLinemen_id(settings.getString("linemen_id", ""));
            getSetValues.setLinemen_name(settings.getString("linemen_name", ""));
            getSetValues.setSubdiv_code(settings.getString("subdivision_code", ""));
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("values", getSetValues);
            startActivity(intent);
            finish();
        } else startup();
    }

    private void startup() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.login_container, new LoginFragment()).commit();
    }

    @TargetApi(23)
    private void checkPermissionsMandAbove() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]
                {
                        WRITE_EXTERNAL_STORAGE,
                        CAMERA
                }, RequestPermissionCode);
    }

    private boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean ReadStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadCameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (!ReadStoragePermission && ReadCameraPermission)
                        finish();
                }
                break;
        }
    }

    public GetSetValues getGetSetValues() {
        return this.getSetValues;
    }

    public SharedPreferences.Editor getEditor() {
        return this.editor;
    }
}

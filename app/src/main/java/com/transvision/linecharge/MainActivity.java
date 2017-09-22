package com.transvision.linecharge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.transvision.linecharge.fragments.MainFragment;
import com.transvision.linecharge.values.GetSetValues;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "LineChargeFile";

    Toolbar toolbar;
    FragmentTransaction fragmentTransaction;
    GetSetValues getSetValues;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();
        editor.apply();

        Intent intent = getIntent();
        getSetValues = (GetSetValues) intent.getSerializableExtra("values");

        startup();
    }

    private void startup() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, new MainFragment()).commit();
    }

    public GetSetValues getGetSetValues() {
        return this.getSetValues;
    }

    public SharedPreferences.Editor getEditor() {
        return this.editor;
    }
}

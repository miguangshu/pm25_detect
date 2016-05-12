package com.bupt.pm25;

import android.os.Bundle;
import android.widget.TextView;

import com.bupt.pm25.base.BaseActivity;


public class BulbActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulb);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        TextView bulbText = (TextView) findViewById(R.id.bulb_text);
        bulbText.setText("手机型号: " + android.os.Build.MODEL + ",\nSDK版本:"
                + android.os.Build.VERSION.SDK + ",\n系统版本:"
                + android.os.Build.VERSION.RELEASE);
    }

    @Override
    protected void initEvents() {

    }
}

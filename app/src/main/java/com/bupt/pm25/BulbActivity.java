package com.bupt.pm25;

import android.os.Bundle;
import android.text.Html;
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
        bulbText.setText(Html.fromHtml("开天眼是北京邮电大学物联网实验室研发的一款根据天空图片分析空气中pm2.5含量的app。<br>" +
                         "用户手机摄像头对准天空拍摄一张照片并上传，可以分析出其中的pm2.5含量。<br>" +
                         "拍摄样例:"));
    }

    @Override
    protected void initEvents() {

    }
}

package com.bupt.pm25;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bupt.pm25.base.BaseActivity;

public class MainActivity extends BaseActivity implements OnClickListener
{

	private static final String TAG = "MainActivity";
	private Button photoButton;

	private LocationClient mLocationClient;
	private double mLatitude;
	private double mLongitude;
	private ImageView bulbButton;
	Handler tHandler = new Handler();
	Runnable HandlerThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			showWaveAnimation();
			tHandler.postDelayed(HandlerThread, 3000);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		initEvents();
	}
	@Override
	public void initViews()
	{
		bulbButton = (ImageView)findViewById(R.id.bulb);
		photoButton = (Button)findViewById(R.id.btn_photo);
		mLocationClient = new LocationClient(this);

		mWave1 = (ImageView) findViewById(R.id.wave1);
		mWave2 = (ImageView) findViewById(R.id.wave2);
		mWave3 = (ImageView) findViewById(R.id.wave3);

		mAnimationSet1 = initAnimationSet();
		mAnimationSet2 = initAnimationSet();
		mAnimationSet3 = initAnimationSet();
	}
	@Override
	protected void initEvents()
	{
		photoButton.setOnClickListener(this);
		bulbButton.setOnClickListener(this);
		tHandler.post(HandlerThread);

		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation bdLocation) {
				// map view 销毁后不在处理新接收的位置
				if (bdLocation == null)
					return;

				mLatitude = bdLocation.getLatitude();
				mLongitude = bdLocation.getLongitude();
				AppConfig.NOW_LONGITUDE = String.valueOf(mLongitude);
				AppConfig.NOW_LATITUDE = String.valueOf(mLatitude);
			}
		});
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//混合模式
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1200);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}



	@Override
	public void onClick(View v)
	{
		Intent intent = null;
		switch (v.getId())
		{
			case R.id.btn_photo:
				intent = new Intent(MainActivity.this,CameraActivity.class);
				startActivity(intent);
				break;
			case R.id.bulb:
				intent = new Intent(MainActivity.this,BulbActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//关闭所有Activity
		ActivityCollector.finishAll();
	}

	private ImageView mWave1, mWave2, mWave3;

	private AnimationSet mAnimationSet1, mAnimationSet2, mAnimationSet3;

	private static final int OFFSET = 400;  //每个动画的播放时间间隔
	private static final int MSG_WAVE1_ANIMATION = 1;
	private static final int MSG_WAVE2_ANIMATION = 2;
	private static final int MSG_WAVE3_ANIMATION = 3;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_WAVE1_ANIMATION:
					mWave1.startAnimation(mAnimationSet1);
					break;
				case MSG_WAVE2_ANIMATION:
					mWave2.startAnimation(mAnimationSet2);
					break;
				case MSG_WAVE3_ANIMATION:
					mWave3.startAnimation(mAnimationSet3);
					break;
			}
		}
	};
	private AnimationSet initAnimationSet() {
		AnimationSet as = new AnimationSet(true);
		ScaleAnimation sa = new ScaleAnimation(1f, 1.7f, 1f, 1.7f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(OFFSET * 3);
		AlphaAnimation aa = new AlphaAnimation(0.5f, 0.1f);
		aa.setDuration(OFFSET * 3);
		as.addAnimation(sa);
		as.addAnimation(aa);
		return as;
	}

	private void showWaveAnimation() {
		mHandler.sendEmptyMessage(MSG_WAVE1_ANIMATION);
		mHandler.sendEmptyMessageDelayed(MSG_WAVE2_ANIMATION, OFFSET);
		mHandler.sendEmptyMessageDelayed(MSG_WAVE3_ANIMATION, OFFSET * 2);
	}

	private void clearWaveAnimation() {
		mWave1.clearAnimation();
		mWave2.clearAnimation();
		mWave3.clearAnimation();
	}

}

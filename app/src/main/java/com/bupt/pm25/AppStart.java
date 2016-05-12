package com.bupt.pm25;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.TextView;

/**
 * 应用启动界面
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年12月22日 上午11:51:56
 *
 */
public class AppStart extends Activity {
    private AnimationSet mAnimationSet;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAnimationSet = initAnimationSet();

        final View view = View.inflate(this, R.layout.app_start, null);
        setContentView(view);
        TextView welcomeText = (TextView) findViewById(R.id.welcome_text);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/HandmadeTypewriter.ttf");
        welcomeText.setTypeface(typeFace);
        welcomeText.startAnimation(mAnimationSet);
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
        aa.setDuration(1500);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationStart(Animation animation) {}
        });
    }


    /**
     * 跳转到...
     */
    private void redirectTo() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private AnimationSet initAnimationSet() {
        AnimationSet as = new AnimationSet(true);
//        ScaleAnimation sa = new ScaleAnimation(1f, 1.7f, 1f, 1.7f,
//                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
//                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
//        sa.setDuration(OFFSET * 3);
//		sa.setRepeatCount(Animation.INFINITE);// 设置循环
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        aa.setDuration(1000);
//		aa.setRepeatCount(Animation.INFINITE);//设置循环
//        as.addAnimation(sa);
        as.addAnimation(aa);
        return as;
    }
}

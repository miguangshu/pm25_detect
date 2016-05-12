/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.bupt.pm25.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.pm25.R;


public class ShowCustomToastUtils {
	/** 显示自定义Toast提示(来自res) **/
	public static void showCustomToast(Context context,int resId) {
		View toastRoot = LayoutInflater.from(context).inflate(
				R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text))
				.setText(context.getResources().getString(resId));
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/** 显示自定义Toast提示(来自String) **/
	public static void showCustomToast(Context context,String text) {
		View toastRoot = LayoutInflater.from(context).inflate(
				R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}
}

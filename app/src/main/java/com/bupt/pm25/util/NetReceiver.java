/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.bupt.pm25.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.bupt.pm25.R;


public class NetReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			boolean isConnected = NetUtils.isNetworkConnected(context);
			Log.i("network", "网络状态：" + isConnected);
			Log.i("network", "wifi状态：" + NetUtils.isWifiConnected(context));
			Log.i("network", "移动网络状态：" + NetUtils.isMobileConnected(context));
			Log.i("network", "网络连接类型：" + NetUtils.getConnectedType(context));
			if (!isConnected) {
				ShowCustomToastUtils.showCustomToast(context, R.string.disconnectNetwork);
			}
		}
	}

}

/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.bupt.pm25.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

/**
 * @fileName NetWorkUtils.java
 * @package com.donghuantime.android.util
 * @description 网络工具类
 * @version 1.0
 */
public class NetWorkUtils {
	private Context mContext;
	public State wifiState = null;
	public State mobileState = null;

	public NetWorkUtils(Context context) {
		mContext = context;
	}

	public enum NetWorkState {
		WIFI, MOBILE, NONE;

	}

	/**
	 * 获取当前的网络状态
	 * 
	 * @return
	 */
	public NetWorkState getConnectState() {
		ConnectivityManager manager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		manager.getActiveNetworkInfo();
		wifiState = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		mobileState = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			return NetWorkState.MOBILE;
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			return NetWorkState.NONE;
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			return NetWorkState.WIFI;
		}
		return NetWorkState.NONE;
	}

}

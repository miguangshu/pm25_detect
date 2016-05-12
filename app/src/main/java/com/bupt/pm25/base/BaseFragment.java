package com.bupt.pm25.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bupt.pm25.dialog.FlippingLoadingDialog;
import com.bupt.pm25.util.NetWorkUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseFragment extends Fragment {
	protected Activity mActivity;
	protected Context mContext;
	protected View mView;
	protected NetWorkUtils mNetWorkUtils;
	protected FlippingLoadingDialog mLoadingDialog;

	/**
	 * 屏幕的宽度、高度、密度
	 */
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected float mDensity;

	protected List<AsyncTask<Void, Void, Object>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Object>>();

	public BaseFragment() {
		super();
	}
	public BaseFragment(Activity activity,Context context) {
		mActivity = activity;
		mContext = context;
		mNetWorkUtils = new NetWorkUtils(context);
		mLoadingDialog = new FlippingLoadingDialog(context, "请求提交中");
		/**
		 * 获取屏幕宽度、高度、密度
		 */
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initViews();
		initEvents();
		initData();
		return mView;
	}

	@Override
	public void onDestroy() {
		clearAsyncTask();
		super.onDestroy();
	}

	protected abstract void initViews();

	protected abstract void initEvents();

	protected abstract void initData();

	public View findViewById(int id) {
		return mView.findViewById(id);
	}

	protected void putAsyncTask(AsyncTask<Void, Void, Object> asyncTask) {
		mAsyncTasks.add(asyncTask.execute());
	}

	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Object>> iterator = mAsyncTasks
				.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Object> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
	}

	protected void showLoadingDialog(String text) {
		if (text != null) {
			mLoadingDialog.setText(text);
		}
		mLoadingDialog.show();
	}

	protected void dismissLoadingDialog() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}
	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(mContext, cls);
		startActivity(intent);
	}
	@Override
	public void onStop() {
		Log.e("TAG", "清除图片缓存");
		super.onStop();
	}
	
}

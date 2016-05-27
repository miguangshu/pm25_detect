package com.bupt.pm25.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.bupt.pm25.R;

/**
 * 加载中Dialog
 * 
 * @author lexyhp
 */
public class ResultDialog extends AlertDialog {

	private TextView tips_loading_msg;
	private int layoutResId;
	private String message = null;

	/**
	 * 构造方法
	 *
	 * @param context
	 *            上下文
	 * @param layoutResId
	 *            要传入的dialog布局文件的id
	 */
	public ResultDialog(Context context, int layoutResId,String _message) {
		super(context);
		this.layoutResId = layoutResId;
		this.message = _message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(layoutResId);
		tips_loading_msg = (TextView)findViewById(R.id.text_result);
		tips_loading_msg.setText(message);
	}
	public void updateMessage(String message){
		this.message = message;
	}
}

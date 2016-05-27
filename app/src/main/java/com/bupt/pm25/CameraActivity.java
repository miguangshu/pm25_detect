package com.bupt.pm25;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.bupt.pm25.network.FdfTransfer;
import com.bupt.pm25.network.FileDataFrame;
import com.bupt.pm25.util.BasicDataTypeTransfer;
import com.bupt.pm25.util.NetUtils;
import com.bupt.pm25.util.SocketUploadUtil;
import com.bupt.pm25.util.UploadUtil;
import com.bupt.pm25.view.LoadingDialog;
import com.bupt.pm25.view.ResultDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends SingleFragmentActivity implements MyCameraFragment.UploadPictureInterface,SocketUploadUtil.OnUploadProcessListener {
    private static String TAG = "CameraActivity";
    private ProgressDialog mDialog;

    private FdfTransfer transfer = FdfTransfer.getInstance();
    private Socket socket;
    private OutputStream os;
    private InputStream is = null;
    private LoadingDialog loadingDialog;
    private ResultDialog resultDialog;
    private MyCameraFragment myCameraFragment;
    /**
     * 去上传文件
     */
    protected static final int TO_UPLOAD_FILE = 1;
    /**
     * 上传文件响应
     */
    protected static final int UPLOAD_FILE_DONE = 2;  //
    /**
     * 选择文件
     */
    public static final int TO_SELECT_PHOTO = 3;
    /**
     * 上传初始化
     */
    private static final int UPLOAD_INIT_PROCESS = 4;
    /**
     * 上传中
     */
    private static final int UPLOAD_IN_PROCESS = 5;
    /***
     * 这里的这个URL是我服务器的javaEE环境URL
     */
    private static String ADDRESS = "222.128.13.159";
    private static int PORT = 9400;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        loadingDialog = new LoadingDialog(this, R.layout.view_tips_loading);
        resultDialog = new ResultDialog(this,R.layout.view_tips_result,"0");

    }

    @Override
    protected void initEvents() {
        resultDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                myCameraFragment.reTakenPic(false);
            }
        });
    }

    @Override
    protected Fragment createFragment() {
        myCameraFragment = new MyCameraFragment();
        return myCameraFragment;
    }
    private boolean sendContent(Socket socket, FileDataFrame fdf) {
        try {
            os = socket.getOutputStream();
            byte[] array = transfer.toByteArray(fdf);
            Log.d(TAG,BasicDataTypeTransfer.getInstance().byteToLong(Arrays.copyOf(array, 8))+"");
            os.write(array);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
        return false;
    }

    public String getResult(Socket socket){
        byte[] array = new byte[1024];
        int len = 0;
        try{
            is = socket.getInputStream();
            len = is.read(array);
            Log.d(TAG, "len:" + len);
            Log.d(TAG, "shi" + new String(Arrays.copyOf(array, len)));
        }catch(Exception e){
            Log.e(TAG,e.getMessage());
        }
        return new String(Arrays.copyOf(array, len));
    }
    @Override
    public void uploadPicture(String filePath){
        AppConfig.NEW_FILE_PATH = filePath;
        Log.i(TAG,filePath);
        if(!NetUtils.isNetworkConnected(this)){
            showCustomToast(R.string.noNetwork);
            return;
        }
       if(filePath != null && !"".equals(filePath)){
           upLoadHandler.sendEmptyMessage(TO_UPLOAD_FILE);
       }
    }

    @Override
    public void onUploadDone(int responseCode, String message) {
        if(loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        Message msg = Message.obtain();
        msg.what = UPLOAD_FILE_DONE;
        msg.arg1 = responseCode;
        msg.obj = message;
        upLoadHandler.sendMessage(msg);
    }

    @Override
    public void onUploadProcess(int uploadSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_IN_PROCESS;
        msg.arg1 = uploadSize;
        upLoadHandler.sendMessage(msg );
    }

    @Override
    public void initUpload(int fileSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_INIT_PROCESS;
        msg.arg1 = fileSize;
        upLoadHandler.sendMessage(msg);
    }
    private void toUploadFile(){
        loadingDialog.show();
        SocketUploadUtil uploadUtil = SocketUploadUtil.getInstance();;
        uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态

        uploadUtil.uploadFile( AppConfig.NEW_FILE_PATH,ADDRESS,PORT);
    }

    private Handler upLoadHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_UPLOAD_FILE:
                    toUploadFile();
                    break;
                case UPLOAD_INIT_PROCESS:
//                    progressBar.setMax(msg.arg1);
                    break;
                case UPLOAD_IN_PROCESS:
//                    progressBar.setProgress(msg.arg1);
                    break;
                case UPLOAD_FILE_DONE:
                    String result = "响应码："+msg.arg1+"\n响应信息："+msg.obj+"\n耗时："+SocketUploadUtil.getRequestTime()+"秒";
                    Log.i(TAG, result);
                    //上传成功
                    if(msg.arg1 == SocketUploadUtil.UPLOAD_SUCCESS_CODE){
                        resultDialog.updateMessage(msg.obj + "");
                        resultDialog.show();
                    }else{
                        showAlertDialog("发生异常",msg.obj+"");
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };
}

package com.bupt.pm25;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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


import com.bupt.pm25.network.FdfTransfer;
import com.bupt.pm25.network.FileDataFrame;
import com.bupt.pm25.util.BasicDataTypeTransfer;
import com.bupt.pm25.util.NetUtils;

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
import java.util.List;

public class CameraActivity extends SingleFragmentActivity implements MyCameraFragment.UploadPictureInterface{
    private static String TAG = "CameraActivity";
    private ProgressDialog mDialog;

    private FdfTransfer transfer = FdfTransfer.getInstance();
    private Socket socket;
    private OutputStream os;
    private InputStream is = null;

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
    }

    @Override
    protected void initEvents() {}

    @Override
    protected Fragment createFragment() {
        return new MyCameraFragment();
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
        if(!NetUtils.isNetworkConnected(this)){
            showCustomToast(R.string.noNetwork);
            return;
        }
        putAsyncTask(new UploadImageTask());
        Log.i(TAG, "上传图片");
    }
    /**
     * 加载视频信息，按照createAt倒序排列
     * @author miguangshu
     *
     */
    private class UploadImageTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected void onPreExecute() {
            mDialog = ProgressDialog.show(CameraActivity.this, null, "正在上传图片，请稍后...", true, false);
        }

        @Override
        protected Object doInBackground(Void... params) {
            String result = "";
            try {
                /* 连接服务器 */
                Log.d("新线程", "刚进入新线程");
                socket = new Socket();
                SocketAddress address = new InetSocketAddress(AppConfig.SERVER_HOST_IP, AppConfig.SERVER_HOST_PORT);
                socket.connect(address, 10000);
                /* 获取输出流 */
                List<File> filesToBeUpload = new ArrayList<File>();
                filesToBeUpload.add(new File(AppConfig.NEW_FILE_PATH));
                Log.d(TAG, AppConfig.NEW_FILE_PATH);
                FileDataFrame fdf = new FileDataFrame(0.8, filesToBeUpload);
                sendContent(socket, fdf);
                result = getResult(socket);
            } catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, e.getMessage() + e.getMessage());
                result = "服务器连接异常";
            }finally {
                try {
                    if(os!=null){
                        os.close();
                    }
                    if(is!=null){
                        is.close();
                    }
                    if(socket!=null){
                        socket.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Dialog dialog = new Dialog(CameraActivity.this);
            dialog.setTitle("当前pm2.5值为:"+o);
            dialog.show();
            super.onPostExecute(o);
        }
    }

}

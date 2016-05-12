package com.bupt.pm25;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;


import com.bupt.pm25.network.FdfTransfer;
import com.bupt.pm25.network.FileDataFrame;
import com.bupt.pm25.util.BasicDataTypeTransfer;
import com.bupt.pm25.util.NetUtils;

import java.io.File;
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

public class CameraActivity extends SingleFragmentActivity implements CameraFragment.UploadPictureInterface{
    private static String TAG = "CameraActivity";

    private FdfTransfer transfer = FdfTransfer.getInstance();
    private Socket socket;
    private OutputStream os;
    private InputStream is = null;

    private Handler handler = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
//                        Toast.makeText(CameraActivity.this, msg.getData().getString("result"), Toast.LENGTH_SHORT).show();
                        Dialog dialog = new Dialog(CameraActivity.this);
                        dialog.setTitle(msg.getData().getString("result"));
                        dialog.show();
                        break;
                    case -1:
                        Dialog dialog2 = new Dialog(CameraActivity.this);
                        dialog2.setTitle("出错了");
                        dialog2.show();
                        break;
                }
            }
        };
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected Fragment createFragment() {
        return new CameraFragment();
    }

    private void initClientSocket() {
        Log.d(TAG, "初始化");
        showLoadingDialog("正在上传图片");
        new Thread() {
            @Override
            public synchronized void run() {
                Message msg = handler.obtainMessage();
                try {
			         /* 连接服务器 */
                    Log.d("新线程", "刚进入新线程");
                    socket = new Socket(AppConfig.SERVER_HOST_IP, AppConfig.SERVER_HOST_PORT);
                    /* 获取输出流 */
                    List<File> filesToBeUpload = new ArrayList<File>();
                    filesToBeUpload.add(new File(AppConfig.NEW_FILE_PATH));
                    FileDataFrame fdf = new FileDataFrame(0.8, filesToBeUpload);
                    sendContent(socket, fdf);
                    String target = getResult(socket);
                    Log.d("", "result:" + target);
                    Bundle b = new Bundle();
                    b.putString("result", "当前雾霾值为:"+target);
                    msg.setData(b);
                    msg.what = 0;
//					tvResult.setText(target);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    Log.d("sockettest", "unknown host exception");
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    Log.e(TAG, e.getMessage());
                }finally {
                    dismissLoadingDialog();
                    handler.sendMessage(msg);
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
            }
        }.start();
    }




    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private boolean sendContent
            (Socket socket, FileDataFrame fdf) {
        try {
            os = socket.getOutputStream();
            byte[] array = transfer.toByteArray(fdf);
            Log.d("sockettest", "qh" + BasicDataTypeTransfer.getInstance().byteToLong(Arrays.copyOf(array, 8)));
            os.write(array);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public String getResult(Socket socket){
        byte[] array = new byte[1024];
        int len = 0;
        try{
            is = socket.getInputStream();
            len = is.read(array);
            Log.d(TAG, "len:" + len);
            Log.d(TAG, "shi" + new String(Arrays.copyOf(array, len)));
        }catch(Exception e){
            e.printStackTrace();
        }
        return new String(Arrays.copyOf(array, len));
    }
    @Override
    public void uploadPicture(String filePath){
        AppConfig.NEW_FILE_PATH = filePath;
//        initClientSocket();
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
            showLoadingDialog("正在上传图片");
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
            dismissLoadingDialog();
            Dialog dialog = new Dialog(CameraActivity.this);
            dialog.setTitle("当前pm2.5值为:"+o);
            dialog.show();
            super.onPostExecute(o);
        }
    }
}

package com.bupt.pm25.util;

/**
 * Created by miguangshu on 2016/5/19.
 */

import android.util.Log;

import com.bupt.pm25.AppConfig;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 *
 * 上传工具类
 * @author spring sky<br>
 * Email :vipa1888@163.com<br>
 * QQ: 840950105<br>
 * 支持上传文件和参数
 */
public class SocketUploadUtil {
    private static SocketUploadUtil uploadUtil;
    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
    private SocketUploadUtil() {

    }

    /**
     * 单例模式获取上传工具类
     * @return
     */
    public static SocketUploadUtil getInstance() {
        if (null == uploadUtil) {
            uploadUtil = new SocketUploadUtil();
        }
        return uploadUtil;
    }

    private static final String TAG = "UploadUtil";
    private int readTimeOut = 10 * 1000; // 读取超时
    private int connectTimeout = 10 * 1000; // 超时时间
    /***
     * 请求使用多长时间
     */
    private static int requestTime = 0;

    private static final String CHARSET = "utf-8"; // 设置编码

    /***
     * 上传成功
     */
    public static final int UPLOAD_SUCCESS_CODE = 1;
    /**
     * 文件不存在
     */
    public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 2;
    /**
     * 服务器出错
     */
    public static final int UPLOAD_SERVER_ERROR_CODE = 3;
    protected static final int WHAT_TO_UPLOAD = 1;
    protected static final int WHAT_UPLOAD_DONE = 2;

    /**
     *
     * @param filePath
     * @param address
     * @param port
     */
    public void uploadFile(String filePath, String address,int port) {
        if (filePath == null) {
            sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,"文件不存在");
            return;
        }
        try {
            File file = new File(filePath);
            uploadFile(file, address,port);
        } catch (Exception e) {
            sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,"文件不存在");
            e.printStackTrace();
            return;
        }
    }

    /**
     *
     * @param file
     * 需要上传的文件
     * @param address
     * 上传服务器地址
     * @param port
     * 上传服务器端口
     */
    public void uploadFile(final File file, final String address, final int port) {
        if (file == null || (!file.exists())) {
            sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,"文件不存在");
            return;
        }

        Log.i(TAG, "请求的Address=" + address);
        Log.i(TAG, "请求的端口=" + port);
        new Thread(new Runnable() { //开启线程上传文件
            @Override
            public void run() {
                toUploadFile(file, address,port);
            }
        }).start();

    }

    private void toUploadFile(File file, String address, int port) {
        String result = null;
        requestTime= 0;
        Socket socket = null;
        long requestTime = System.currentTimeMillis();
        long responseTime = 0;
        OutputStream outputStream = null;
        try {
            socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(address, port);
            socket.connect(socketAddress,connectTimeout);
            outputStream = socket.getOutputStream();
            String picName = file.getName();//获取图片名称
            int picNameLen = picName.length();
            byte[] picNameLenArray = BasicDataTypeTransfer.getInstance().IntToByteArray(picNameLen);
            byte[] picNameArray = BasicDataTypeTransfer.getInstance().StringToByteArray(picName);
            long picContentLen = file.length();
            byte[] picContentLenArray = BasicDataTypeTransfer.getInstance().longToByteArray(picContentLen);
            byte[] headArray = ArrayUtil.getInstance().concat(ArrayUtil.getInstance().concat(picNameLenArray, picNameArray), picContentLenArray);
            long totalLen = 8 + headArray.length;
            byte[] totalLenArray = BasicDataTypeTransfer.getInstance().longToByteArray(totalLen);
            byte[] preHeadArray = ArrayUtil.getInstance().concat(totalLenArray, headArray);
            outputStream.write(preHeadArray);
            byte[] tmpArray = new byte[1024];
            int length = 0;
            int curlen = preHeadArray.length;
            InputStream is = new FileInputStream(file);
            onUploadProcessListener.initUpload((int) file.length());
            onUploadProcessListener.onUploadProcess(curlen);

            while ((length = is.read(tmpArray)) != -1) {
                curlen = length + curlen;
                outputStream.write(tmpArray, 0, length);
                onUploadProcessListener.onUploadProcess(curlen);
            }
            is.close();
            outputStream.flush();
            responseTime = System.currentTimeMillis();
            this.requestTime = (int)(responseTime - requestTime)/1000;

            InputStream socketInputStream = socket.getInputStream();
            byte[] responseByte = new byte[1024];
            int responseLen = socketInputStream.read(responseByte);
            socketInputStream.close();
            String responseMessage = new String(Arrays.copyOf(responseByte, responseLen));
            sendMessage(UPLOAD_SUCCESS_CODE, responseMessage);
        }catch (IOException e){
            Log.e(TAG,e.getMessage());
            sendMessage(UPLOAD_SERVER_ERROR_CODE,"服务端异常");
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
            sendMessage(UPLOAD_SERVER_ERROR_CODE,"服务端异常");
        }finally {

            try {
                if(outputStream != null) {
                    outputStream.close();
                }
                if(socket != null){
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 发送上传结果
     * @param responseCode
     * @param responseMessage
     */
    private void sendMessage(int responseCode,String responseMessage)
    {
        onUploadProcessListener.onUploadDone(responseCode, responseMessage);
    }

    /**
     * 下面是一个自定义的回调函数，用到回调上传文件是否完成
     *
     * @author shimingzheng
     *
     */
    public static interface OnUploadProcessListener {
        /**
         * 上传响应
         * @param responseCode
         * @param message
         */
        void onUploadDone(int responseCode, String message);
        /**
         * 上传中
         * @param uploadSize
         */
        void onUploadProcess(int uploadSize);
        /**
         * 准备上传
         * @param fileSize
         */
        void initUpload(int fileSize);
    }
    private OnUploadProcessListener onUploadProcessListener;

    public void setOnUploadProcessListener(
            OnUploadProcessListener onUploadProcessListener) {
        this.onUploadProcessListener = onUploadProcessListener;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    /**
     * 获取上传使用的时间
     * @return
     */
    public static int getRequestTime() {
        return requestTime;
    }

    public static interface uploadProcessListener{

    }

}
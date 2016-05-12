package com.bupt.pm25;

import android.os.Environment;

import java.io.File;

/**
 * Created by hougr on 15/11/26.
 */
public class AppConfig {
    public static String sSDRootPath = Environment.getExternalStorageDirectory().getPath()+ File.separator;

    public static final String APP_FOLDER = sSDRootPath + "LaoHanSocket"+ File.separator;

    //配置，不变
    public static String NEW_FILE_PATH = "";

    public static String NOW_LONGITUDE = "";
    public static String NOW_LATITUDE = "";


    /* 服务器地址 */
	public final static String SERVER_HOST_IP = "10.103.242.79";
//    public final static String SERVER_HOST_IP = "192.168.2.1";
//    public final static String SERVER_HOST_IP = "222.128.13.159";

    /* 服务器端口 */
    public final static int SERVER_HOST_PORT = 9400;

}

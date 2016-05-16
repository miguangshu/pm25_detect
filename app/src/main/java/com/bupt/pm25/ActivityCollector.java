package com.bupt.pm25;

import android.app.Activity;


import com.bupt.pm25.util.DebugHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hougr on 15-6-11.
 */
public class ActivityCollector {
    public static List<Activity> sActivityList = new ArrayList<Activity>();
    public static ArrayList<Activity> sSignUpActivityList = new ArrayList<Activity>();



    public static void addActivity(Activity activity){
        sActivityList.add(activity);
    }

    public static void removeActivity(Activity activity){
        sActivityList.remove(activity);
    }

//    public static void removeActivity(Activity activity){
//        sActivityList.remove(activity);
//    }



    public static void finishAll(){
        for (Activity activity:sActivityList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }


    public static void finishAllSignup(){

        for (Activity activity:sSignUpActivityList){
            DebugHelper.logD(activity.toString() + "死了");
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        sSignUpActivityList = new ArrayList<Activity>();
    }
}

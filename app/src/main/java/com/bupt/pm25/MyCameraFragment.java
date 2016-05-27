/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.bupt.pm25;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bupt.pm25.util.BitmapUtils;
import com.bupt.pm25.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyCameraFragment extends Fragment implements View.OnClickListener,SurfaceHolder.Callback{
    private static final String TAG = "MyCameraFragment";
    public static final String SD_IMAGES_PATH = Environment.getExternalStorageDirectory().getPath() + "/cn.edu.bupt/";
    public static final String DATA_IMAGES_PATH = "/data/data/cn.edu.bupt/images/";

    private SurfaceView mSurfaceView;
    private SurfaceHolder holder;
    private View mBgFrame;
    //    private ImageView mCountImageView;
    private TextView mCountText;
    private Button mPhotoButton;//拍照按鈕
    private Button mCancleButton;//退出按鈕
    private Button mOKButton;//上傳圖片按鈕
    private Button mChongpaiButton;//重拍按鈕
    private boolean mPhotoTaked;//是否拍照
    private String mPhotoFilePath = new String();
    private Camera mCamera;
    private ProgressBar mProgressBar;//进度条
    @SuppressWarnings("deprecation")
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Date photoDate = new Date();
            String shortName = new SimpleDateFormat("yyyyMMddHHmmss").format(photoDate) +"_"+AppConfig.NOW_LONGITUDE +"_"+AppConfig.NOW_LATITUDE + "_" + Build.MODEL + ".jpeg";
            mPhotoFilePath = AppConfig.APP_FOLDER + shortName;
            FileOutputStream fos = null;
            File pictureFile = FileUtils.createFileSuccessful(getActivity(), mPhotoFilePath);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            bitmap = BitmapUtils.resizeImage(bitmap, 256, 256);
            data = BitmapUtils.Bitmap2Bytes(bitmap);
            try {
                fos = new FileOutputStream(pictureFile);
                fos.write(data);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            mPhotoTaked = true;
            resetView();
        }

    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPhotoTaked = false;
    }
    private void initView(View v){
        mBgFrame = v.findViewById(R.id.bg_frame);
        mSurfaceView = (SurfaceView) v.findViewById(R.id.surfaceview_camera);
        holder = mSurfaceView.getHolder();
        mPhotoButton = (Button) v.findViewById(R.id.button_takephoto);
        mChongpaiButton = (Button) v.findViewById(R.id.button_chongpai);
        mOKButton = (Button) v.findViewById(R.id.button_ok);
        mCancleButton = (Button) v.findViewById(R.id.button_cancle);
        mCountText = (TextView) v.findViewById(R.id.count_textView);
/*
        mProgressBar = (ProgressBar) v.findViewById(R.id.id_upload_progress_bar);
*/
        mPhotoButton.setVisibility(View.VISIBLE);
        mCancleButton.setVisibility(View.VISIBLE);
        mOKButton.setVisibility(View.INVISIBLE);
        mChongpaiButton.setVisibility(View.INVISIBLE);
    }
    @SuppressWarnings("deprecation")
    private void initEvent(){
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
        mPhotoButton.setEnabled(true);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.setPreviewSize(400, 240);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        //success代表对焦是否准确
                        if (success) {
                            mCamera.takePicture(null, null, mPictureCallback);
                        }
                    }
                });*/
                if (mCamera != null) {
                    mCamera.takePicture(null, null, mPictureCallback);
                }
            }
        });
        mPhotoButton.setEnabled(true);
        mCancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mChongpaiButton.setEnabled(false);
        mChongpaiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoTaked = false;
                resetView();
                new File(mPhotoFilePath).delete();
            }
        });
        mOKButton.setEnabled(false);
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture.uploadPicture(mPhotoFilePath);
            }
        });
    }

    /**
     * 开始预览相机内容
     */
    @SuppressWarnings("deprecation")
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            //将holder对象传递到Camera对象中,完成绑定操作
            camera.setPreviewDisplay(holder);
            //获取相机参数
            Camera.Parameters parameters = mCamera.getParameters();
            //保存照片旋转角度
            parameters.setRotation(90);
            Size previewSize = getBestSupportedSize(parameters.getSupportedPreviewSizes(), 1, 1);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            Size pictureSize = getMatchedSupportedSize(parameters.getSupportedPictureSizes(), previewSize);
            parameters.setPictureSize(pictureSize.width, pictureSize.height);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
            mCamera.setParameters(parameters);
            //将Camera预览角度进行调整90°
            //开始在surface预览操作,但是是横屏的，在预览之前增加一个setDisplayOrientation方法
            camera.setDisplayOrientation(90);
            camera.startPreview();
            camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    /**
     * 获取Camerad对象
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    private Camera getCamera() {
        Camera camera;
        try {
            int n =Camera.getNumberOfCameras();
            Log.i(TAG,n+"");
            camera = Camera.open(0);
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }
    /**
     * 释放相机占用的资源，请求了Camera但是没有释放会出现错误，所以建议跟系统生命周期绑定
     */
    private void releaseCamera() {
        if (mCamera != null) {
            //将相机的回调置空,取消mCamera跟surfaceView的关联操作
            mCamera.setPreviewCallback(null);
            //取消掉相机取景功能
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开始camera与surfaceview的绑定
        setStartPreview(mCamera, holder);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        //重启整个功能，首先stopCamera,将相机进行关闭
        mCamera.stopPreview();
        setStartPreview(mCamera, holder);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //释放相机
        releaseCamera();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bg_frame:
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        Log.d("点击屏幕", "hahahah");
                    }
                });
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera_mrliu_two_good, container, false);
        initView(v);
        initEvent();
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            uploadPicture = (UploadPictureInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement UploadPictureInterface");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mPhotoTaked=false;
        resetView();
        //camera初始化
        if (mCamera == null) {
            mCamera = getCamera();
            if (holder != null) {
                setStartPreview(mCamera, holder);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //释放camera
        releaseCamera();
    }


    private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }
    @SuppressWarnings("deprecation")
    private Size getMatchedSupportedSize(List<Size> sizes, Size previewSize) {
        Size bestSize = sizes.get(0);
        boolean has1080 = false;
        double bili = (double) previewSize.width / previewSize.height;
        for (Size s : sizes) {
//            Log.d("尺寸组合：","width"+s.width+"height"+s.height);
            if (Math.abs((double) s.width / s.height - bili) < 0.005) {
                int smallOne = (s.width - s.height > 0) ? s.height : s.width;
                if (smallOne == 1080) {
                    bestSize = s;
                    has1080 = true;
                    break;
                }
            }
        }
        if (!has1080) {
            for (Size s : sizes) {
                if (Math.abs((double) s.width / s.height - bili) < 0.005) {
                    int smallOne = (s.width - s.height > 0) ? s.height : s.width;
                    if (smallOne > 900 && smallOne < 1200) {
                        bestSize = s;
                        break;
                    }
                }
            }
        }

        return bestSize;
    }
    public void reTakenPic(boolean isTaken){
        mPhotoTaked = isTaken;
        resetView();
    }
    private void resetView() {
        //如果沒有拍照
        if (!mPhotoTaked) {
            mCountText.setText("");
            mPhotoButton.setEnabled(true);
            mPhotoButton.setVisibility(View.VISIBLE);//拍照按鈕可見
            mCancleButton.setEnabled(true);
            mCancleButton.setVisibility(View.VISIBLE);//退出按鈕可見
            mOKButton.setEnabled(false);
            mOKButton.setVisibility(View.INVISIBLE);//上傳圖片按鈕不可見
            mChongpaiButton.setEnabled(false);
            mChongpaiButton.setVisibility(View.INVISIBLE);//重拍按鈕不可見
            try {
                if(mCamera != null) {
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                Log.e(TAG, "启动预览失败", e);
                mCamera.release();
                mCamera = null;
            }
        } else {
//            mCountText.setText("不满意可重新拍照");
            mOKButton.setEnabled(true);
            mOKButton.setVisibility(View.VISIBLE);//上傳圖片按鈕可見
            mChongpaiButton.setEnabled(true);
            mChongpaiButton.setVisibility(View.VISIBLE);//重拍按鈕可見
            mPhotoButton.setEnabled(false);
            mPhotoButton.setVisibility(View.INVISIBLE);//拍照不按鈕可見
            mCancleButton.setEnabled(false);
            mCancleButton.setVisibility(View.INVISIBLE);//退出不按鈕可見
            try {
                if(mCamera != null) {
                    mCamera.stopPreview();
                }
            } catch (Exception e) {
                Log.e(TAG, "启动预览失败", e);
                mCamera.release();
                mCamera = null;
            }
        }
    }

    public interface UploadPictureInterface {
        public void uploadPicture(String mPhotoFilePath);
    }

    private UploadPictureInterface uploadPicture;

    private String getImageDir() {
        String storagePath = null;
        String sdStatus = Environment.getExternalStorageState();
        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            storagePath = SD_IMAGES_PATH;
        } else {
            storagePath = DATA_IMAGES_PATH;
        }

        return storagePath;
    }

    private String getImageName() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + AppConfig.NOW_LONGITUDE + "_" + AppConfig.NOW_LATITUDE + ".jpeg";
    }
}

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.bupt.pm25.util.BitmapUtils;
import com.bupt.pm25.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CameraFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "CameraFragment";
    public static final int RESULT_OK = -1;
    public static final String EXTRA_OK_PHOTOFILENAME_STRING = "extra_ok_photoFileName_string";
    public static final String SD_IMAGES_PATH = Environment.getExternalStorageDirectory().getPath() + "/cn.edu.bupt/";
    public static final String DATA_IMAGES_PATH = "/data/data/cn.edu.bupt/images/";

    private SurfaceView mSurfaceView;
    private View mBgFrame;
    //    private ImageView mCountImageView;
    private TextView mCountText;
    private Button mPhotoButton;//拍照按鈕
    private Button mCancleButton;//退出按鈕
    private Button mOKButton;//上傳圖片按鈕
    private TextView mChongpaiButton;//重拍按鈕
    private boolean mPhotoTaked;//是否拍照
    private String mPhotoFilePath = new String();
    private Camera mCamera;
    private Camera.PictureCallback mJpegCallBack = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Date photoDate = new Date();
            String shortName = new SimpleDateFormat("yyyyMMddHHmmss").format(photoDate) +"_"+AppConfig.NOW_LONGITUDE +"_"+AppConfig.NOW_LATITUDE+ ".jpeg";
            mPhotoFilePath = AppConfig.APP_FOLDER + shortName;
            String photoTime=new SimpleDateFormat("HH:mm:ss").format(photoDate);
            FileOutputStream fos = null;
            File pictureFile = FileUtils.createFileSuccessful(getActivity(), mPhotoFilePath);

            /*以下三行为侯哥的代码*/
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
        mPhotoButton = (Button) v.findViewById(R.id.button_takephoto);
        mChongpaiButton = (TextView) v.findViewById(R.id.button_chongpai);
        mOKButton = (Button) v.findViewById(R.id.button_ok);
        mCancleButton = (Button) v.findViewById(R.id.button_cancle);
        mCountText = (TextView) v.findViewById(R.id.count_textView);
        mPhotoButton.setVisibility(View.VISIBLE);
        mCancleButton.setVisibility(View.VISIBLE);
        mOKButton.setVisibility(View.INVISIBLE);
        mChongpaiButton.setVisibility(View.INVISIBLE);
    }
    private void initEvent(){
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exception) {
                    Log.e(TAG, "设置预览失败", exception);
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if (mCamera == null) {
                    return;
                }
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(0, info);//0是默认的第一个相机

                int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                int degrees = 0;
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 270;
                        break;
                }
                int result;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = (info.orientation + degrees) % 360;
                    result = (360 - result) % 360; // compensate the mirror
                } else { // back-facing
                    result = (info.orientation - degrees + 360) % 360;
                }

                Camera.Parameters parameters = mCamera.getParameters();
                Size previewSize = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
                parameters.setPreviewSize(previewSize.width, previewSize.height);
                parameters.setRotation(result);
                Log.d("预览尺寸", previewSize.width + "*" + previewSize.height);

                String previewSizesString = parameters.get("preview-size-values");
                Log.d("预览全尺寸", previewSizesString);

                Size pictureSize = getMatchedSupportedSize(parameters.getSupportedPictureSizes(), previewSize);
                Log.d("拍照尺寸", pictureSize.width + "*" + pictureSize.height);
                parameters.setPictureSize(pictureSize.width, pictureSize.height);
                String pictureSizesString = parameters.get("picture-size-values");
                Log.d("拍照全尺寸", pictureSizesString);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(result);
                try {
                    mCamera.startPreview();
                    mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                } catch (Exception e) {
                    Log.e(TAG, "启动预览失败", e);
                    mCamera.release();
                    mCamera = null;
                }
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });

        mPhotoButton.setEnabled(true);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(null, null, mJpegCallBack);
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
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                uploadPicture.uploadPicture(mPhotoFilePath);
            }
        });
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
    public void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {//为什么要在这里增加打开指定相机。相当于初始化。
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
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
                mCamera.startPreview();
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

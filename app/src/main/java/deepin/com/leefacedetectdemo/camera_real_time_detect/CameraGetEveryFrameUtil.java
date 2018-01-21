package deepin.com.leefacedetectdemo.camera_real_time_detect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import deepin.com.leefacedetectdemo.ImageUtils;

public class CameraGetEveryFrameUtil {

    public interface onFrameDeocodedCallback {
        void onPhotoResult(byte[] bytes);
    }

    Activity mContext;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private ImageView iv_show;
    private int viewWidth, viewHeight;//mSurfaceView的宽和高
    private onFrameDeocodedCallback mOnFrameDeocodedCallback;
    Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            Camera.Size size = camera.getParameters().getPreviewSize();
            try {
                YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                if (image != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

                    Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                    if (mOnFrameDeocodedCallback != null) {
                        mOnFrameDeocodedCallback.onPhotoResult(ImageUtils.bitmap2Bytes(bmp, Bitmap.CompressFormat.JPEG));
                    }
                    //**********************
                    //因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上
//                        rotateMyBitmap(bmp);
                    //**********************************

                    Log.i("leeTest", "image getWidth:" + image.getWidth());
                    Log.i("leeTest", "image getHeight:" + image.getHeight());
                    stream.close();
                }
            } catch (Exception ex) {

                Log.e("Sys", "Error:" + ex.getMessage());
            }
        }
    };

    public onFrameDeocodedCallback getmOnFrameDeocodedCallback() {
        return mOnFrameDeocodedCallback;
    }

    public void setmOnFrameDeocodedCallback(onFrameDeocodedCallback mOnFrameDeocodedCallback) {
        this.mOnFrameDeocodedCallback = mOnFrameDeocodedCallback;
    }


    public CameraGetEveryFrameUtil(Activity context, SurfaceView surfaceView) {
        mContext = context;
        mSurfaceView = surfaceView;

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mSurfaceHolder = mSurfaceView.getHolder();
        // mSurfaceView 不需要自己的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // mSurfaceView添加回调
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) { //SurfaceView创建
                // 初始化Camera
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { //SurfaceView销毁
                // 释放Camera资源
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                }
            }
        });
//
    }


    /**
     * SurfaceHolder 回调接口方法
     */
    private void initCamera() {
        mCamera = Camera.open();//默认开启后置
        mCamera.setDisplayOrientation(90);//摄像头进行旋转90°
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                //设置预览照片的大小
                parameters.setPreviewFpsRange(viewWidth, viewHeight);
                //设置相机预览照片帧数
                parameters.setPreviewFpsRange(4, 10);
                //设置图片格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                //设置图片的质量
                parameters.set("jpeg-quality", 90);
                //设置照片的大小
                parameters.setPictureSize(viewWidth, viewHeight);

//                parameters.setRotation(270);
                //通过SurfaceView显示预览
                mCamera.setPreviewDisplay(mSurfaceHolder);
                //开始预览
                mCamera.startPreview();
                change2FrontCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void getPreViewImage() {
        mCamera.setPreviewCallback(mPreviewCallback);
    }

    void change2FrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            //现在是后置，变更为前置
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                mCamera.stopPreview();//停掉原来摄像头的预览
                mCamera.release();//释放资源
                mCamera = null;//取消原来摄像头
                mCamera = Camera.open(i);//打开当前选中的摄像头
                mCamera.setDisplayOrientation(90);//摄像头进行旋转90°

                Camera.Parameters parameters = mCamera.getParameters();
                //设置预览照片的大小
                parameters.setPreviewFpsRange(viewWidth, viewHeight);
                //设置相机预览照片帧数
                parameters.setPreviewFpsRange(4, 10);
                //设置图片格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                //设置图片的质量
                parameters.set("jpeg-quality", 90);
                //设置照片的大小
                parameters.setPictureSize(viewWidth, viewHeight);
//                parameters.setRotation(90); // 没有生效，需要旋转90度才是真实的（显示是正常的）
                try {
                    mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();//开始预览
                break;

            }
        }
    }
}
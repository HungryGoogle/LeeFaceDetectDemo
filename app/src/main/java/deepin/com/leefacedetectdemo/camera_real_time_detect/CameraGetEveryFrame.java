package deepin.com.leefacedetectdemo.camera_real_time_detect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import deepin.com.leefacedetectdemo.R;

public class CameraGetEveryFrame extends Activity implements View.OnClickListener {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private ImageView iv_show;
    private int viewWidth, viewHeight;//mSurfaceView的宽和高

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        iv_show = (ImageView) findViewById(R.id.iv_show_camera2_activity);
        //mSurfaceView
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view_camera2_activity);
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
        //设置点击监听
        mSurfaceView.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mSurfaceView != null) {
            viewWidth = mSurfaceView.getWidth();
            viewHeight = mSurfaceView.getHeight();
        }
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
                //通过SurfaceView显示预览
                mCamera.setPreviewDisplay(mSurfaceHolder);
                //开始预览
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 点击回调方法
     */
    @Override
    public void onClick(View v) {
        if (mCamera == null) return;

        getPreViewImage();
    }

    private void getPreViewImage() {

        mCamera.setPreviewCallback(new Camera.PreviewCallback(){

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Size size = camera.getParameters().getPreviewSize();
                try{
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                    if(image!=null){
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                        //**********************
                        //因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上
//                        rotateMyBitmap(bmp);
                        //**********************************

                        Log.i("leeTest","image getWidth:"+ image.getWidth());
                        Log.i("leeTest","image getHeight:"+ image.getHeight());
                        stream.close();
                    }
                }catch(Exception ex){

                    Log.e("Sys","Error:"+ex.getMessage());
                }
            }
        });
    }

    public void rotateMyBitmap(Bitmap bmp){
//        //*****旋转一下
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//
//        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
//
//        Bitmap nbmp2 = Bitmap.createBitmap(bmp, 0,0, bmp.getWidth(),  bmp.getHeight(), matrix, true);
//
//

    };
}
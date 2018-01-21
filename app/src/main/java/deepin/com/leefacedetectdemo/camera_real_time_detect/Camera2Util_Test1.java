package deepin.com.leefacedetectdemo.camera_real_time_detect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import deepin.com.leefacedetectdemo.R;

public class Camera2Util_Test1 extends Activity {

    private ImageView iv_show;
    private SurfaceView mSurfaceView;
    Camera2Util mCameraUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_camera_util0);
        initVIew();

        mCameraUtil = new Camera2Util(this, mSurfaceView);
        mCameraUtil.setmOnFrameDeocodedCallback(new Camera2Util.onFrameDeocodedCallback() {
            @Override
            public void onPhotoResult(final byte[] bytes) {
                showPicture(bytes);

            }
        });

        findViewById(R.id.id_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraUtil.takePicture();
            }
        });

    }

    private void showPicture(final byte[] bytes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    iv_show.setVisibility(View.VISIBLE);
                    iv_show.setImageBitmap(bitmap);
                }
            }
        });
    }

    /**
     * 初始化
     */
    private void initVIew() {
        iv_show = (ImageView) findViewById(R.id.iv_show_camera2_activity);
        //mSurfaceView
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view_camera2_activity);
    }
}

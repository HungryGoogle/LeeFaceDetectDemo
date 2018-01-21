package deepin.com.leefacedetectdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.aip.face.AipFace;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import deepin.com.leefacedetectdemo.camera_real_time_detect.CameraGetEveryFrameUtil;

public class TakeCameraFrameToDetectActivity extends Activity {

    //设置APPID/AK/SK
    public static final String APP_ID = "10724478";
    public static final String API_KEY = "lFUgiAI1IXrKhXNjZccdTo2M";
    public static final String SECRET_KEY = "WMaebG71y7zAygKI5PUEvG4Iv98v9Upm";
    TextView mResultShow;
    SurfaceView mSurfaceView;
    CameraGetEveryFrameUtil mCameraGetEveryFrameUtil;
    long mLastRequestTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // 初始化一个AipFace
        final AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        mResultShow = findViewById(R.id.id_face_result);
        mSurfaceView = findViewById(R.id.id_surface_view);

        mCameraGetEveryFrameUtil = new CameraGetEveryFrameUtil(this, mSurfaceView);


        findViewById(R.id.id_face_detect1).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Button) findViewById(R.id.id_face_detect1)).setText("实时识别中..");
                    }
                });
                mCameraGetEveryFrameUtil.getPreViewImage();
                mCameraGetEveryFrameUtil.setmOnFrameDeocodedCallback(new CameraGetEveryFrameUtil.onFrameDeocodedCallback() {
                    @Override
                    public void onPhotoResult(final byte[] bytes) {
                        if (System.currentTimeMillis() - mLastRequestTime < 3000) {
                            return;
                        }
                        mLastRequestTime = System.currentTimeMillis();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                Drawable drawable = getDrawable(R.drawable.head_iamge_2);
//                                final byte[] bytes = ImageUtils.drawable2Bytes(drawable, JPEG);
                                if (bytes == null) {
                                    return;
                                }

                                final JSONObject res = client.detect(bytes, new HashMap<String, String>());
                                try {
                                    System.out.println(res.toString(2));
                                    showResult(res);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });

            }
        });

    }

    private void showResult(final JSONObject res) {
        if (res == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mResultShow.setText(res.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

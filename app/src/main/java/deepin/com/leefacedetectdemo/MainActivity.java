package deepin.com.leefacedetectdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.aip.face.AipFace;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.graphics.Bitmap.CompressFormat.JPEG;

public class MainActivity extends Activity {
    //设置APPID/AK/SK
    public static final String APP_ID = "10724478";
    public static final String API_KEY = "lFUgiAI1IXrKhXNjZccdTo2M";
    public static final String SECRET_KEY = "WMaebG71y7zAygKI5PUEvG4Iv98v9Upm";
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化一个AipFace
        final AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        mTextView = findViewById(R.id.id_face_result);

        findViewById(R.id.id_face_detect1).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                mTextView.setText("计算中 ...");
                // 调用接口
                Drawable drawable = getDrawable(R.drawable.head_iamge_1);
                final byte[] bytes = ImageUtils.drawable2Bytes(drawable, JPEG);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final JSONObject res = client.detect(bytes, new HashMap<String, String>());
                        try {
                            System.out.println(res.toString(2));
                            showResult(res);

                            /**
                             * eg
                             * {
                             "result_num": 1,
                             "result": [
                             {
                             "location": {
                             "left": 148,
                             "top": 251,
                             "width": 308,
                             "height": 230
                             },
                             "face_probability": 0.79689013957977,
                             "rotation_angle": -3,
                             "yaw": 3.5611751079559,
                             "pitch": 3.5716376304626,
                             "roll": -3.1866459846497
                             }
                             ],
                             "log_id": 3390276698012116
                             }

                             */
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


        findViewById(R.id.id_face_detect2).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                mTextView.setText("计算中 ...");
                // 调用接口
                Drawable drawable = getDrawable(R.drawable.head_iamge_2);
                final byte[] bytes = ImageUtils.drawable2Bytes(drawable, JPEG);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject res = client.detect(bytes, new HashMap<String, String>());
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

    private void showResult(final JSONObject res) {
        if(res == null){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mTextView.setText(res.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}

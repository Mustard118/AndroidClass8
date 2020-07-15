package com.example.mycamera;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.mycamera.R;

public class MainActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */

    // camera 类
    private Camera camera = null;
    // 继承surfaceView的自定义view 用于存放照相的图片
    private CameraView cv = null;

    // 回调用的picture，实现里边的onPictureTaken方法，其中byte[]数组即为照相后获取到的图片信息
    private Camera.PictureCallback picture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 主要就是将图片转化成drawable，设置为固定区域的背景（展示图片），当然也可以直接在布局文件里放一个surfaceView供使用。
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            Drawable d = BitmapDrawable.createFromStream(bais, Environment
                    .getExternalStorageDirectory().getAbsolutePath()
                    + "/img.jpeg");
            l.setBackgroundDrawable(d);
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    // 按钮 布局等定义，不作赘述
    Button btn1 = null;
    Button btn2 = null;
    LinearLayout l = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);

        l = (LinearLayout) findViewById(R.id.cameraView);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    //主要的surfaceView，负责展示预览图片，camera的开关
    class CameraView extends SurfaceView {

        //
        private SurfaceHolder holder = null;

        public CameraView(Context context) {
            super(context);
            holder = this.getHolder();

            holder.addCallback(new SurfaceHolder.Callback() {

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format,
                                           int width, int height) {
                    Camera.Parameters parameters = camera.getParameters();
                    //以下注释掉的是设置预览时的图像以及拍照的一些参数
                    // parameters.setPictureFormat(PixelFormat.JPEG);
                    // parameters.setPreviewSize(parameters.getPictureSize().width,
                    // parameters.getPictureSize().height);
                    // parameters.setFocusMode("auto");
                    // parameters.setPictureSize(width, height);
                    camera.setParameters(parameters);
                    camera.startPreview();
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    camera = Camera.open();

                    try {
                        //设置camera预览的角度，因为默认图片是倾斜90度的
                        camera.setDisplayOrientation(90);
                        //设置holder主要是用于surfaceView的图片的实时预览，以及获取图片等功能，可以理解为控制camera的操作..
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        camera.release();
                        camera = null;
                        e.printStackTrace();
                    }

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    //顾名思义可以看懂
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            });
//          holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn1) {
            l.removeAllViews();
            cv = new CameraView(MainActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT);
            l.addView(cv, params);
        } else if (v == btn2) {
            camera.takePicture(null, null, picture);
        }
    }
}
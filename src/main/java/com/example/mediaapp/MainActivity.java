package com.example.mediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.PathUtils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSION_REQUEST_CAMERA_CODE=10;
    private final int REQUEST_CODE_TAKE_PHOTO=20;
    private final int REQUEST_CODE_RECORD=30;

    private Button bt_cam,bt_vid,bt_mycam;
    public ImageView iv_photo;
    private String takeImagePath;
    private String mp4Path;
    private VideoView mVideoView;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_cam=findViewById(R.id.bt_camera);
        bt_vid=findViewById(R.id.bt_video);
        bt_mycam=findViewById(R.id.bt_mycamera);
        iv_photo=findViewById(R.id.iv_photo);
        mVideoView=findViewById(R.id.vv_video);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "hava this permission", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "no this permission", Toast.LENGTH_SHORT).show();
        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CAMERA_CODE);

        bt_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent call_cam=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takeImagePath=getOutputMediaPath();
                //Uri pathuri=Uri.parse(takeImagePath);
                //call_cam.putExtra(MediaStore.EXTRA_OUTPUT, pathuri);
                call_cam.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(MainActivity.this,takeImagePath));
                if (call_cam.resolveActivity(getPackageManager()) == null) {
                    startActivityForResult(call_cam,REQUEST_CODE_TAKE_PHOTO);
                }
            }
        });
        bt_vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                mp4Path=getOutputMediaPath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(MainActivity.this,mp4Path));
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
                if(intent.resolveActivity(getPackageManager())==null){
                    startActivityForResult(intent,REQUEST_CODE_RECORD);
                }
            }
        });
    }

    private void initCamera(){
        mCamera=Camera.open();
        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.set("orientation","portrait");
        parameters.set("rotation",90);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
    }

    public static Uri getUriForFile(Context context, String path) {
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context.getApplicationContext(), context.getApplicationContext().getPackageName() + ".fileprovider", new File(path));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_TAKE_PHOTO && resultCode==RESULT_OK){
            int targetWidth=iv_photo.getWidth();
            int targetHeight=iv_photo.getHeight();
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(takeImagePath,options);
            int photoWidth=options.outWidth;
            int photoHeight=options.outHeight;
            int scaleFactor=Math.min(photoWidth/targetWidth,photoHeight/targetHeight);
            options.inJustDecodeBounds=false;
            options.inSampleSize=scaleFactor;
            Bitmap bitmap =BitmapFactory.decodeFile(takeImagePath,options);
            iv_photo.setImageBitmap(bitmap);
//            Bundle extras=data.getExtras();
//            Bitmap bitmap=(Bitmap)extras.get("data");
//            iv_photo.setImageBitmap(bitmap);
        }
        if(requestCode==REQUEST_CODE_RECORD&&resultCode==RESULT_OK){
            play();
        }
    }

    private void play(){
        mVideoView.setVideoPath(mp4Path);
        mVideoView.start();
    }

    private String getOutputMediaPath(){
        File mediaStorageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile=new File(mediaStorageDir,"IMG_"+timeStamp+".jpg");
        if(!mediaFile.exists()){
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

}
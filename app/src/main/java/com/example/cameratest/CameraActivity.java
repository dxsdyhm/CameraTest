package com.example.cameratest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.File;
import java.io.IOException;

/**
 * 原始API预览数据
 */
public class CameraActivity extends AppCompatActivity {
    public static SurfaceView sv_camera;
    private Camera camera;
    private TextView txLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        txLog=findViewById(R.id.tx_log);
        findViewById(R.id.btn_pre).setOnClickListener(view -> initCamera());
    }

    int a=0;
    private void initCamera() {
        sv_camera=findViewById(R.id.sv_camera);
        if (camera != null) {
            return;
        }
        camera = Camera.open(0);
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                Log.e("CameraActivity", "onPreviewFrame---->" + bytes.length);
                if(a==0){
                    FileIOUtils.writeFileFromBytesByChannel(PathUtils.getExternalDownloadsPath() + File.separator + "b.yuv", bytes, true);
                    a++;
                }
                addLog(" -- Frame buffer len:"+bytes.length);
            }
        });
        try {
            camera.setPreviewDisplay(sv_camera.getHolder());
            camera.startPreview();//开始预览
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addLog(String txt){
        if(txLog.getText().length()>=900){
            txLog.setText("");
        }
        txLog.append(TimeUtils.getNowString()+txt+"\n");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCamera();
    }

    private void stopCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }
}
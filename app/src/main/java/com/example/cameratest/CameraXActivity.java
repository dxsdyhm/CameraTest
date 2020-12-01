package com.example.cameratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.TextView;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraXActivity extends AppCompatActivity {
    private static final String TAG="CameraXActivity";
    private ProcessCameraProvider cameraProvider;
    private PreviewView previewSurface;
    private TextView txLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x);
        previewSurface=findViewById(R.id.sv_camera_x);
        txLog=findViewById(R.id.tx_log);
        findViewById(R.id.btn_pre).setOnClickListener(view -> startCameraX());
    }

    private void startCameraX() {
        final ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);
        listenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = listenableFuture.get();
                    prepare();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void prepare() {
        // Preview
        Preview.Builder preBuilder = new Preview.Builder()
                .setTargetResolution(new Size(1280, 720));
        Preview preview = preBuilder.build();

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        // ImageAnalysis
        ImageAnalysis.Builder builder = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
//                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST);


        ImageAnalysis imageAnalysis = builder.build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                int rotationDegrees = image.getImageInfo().getRotationDegrees();
                // insert your code here.
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                Log.d(TAG, image.getWidth() + "," + image.getHeight());
                addLog("w:"+image.getWidth() + ",h:" + image.getHeight());
                image.close();
            }
        });

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll();

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(previewSurface.getSurfaceProvider());
        } catch (Exception executor) {
            Log.e(TAG, "Use case binding failed:" + executor);
        }
    }

    private void addLog(String txt){
        if(txLog.getText().length()>=900){
            txLog.setText("");
        }
        txLog.append(TimeUtils.getNowString()+txt+"\n");
    }
}
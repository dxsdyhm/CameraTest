package com.example.cameratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.UtilsTransActivity;

import java.security.Permissions;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requirePermission();
    }

    private void requirePermission() {
        PermissionUtils.permission(PermissionConstants.CAMERA,PermissionConstants.STORAGE)
                .rationale((activity, shouldRequest) -> shouldRequest.again(true))
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        findViewById(R.id.btn_camera).setOnClickListener(view -> ActivityUtils.startActivity(CameraActivity.class));
                        findViewById(R.id.btn_camera_x).setOnClickListener(view -> ActivityUtils.startActivity(CameraXActivity.class));
                        findViewById(R.id.btn_camera_x_low).setOnClickListener(view -> ActivityUtils.startActivity(CameraXLowActivity.class));
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        boolean isForever=permissionsDeniedForever!=null&& permissionsDeniedForever.size()>0;
                        PermissionUtils.launchAppDetailsSettings();
                    }
                }).request();
    }
}
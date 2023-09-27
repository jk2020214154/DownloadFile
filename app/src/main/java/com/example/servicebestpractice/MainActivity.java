// MainActivity.java
package com.example.servicebestpractice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DownloadService.DownloadBinder downloadBinder;
    private boolean isContinue = false;
    private boolean isDownload = false;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startDownload = findViewById(R.id.start_download);
        Button pauseDownload = findViewById(R.id.pause_download);
        Button cancelDownload = findViewById(R.id.cancel_download);
        startDownload.setOnClickListener(this);
        pauseDownload.setOnClickListener(this);
        cancelDownload.setOnClickListener(this);
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent); // 启动服务
        bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务

        // 检查并请求读取外部存储的权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

    }

    @Override
    public void onClick(View v) {
        String url = "https://dldir1.qq.com/qqfile/qq/PCQQ9.7.16/QQ9.7.16.29187.exe";
        if (downloadBinder == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.start_download:
                downloadBinder.startDownload(url);
                Button startDownload = findViewById(R.id.start_download);
                isDownload = true;
                startDownload.setEnabled(false);
                break;
            case R.id.pause_download:
                Button pauseDownload = findViewById(R.id.pause_download);
                if (isDownload) {
                    if (!isContinue) {
                        downloadBinder.pauseDownload();
                        pauseDownload.setText("Continue_download");
                        pauseDownload.setTextColor(Color.YELLOW);
                        pauseDownload.setBackgroundColor(Color.RED);
                        isContinue = true;
                    } else {
                        downloadBinder.startDownload(url);
                        pauseDownload.setText("pause_download");
                        pauseDownload.setTextColor(Color.BLACK);
                        pauseDownload.setBackgroundColor(Color.LTGRAY);
                        isContinue = false;
                    }
                } else {
                    Toast.makeText(this, "请先点击下载", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel_download:
                downloadBinder.cancelDownload();
                Button startDownload2 = findViewById(R.id.start_download);
                startDownload2.setEnabled(true);
                Button pauseDownload2 = findViewById(R.id.pause_download);
                pauseDownload2.setText("pause_download");
                pauseDownload2.setTextColor(Color.BLACK);
                pauseDownload2.setBackgroundColor(Color.LTGRAY);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }


    // 处理权限请求结果
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已被授予，可以执行相关操作
            } else {
                // 权限被拒绝，可以向用户解释权限的重要性，并采取相应措施
                Toast.makeText(this, "权限被拒绝，无法访问外部存储", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.servicebestpractice;

public interface DownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused(int progress);

    void onCanceled();

}

package com.feelschaotic.sdkhotfix.sdk.service

import android.util.Log
import com.alibaba.sdk.android.oss.*
import com.feelschaotic.sdkhotfix.sdk.entity.DownloadRequest
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils
import java.io.File
import com.liulishuo.filedownloader.BaseDownloadTask

import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import java.lang.Exception


/**
 * 阿里云oss文件下载
 * @author feelschaotic
 * @create 2019/6/26.
 */
object SdkFileDownloader {
    private val TAG = "sdk-patch"

    /**
     * oss流式下载
     */
    fun downloadSync(request: DownloadRequest, listener: RespondListener<String>) {

        val dirPathFile = File(request.fileDir)
        if (!dirPathFile.exists() && !dirPathFile.mkdirs()) {
            LogUtils.e(TAG, "创建补丁下载目录失败，下载终止")
            return
        }

        Log.e(
            TAG,
            "downloadSync: " + request.fileDir + "," + request.patchName + "," + request.objectKey
        )

        FileDownloader.getImpl().create(request.objectKey)
            .setPath(request.fileDir + File.separator + request.patchName)
            .setListener(object : FileDownloadListener() {
                override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}
                override fun connected(
                    task: BaseDownloadTask,
                    etag: String,
                    isContinue: Boolean,
                    soFarBytes: Int,
                    totalBytes: Int
                ) {
                }

                override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}
                override fun blockComplete(task: BaseDownloadTask) {}
                override fun retry(
                    task: BaseDownloadTask,
                    ex: Throwable,
                    retryingTimes: Int,
                    soFarBytes: Int
                ) {
                }

                override fun completed(task: BaseDownloadTask) {
                    Log.e(TAG, "completed: ${task.path}")
                    listener.onSuccess(task.path)
                }

                override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}
                override fun error(task: BaseDownloadTask, e: Throwable) {
                    listener.onError(Exception(e))
                }

                override fun warn(task: BaseDownloadTask) {}
            }).start()
    }
}
package com.mleval.pexelsapp.data.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageDownloader @Inject constructor(
    @param:ApplicationContext private val context: Context
): DownLoader {

    private  val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun download(url: String): Long {

        if (!isInternetAvailable()) {
            Toast.makeText(
                context,
                "No internet connection",
                Toast.LENGTH_SHORT
            ).show()
            return -1L
        }

        val request = DownloadManager.Request(url.toUri())
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setTitle("Downloading image")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "image_${System.currentTimeMillis()}.jpg"
            )
        return downloadManager.enqueue(request)
    }
    private fun isInternetAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null
    }
}
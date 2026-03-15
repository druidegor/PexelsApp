package com.mleval.pexelsapp.data.downloader

interface DownLoader {

    fun download(url: String): Long
}
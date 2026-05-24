package com.example.moviecatalog

import android.app.Application
import com.example.moviecatalog.data.AppContainer
import com.example.moviecatalog.data.DefaultAppContainer

class MovieApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
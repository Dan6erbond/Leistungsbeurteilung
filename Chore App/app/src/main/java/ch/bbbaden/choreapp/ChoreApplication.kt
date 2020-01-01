package ch.bbbaden.choreapp

import android.app.Application
import android.content.Context
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

class ChoreApplication : Application(), CameraXConfig.Provider {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig(this)
    }
}
package com.example.golda.dagger

import android.app.Application
import android.content.Context
import timber.log.Timber
import javax.inject.Inject

class App: Application() {

    private lateinit var appComponent: AppComponent

    companion object {
        fun getAppComponent(context: Context): AppComponent {
            return (context.applicationContext as App).appComponent
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.d("Application start")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }
}
//package com.example.golda.tasks
//
//import android.content.Context
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import android.app.NotificationManager
//import android.app.NotificationChannel
//import androidx.core.app.NotificationCompat
//import com.example.golda.R
//
//
//class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
//    Worker(appContext, workerParams) {
//    override fun doWork(): Result {
//        remind("My title", "My message")
//        return Result.success()
//    }
//
//    private fun remind(title: String, message: String) {
//        val notificationManager =
//            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        //If on Oreo then notification required a notification channel.
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            val channel =
//                NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//        val notification = NotificationCompat.Builder(applicationContext, "default")
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(R.mipmap.ic_launcher)
//        notificationManager.notifyDataChange(1, notification.build())
//    }
//}
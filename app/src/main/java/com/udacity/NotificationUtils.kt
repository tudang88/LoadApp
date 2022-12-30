package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.details.DetailActivity


data class DownloadStatusInfo(val url: String?, val fileName: String?, val status: Int)

/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(
    notificationId: Int,
    messageBody: DownloadStatusInfo,
    applicationContext: Context
) {
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationId,
        createDetailIntent(messageBody, applicationContext),
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val notificationMessage = when (messageBody.status) {
        DownloadManager.STATUS_SUCCESSFUL -> applicationContext.getString(R.string.notification_description)
        DownloadManager.STATUS_FAILED -> applicationContext.getString(R.string.notification_description_failed)
        else -> "Unknown Error"
    }

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(notificationMessage)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            pendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    // send notify
    notify(notificationId, builder.build())

}

/**
 * create notification channel
 */
fun NotificationManager.createNotificationChannel(
    channelId: String,
    channelName: String, applicationContext: Context
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel =
            // change importance
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                .apply { setShowBadge(false) }
        notificationChannel.enableVibration(true)
        notificationChannel.description =
            applicationContext.getString(R.string.notification_title)
        createNotificationChannel(notificationChannel)
    }
}

/**
 * cancel all notification
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}

/**
 * create intent for DetailActivity
 */
private fun createDetailIntent(downloadStatus: DownloadStatusInfo, context: Context): Intent {
    val intent = Intent(context, DetailActivity::class.java)
    val statusString = convertDownloadStatus(downloadStatus.status)
    intent.putExtra("STATUS", statusString)
    intent.putExtra(
        "FILE_NAME",
        downloadStatus.fileName ?: "None"
    )
    intent.putExtra("URL_NAME", downloadStatus.url ?: "None")
    return intent
}

/**
 * convert status from
 * Int -> String
 */
private fun convertDownloadStatus(status: Int): String {
    return when (status) {
        DownloadManager.STATUS_FAILED -> "Failed"
        DownloadManager.STATUS_SUCCESSFUL -> "Success"
        else -> "Unknown"
    }
}
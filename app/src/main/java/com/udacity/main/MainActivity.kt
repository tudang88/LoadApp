package com.udacity.main


import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.*
import com.udacity.databinding.ActivityMainBinding
import timber.log.Timber
import java.io.File
import java.util.*


private val URL_LIST = listOf(
    "https://github.com/bumptech/glide/archive/master.zip",
    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive",
    "https://github.com/square/retrofit/archive/master.zip"
)
private val NOTIFICATION_ID = 0

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var downloadManager: DownloadManager

    private lateinit var notificationManager: NotificationManager
    private lateinit var customButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        // create notification channel
        notificationManager.createNotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
            applicationContext
        )
        // notification will launch the DetailActivity
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // receiver
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        // register network callback
        registerNetworkListener()
        customButton = binding.contentMainLayout.customButton
        customButton.setOnClickListener {
            Timber.d("onButtonClick from Activity")
            // cancel existing notification
            notificationManager.cancelNotifications()
            if (!getNetworkStatus()) {
                Toast.makeText(this, "Network not available!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val checkId = binding.contentMainLayout.radioGroup.checkedRadioButtonId
            if (-1 == checkId) {
                // show toast in case no item selected
                val toast =
                    Toast.makeText(this, getString(R.string.toast_message), Toast.LENGTH_SHORT)
                toast.show()
                customButton.buttonState = ButtonState.Clicked
            } else {
                when (checkId) {
                    R.id.glideRadioButton -> download(URL_LIST[0])
                    R.id.loadAppRadioButton -> download(URL_LIST[1])
                    R.id.retrofitRadioButton -> download(URL_LIST[2])
                }
                // change button state
                customButton.buttonState = ButtonState.Loading
            }

        }
    }

    /**
     * unregister download manager
     * receiver
     */
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    /**
     * Receiver for listening event from DownloadManager
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent?.action)) {
                if (downloadID == id) {
                    val query: DownloadManager.Query = DownloadManager.Query()
                    query.setFilterById(id)
                    val cursor = downloadManager.query(query)
                    if (!cursor.moveToFirst()) {
                        return
                    }
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))
                    val filename =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    val downloadStatusMessage = DownloadStatusInfo(url, filename, status)
                    notificationManager.sendNotification(
                        NOTIFICATION_ID,
                        downloadStatusMessage,
                        applicationContext
                    )
                    customButton.buttonState = ButtonState.Completed
                }

            }
        }
    }


    /**
     * listen to network status
     */
    private fun registerNetworkListener() {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerDefaultNetworkCallback(object : NetworkCallback() {
            override fun onLost(network: Network) {
                if (downloadID > 0) {
                    Timber.d("Stop download due to Network lost")
                    Toast.makeText(
                        applicationContext,
                        "Download terminated due to Network Lost",
                        Toast.LENGTH_SHORT
                    ).show()
                    // cancel download task
                    downloadManager.remove(downloadID)
                    // update state of button
                    customButton.buttonState = ButtonState.Completed
                }
            }
        })
    }

    /**
     * confirm network status
     * false if network not available
     * otherwise return true
     */
    private fun getNetworkStatus(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return null != cm.activeNetwork
    }

    /**
     * download file from
     * internet
     */
    private fun download(url: String) {
        var fileName = url.substring(url.lastIndexOf('/') + 1)
        fileName = fileName.substring(0, 1).toUpperCase(Locale.ROOT) + fileName.substring(1)
        val file = File(getExternalFilesDir(null), fileName)
        Timber.d("file path: ${file.path}")
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setDestinationUri(Uri.fromFile(file))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

}

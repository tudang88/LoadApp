package com.udacity.details

import android.app.Application
import android.app.NotificationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.udacity.cancelNotifications

class DetailActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _fileLocalUri = MutableLiveData<String>()
    val fileLocalUri: LiveData<String>
        get() = _fileLocalUri

    private val _orgUrl = MutableLiveData<String>()
    val orgUrl: LiveData<String>
        get() = _orgUrl

    init {
        _fileLocalUri.value = "None"
        _orgUrl.value = "None"
        cancelNotification()
    }

    /**
     * extract download status info received from
     * MainActivity
     */
    fun extractInfoFromBundle(bundle: Bundle?) {
        if (bundle != null) {
            _status.value = bundle.getString("STATUS")
            _fileLocalUri.value = bundle.getString("FILE_NAME")
            _orgUrl.value = bundle.getString("URL_NAME")
        } else {
            _status.value = "None"
            _fileLocalUri.value = "None"
            _orgUrl.value = "None"
        }
    }

    /**
     * cancel all notification when DetailActivity
     * active
     */
    private fun cancelNotification() {
        val notificationManager = ContextCompat.getSystemService(
            getApplication(),
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
    }
}

/**
 * ViewModel Factory
 */
class DetailViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailActivityViewModel::class.java)) return DetailActivityViewModel(
            app
        ) as T
        throw IllegalArgumentException(" Unable to construct ViewModel")
    }

}


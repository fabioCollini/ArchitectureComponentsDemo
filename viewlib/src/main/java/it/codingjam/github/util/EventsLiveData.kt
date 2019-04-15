package it.codingjam.github.util

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * This class is similar to SingleLiveEvent but it caches all the values when the live data is not active
 */
class EventsLiveData<T> {

    private val liveData = MutableLiveData<List<T>>()

    private var cache = emptyList<T>()

    @MainThread
    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) {
        if (liveData.hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        liveData.observe(owner, Observer {
            cache.forEach {
                observer(it)
            }
            cache = emptyList()
        })
    }

    @MainThread
    fun addEvent(t: T) {
        cache = cache + t
        liveData.value = cache
    }

    companion object {
        private const val TAG = "EventsLiveData"
    }
}
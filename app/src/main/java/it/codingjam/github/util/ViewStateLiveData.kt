package it.codingjam.github.util


import android.arch.lifecycle.MutableLiveData
import android.support.annotation.MainThread
import timber.log.Timber

class ViewStateLiveData<T>(initialState: T) : MutableLiveData<T>() {

    init {
        value = initialState
    }

    override fun getValue(): T {
        return super.getValue()!!
    }

    override fun setValue(value: T) {
        super.setValue(value)
    }

    override fun postValue(value: T) {
        super.postValue(value)
    }

    @MainThread inline fun update(updater: T.() -> T) {
        val currentState = value
        val newState = updater.invoke(currentState)
        value = newState
        Timber.d("%s", newState)
    }

    @MainThread inline fun <V> updateOnEvent(crossinline updater: T.(V) -> T): (V) -> Unit = {
        val currentState = value
        val newState = updater.invoke(currentState, it)
        value = newState
        Timber.d("%s", newState)
    }
}

package it.codingjam.github.util


import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import kotlin.reflect.KProperty

class ViewStateLiveData<T>(initialState: T, private val liveData: MutableLiveData<T> = MutableLiveData<T>()) {

    init {
        liveData.value = initialState
    }

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) =
            liveData.observe(owner, Observer { observer(it!!) })

    fun observeForever(observer: (T) -> Unit) =
            liveData.observeForever { observer(it!!) }

    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
        liveData.value = value
    }

    operator fun getValue(thisRef: ViewModel, property: KProperty<*>): T = liveData.value!!
}

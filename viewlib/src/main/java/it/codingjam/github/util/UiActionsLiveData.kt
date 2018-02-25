package it.codingjam.github.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import android.support.v4.app.FragmentActivity
import java.util.*

typealias UiAction = (FragmentActivity) -> Unit

class UiActionsLiveData {
    private val delegate = MutableLiveData<List<UiAction>>()

    private var list: MutableList<UiAction> = ArrayList()

    fun execute(action: UiAction) {
        list.add(action)
        delegate.value = list
    }

    operator fun invoke(action: UiAction) {
        execute(action)
    }

    fun observe(owner: LifecycleOwner, executor: (UiAction) -> Unit) =
            delegate.observe(owner, Observer {
                list.forEach { executor(it) }
                list = ArrayList()
            })

    @MainThread fun observeForever(executor: (UiAction) -> Unit) =
            delegate.observeForever {
                list.forEach { executor(it) }
                list = ArrayList()
            }
}

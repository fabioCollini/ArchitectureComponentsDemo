package it.codingjam.github.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import android.support.v4.app.FragmentActivity
import java.util.*

class UiActionsLiveData {
    private val delegate = MutableLiveData<List<(FragmentActivity) -> Unit>>()

    private var list: MutableList<(FragmentActivity) -> Unit> = ArrayList()

    fun execute(action: (FragmentActivity) -> Unit) {
        list.add(action)
        delegate.value = list
    }

    operator fun invoke(action: (FragmentActivity) -> Unit) {
        execute(action)
    }

    fun observe(owner: LifecycleOwner, executor: ((FragmentActivity) -> Unit) -> Unit) =
            delegate.observe(owner, Observer {
                list.forEach { executor(it) }
                list = ArrayList()
            })

    @MainThread fun observeForever(executor: ((FragmentActivity) -> Unit) -> Unit) =
            delegate.observeForever {
                list.forEach { executor(it) }
                list = ArrayList()
            }
}

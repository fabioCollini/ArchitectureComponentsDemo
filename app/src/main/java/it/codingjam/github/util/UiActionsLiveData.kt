package it.codingjam.github.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import java.util.*

class UiActionsLiveData {
    private val delegate = MutableLiveData<List<(FragmentActivity) -> Unit>>()

    private var list: MutableList<(FragmentActivity) -> Unit> = ArrayList()

    fun execute(value: (FragmentActivity) -> Unit) {
        list.add(value)
        delegate.value = list
    }

    fun <F> observe(owner: F) where F : Fragment, F : LifecycleOwner {
        delegate.observe(owner, Observer {
            list.forEach { it.invoke(owner.activity) }
            list = ArrayList()
        })
    }

    fun <A> observe(owner: A) where A : FragmentActivity, A : LifecycleOwner {
        delegate.observe(owner, Observer {
            list.forEach { it.invoke(owner) }
            list = ArrayList()
        })
    }

    @MainThread fun observeForever(activity: FragmentActivity) {
        delegate.observeForever {
            list.forEach { it.invoke(activity) }
            list = ArrayList()
        }
    }
}

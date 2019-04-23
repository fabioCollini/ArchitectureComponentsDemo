package it.codingjam.github

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import it.codingjam.github.core.utils.ComponentHolder
import it.codingjam.github.core.utils.getOrCreate


inline fun <reified C : Any> ComponentHolder.getOrCreate(lifecycleOwner: LifecycleOwner, noinline componentFactory: () -> C): C {
    val key = lifecycleOwner to C::class.java
    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            remove(key)
        }
    })
    return getOrCreate(key, C::class.java, componentFactory)
}

inline fun <reified C : Any> Application.getOrCreate(noinline componentFactory: () -> C): C =
        (this as ComponentHolder).getOrCreate(componentFactory)

inline fun <reified C : Any> FragmentActivity.getOrCreateActivityComponent(noinline componentFactory: () -> C): C =
        (application as ComponentHolder).getOrCreate(this, componentFactory)

inline fun <reified C : Any> Fragment.getOrCreateFragmentComponent(noinline componentFactory: () -> C): C =
        (requireActivity().application as ComponentHolder).getOrCreate(this, componentFactory)
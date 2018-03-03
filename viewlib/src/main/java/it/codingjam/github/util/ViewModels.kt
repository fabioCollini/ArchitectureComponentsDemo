package it.codingjam.github.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import javax.inject.Provider

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
        crossinline provider: () -> VM) = lazy(mode) {
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T1 : ViewModel> create(aClass: Class<T1>) = provider() as T1
    }).get(VM::class.java)
}

inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
        crossinline provider: () -> VM) = lazy(mode) {
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T1 : ViewModel> create(aClass: Class<T1>) = provider() as T1
    }).get(VM::class.java)
}

fun Job.async(f: suspend () -> Unit) = async(this + UI) {
    f()
}

interface ViewModelFactory {
    operator fun <VM : ViewModel> invoke(fragment: Fragment, provider: Provider<VM>): VM
}

class AndroidViewModelFactory : ViewModelFactory {
    override operator fun <VM : ViewModel> invoke(fragment: Fragment, provider: Provider<VM>): VM =
            ViewModelProviders.of(fragment, object : ViewModelProvider.Factory {
                override fun <T1 : ViewModel> create(aClass: Class<T1>) = provider.get() as T1
            }).get(ViewModel::class.java) as VM
}
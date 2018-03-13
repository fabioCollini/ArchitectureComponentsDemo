package it.codingjam.github.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import javax.inject.Provider

class ViewModelFactory(val testProvider: (() -> ViewModel)? = null) {
    inline operator fun <VM : ViewModel> invoke(fragment: Fragment, provider: Provider<VM>, crossinline init: (VM) -> Unit = {}): VM {
        return ViewModelProviders.of(fragment, object : ViewModelProvider.Factory {
            override fun <T1 : ViewModel> create(aClass: Class<T1>): T1 {
                val viewModel = testProvider?.invoke() as VM? ?: provider.get()
                return viewModel.also(init) as T1
            }
        }).get(ViewModel::class.java) as VM
    }
}
package it.codingjam.github.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import javax.inject.Provider

interface ViewModelFactory {
    operator fun <VM : ViewModel> invoke(fragment: Fragment, provider: Provider<VM>, init: (VM) -> Unit = {}): VM
}

class AndroidViewModelFactory : ViewModelFactory {
    override operator fun <VM : ViewModel> invoke(fragment: Fragment, provider: Provider<VM>, init: (VM) -> Unit): VM =
            ViewModelProviders.of(fragment, object : ViewModelProvider.Factory {
                override fun <T1 : ViewModel> create(aClass: Class<T1>) = provider.get().also(init) as T1
            }).get(ViewModel::class.java) as VM
}
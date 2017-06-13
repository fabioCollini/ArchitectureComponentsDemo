package it.codingjam.github.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment

import javax.inject.Provider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object ViewModels {
    private fun factory(provider: Provider<out ViewModel>): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>): T {
                return provider.get() as T
            }
        }
    }

    private fun <T : ViewModel> factory(provider: () -> T, initializer: (T) -> Unit): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T1 : ViewModel> create(aClass: Class<T1>): T1 {
                val t = provider()
                initializer(t)
                return t as T1
            }
        }
    }

    operator fun <T : ViewModel> get(fragment: Fragment, viewModelClass: Class<T>, provider: Provider<T>): T {
        return ViewModelProviders.of(fragment, factory(provider)).get(viewModelClass)
    }

    operator fun <T : ViewModel> get(fragment: Fragment, viewModelClass: Class<T>, provider: () -> T, initializer: (T) -> Unit): T {
        return ViewModelProviders.of(fragment, factory(provider, initializer)).get(viewModelClass)
    }

    inline fun <reified VM : ViewModel> delegate(noinline provider: () -> VM, noinline initializer: (VM) -> Unit = {}): ReadOnlyProperty<Fragment, VM> =
            object : ReadOnlyProperty<Fragment, VM> {

                private var _value: VM? = null

                override fun getValue(thisRef: Fragment, property: KProperty<*>): VM {
                    if (_value == null) {
                        _value = ViewModels.get(thisRef, VM::class.java, provider, initializer)
                    }
                    return _value!!
                }
            }
}

package it.codingjam.github.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object ViewModels {
    inline fun <reified VM : ViewModel> delegate(crossinline provider: () -> VM): ReadOnlyProperty<Fragment, VM> =
            object : ReadOnlyProperty<Fragment, VM> {

                private var _value: VM? = null

                override fun getValue(thisRef: Fragment, property: KProperty<*>): VM {
                    if (_value == null) {
                        _value = ViewModelProviders.of(thisRef, object : ViewModelProvider.Factory {
                            override fun <T1 : ViewModel> create(aClass: Class<T1>) = provider() as T1
                        }).get(VM::class.java)
                    }
                    return _value!!
                }
            }
}

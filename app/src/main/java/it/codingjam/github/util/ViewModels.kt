package it.codingjam.github.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified VM : ViewModel> fragmentViewModel(crossinline provider: () -> VM): ReadOnlyProperty<Fragment, VM> =
        object : ReadOnlyProperty<Fragment, VM> {

            private var viewModel: VM? = null

            override fun getValue(thisRef: Fragment, property: KProperty<*>): VM {
                if (viewModel == null) {
                    viewModel = ViewModelProviders.of(thisRef, object : ViewModelProvider.Factory {
                        override fun <T1 : ViewModel> create(aClass: Class<T1>) = provider() as T1
                    }).get(VM::class.java)
                }
                return viewModel!!
            }
        }

inline fun <reified VM : ViewModel> activityViewModel(crossinline provider: () -> VM): ReadOnlyProperty<FragmentActivity, VM> =
        object : ReadOnlyProperty<FragmentActivity, VM> {

            private var viewModel: VM? = null

            override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): VM {
                if (viewModel == null) {
                    viewModel = ViewModelProviders.of(thisRef, object : ViewModelProvider.Factory {
                        override fun <T1 : ViewModel> create(aClass: Class<T1>) = provider() as T1
                    }).get(VM::class.java)
                }
                return viewModel!!
            }
        }

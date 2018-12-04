package it.codingjam.github.ui.common

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController

const val FRAGMENT_CREATOR_PARAM = "param"

open class FragmentCreator<T>(
        @IdRes val graphId: Int,
        @IdRes val nodeId: Int
) {
    @Suppress("UNCHECKED_CAST")
    val androidx.fragment.app.Fragment.param: T
        get() = arguments!!.get(FRAGMENT_CREATOR_PARAM) as T

    fun param(fragment: androidx.fragment.app.Fragment): T {
        @Suppress("UNCHECKED_CAST")
        return fragment.arguments!!.get(FRAGMENT_CREATOR_PARAM) as T
    }
}

fun <T : Any> FragmentCreator<T>.args(param: T): Bundle {
    return bundleOf(FRAGMENT_CREATOR_PARAM to param)
}

fun <T : Parcelable> FragmentCreator<T>.navigate(fragment: androidx.fragment.app.Fragment, param: T) {
    fragment.findNavController().navigate(nodeId, Bundle().apply {
        putParcelable(FRAGMENT_CREATOR_PARAM, param)
    })
}

fun FragmentCreator<String>.navigate(fragment: androidx.fragment.app.Fragment, param: String) {
    fragment.findNavController().navigate(nodeId, Bundle().apply {
        putString(FRAGMENT_CREATOR_PARAM, param)
    })
}
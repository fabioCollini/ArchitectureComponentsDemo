package it.codingjam.github.ui.common

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment

const val FRAGMENT_CREATOR_PARAM = "param"

open class FragmentCreator<T>(
        val factory: () -> Fragment
) {
    @Suppress("UNCHECKED_CAST")
    val Fragment.param: T
        get() = arguments!!.get(FRAGMENT_CREATOR_PARAM) as T

    fun param(fragment: Fragment): T {
        @Suppress("UNCHECKED_CAST")
        return fragment.arguments!!.get(FRAGMENT_CREATOR_PARAM) as T
    }
}

fun <T : Parcelable> FragmentCreator<T>.create(param: T): Fragment {
    return factory().apply {
        arguments = Bundle().apply {
            putParcelable(FRAGMENT_CREATOR_PARAM, param)
        }
    }
}

fun FragmentCreator<String>.create(param: String): Fragment {
    return factory().apply {
        arguments = Bundle().apply {
            putString(FRAGMENT_CREATOR_PARAM, param)
        }
    }
}

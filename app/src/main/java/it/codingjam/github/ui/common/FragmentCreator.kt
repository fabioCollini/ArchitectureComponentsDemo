package it.codingjam.github.ui.common

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment

open class FragmentCreator<T : Parcelable>(
        private val factory: () -> Fragment
) {
    fun create(param: T): Fragment =
            factory().apply {
                arguments = Bundle().apply {
                    putParcelable("param", param)
                }
            }

    fun getParam(fragment: Fragment): T =
            fragment.arguments.getParcelable("param")
}
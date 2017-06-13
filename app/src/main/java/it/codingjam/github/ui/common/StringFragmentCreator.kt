package it.codingjam.github.ui.common

import android.os.Bundle
import android.support.v4.app.Fragment

open class StringFragmentCreator(
        private val factory: () -> Fragment
) {
    fun create(param: String): Fragment =
            factory().apply {
                arguments = Bundle().apply {
                    putString("param", param)
                }
            }

    fun getParam(fragment: Fragment): String =
            fragment.arguments.getString("param")
}
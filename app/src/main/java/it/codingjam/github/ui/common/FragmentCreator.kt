package it.codingjam.github.ui.common

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment

open class FragmentCreator<T>(
        val factory: () -> Fragment
)

fun <T : Parcelable> FragmentCreator<T>.create(param: T): Fragment =
        factory().apply {
            arguments = Bundle().apply {
                putParcelable("param", param)
            }
        }

fun <T : Parcelable> FragmentCreator<T>.getParam(fragment: Fragment): T =
        fragment.arguments.getParcelable("param")

fun FragmentCreator<String>.create(param: String): Fragment =
        factory().apply {
            arguments = Bundle().apply {
                putString("param", param)
            }
        }

fun FragmentCreator<String>.getParam(fragment: Fragment): String =
        fragment.arguments.getString("param")

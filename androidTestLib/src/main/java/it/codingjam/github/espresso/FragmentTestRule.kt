package it.codingjam.github.espresso


import android.os.Bundle
import androidx.test.rule.ActivityTestRule
import it.codingjam.github.ui.common.FragmentCreator
import it.codingjam.github.ui.common.args

class FragmentTestRule<T>(
        private val graphId: Int,
        private val nodeId: Int,
        private val bundleCreator: (T) -> Bundle
) : ActivityTestRule<SingleFragmentActivity>(SingleFragmentActivity::class.java, false, false) {

    fun launchFragment(param: T) {
        launchActivity(null)
        activity.setFragment(graphId, nodeId, bundleCreator(param))
    }
}

fun <T : Any> FragmentCreator<T>.rule() =
        FragmentTestRule<T>(graphId, nodeId) { args(it) }

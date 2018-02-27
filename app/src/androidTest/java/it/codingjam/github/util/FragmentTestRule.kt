package it.codingjam.github.util


import android.support.test.rule.ActivityTestRule
import android.support.v4.app.Fragment
import it.codingjam.github.espresso.SingleFragmentActivity

class FragmentTestRule : ActivityTestRule<SingleFragmentActivity>(SingleFragmentActivity::class.java, false, false) {

    fun launchFragment(fragment: Fragment) {
        launchActivity(null)
        activity.setFragment(fragment)
    }
}

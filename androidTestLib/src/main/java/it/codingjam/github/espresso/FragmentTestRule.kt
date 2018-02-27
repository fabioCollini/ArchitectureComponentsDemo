package it.codingjam.github.espresso


import android.support.test.rule.ActivityTestRule
import android.support.v4.app.Fragment

class FragmentTestRule : ActivityTestRule<SingleFragmentActivity>(SingleFragmentActivity::class.java, false, false) {

    fun launchFragment(fragment: Fragment) {
        launchActivity(null)
        activity.setFragment(fragment)
    }
}

package it.codingjam.github

import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.os.bundleOf
import it.codingjam.github.core.RepoId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNavigationController @Inject constructor(): NavigationController {

    override fun navigateToRepo(activity: FragmentActivity, repoId: RepoId) {
        activity.findNavController(R.id.my_nav_host_fragment).navigate(R.id.repo, bundleOf("param" to repoId))
//        val fragment = RepoFragment.create(repoId)
//        val tag = "repo/${repoId.owner}/${repoId.name}"
//        activity.supportFragmentManager.beginTransaction()
//                .replace(R.id.container, fragment, tag)
//                .addToBackStack(null)
//                .commitAllowingStateLoss()
    }

    override fun navigateToUser(activity: FragmentActivity, login: String) {
        activity.findNavController(R.id.my_nav_host_fragment).navigate(R.id.user, bundleOf("param" to login))
//        val tag = "user/$login"
//        val userFragment = UserFragment.create(login)
//        activity.supportFragmentManager.beginTransaction()
//                .replace(R.id.container, userFragment, tag)
//                .addToBackStack(null)
//                .commitAllowingStateLoss()
    }

    override fun showError(activity: FragmentActivity, error: String?) {
        Snackbar.make(activity.findViewById(android.R.id.content), error
                ?: "Error", Snackbar.LENGTH_LONG).show()
    }
}
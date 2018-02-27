package it.codingjam.github

import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import it.codingjam.github.core.RepoId
import it.codingjam.github.ui.common.create
import it.codingjam.github.ui.repo.RepoFragment
import it.codingjam.github.ui.search.SearchFragment
import it.codingjam.github.ui.user.UserFragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNavigationController @Inject constructor(): NavigationController {

    override fun navigateToSearch(activity: FragmentActivity) {
        val searchFragment = SearchFragment()
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, searchFragment)
                .commitAllowingStateLoss()
    }

    override fun navigateToRepo(activity: FragmentActivity, repoId: RepoId) {
        val fragment = RepoFragment.create(repoId)
        val tag = "repo/${repoId.owner}/${repoId.name}"
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    override fun navigateToUser(activity: FragmentActivity, login: String) {
        val tag = "user/$login"
        val userFragment = UserFragment.create(login)
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, userFragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    override fun showError(activity: FragmentActivity, error: String?) {
        Snackbar.make(activity.findViewById(android.R.id.content), error
                ?: "Error", Snackbar.LENGTH_LONG).show()
    }
}
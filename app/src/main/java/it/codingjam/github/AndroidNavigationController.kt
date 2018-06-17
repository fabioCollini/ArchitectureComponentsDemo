package it.codingjam.github

import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import it.codingjam.github.core.RepoId
import it.codingjam.github.ui.common.navigate
import it.codingjam.github.ui.repo.RepoFragment
import it.codingjam.github.ui.user.UserFragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNavigationController @Inject constructor() : NavigationController {

    override fun navigateToRepo(fragment: Fragment, repoId: RepoId) {
        RepoFragment.navigate(fragment, repoId)
    }

    override fun navigateToUser(fragment: Fragment, login: String) {
        UserFragment.navigate(fragment, login)
    }

    override fun showError(activity: FragmentActivity, error: String?) {
        Snackbar.make(activity.findViewById(android.R.id.content), error
                ?: "Error", Snackbar.LENGTH_LONG).show()
    }
}
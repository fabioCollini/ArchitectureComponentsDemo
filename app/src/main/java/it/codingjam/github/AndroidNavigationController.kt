package it.codingjam.github

import com.google.android.material.snackbar.Snackbar
import it.codingjam.github.core.RepoId
import it.codingjam.github.ui.common.navigate
import it.codingjam.github.ui.repo.RepoFragment
import it.codingjam.github.ui.user.UserFragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNavigationController @Inject constructor() : NavigationController {

    override fun navigateToRepo(fragment: androidx.fragment.app.Fragment, repoId: RepoId) {
        RepoFragment.navigate(fragment, repoId)
    }

    override fun navigateToUser(fragment: androidx.fragment.app.Fragment, login: String) {
        UserFragment.navigate(fragment, login)
    }

    override fun showError(activity: androidx.fragment.app.FragmentActivity, error: String?) {
        Snackbar.make(activity.findViewById(android.R.id.content), error
                ?: "Error", Snackbar.LENGTH_LONG).show()
    }
}
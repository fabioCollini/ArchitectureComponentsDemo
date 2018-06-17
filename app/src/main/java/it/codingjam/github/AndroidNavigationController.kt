package it.codingjam.github

import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import it.codingjam.github.core.RepoId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidNavigationController @Inject constructor(): NavigationController {

    override fun navigateToRepo(fragment: Fragment, repoId: RepoId) {
        fragment.findNavController().navigate(R.id.repo, bundleOf("param" to repoId))
    }

    override fun navigateToUser(fragment: Fragment, login: String) {
        fragment.findNavController().navigate(R.id.user, bundleOf("param" to login))
    }

    override fun showError(activity: FragmentActivity, error: String?) {
        Snackbar.make(activity.findViewById(android.R.id.content), error
                ?: "Error", Snackbar.LENGTH_LONG).show()
    }
}
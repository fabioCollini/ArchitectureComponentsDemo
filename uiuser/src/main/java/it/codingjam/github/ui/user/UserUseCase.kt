package it.codingjam.github.ui.user

import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.OpenForTesting
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.NavigationSignal
import it.codingjam.github.vo.lce
import kotlinx.coroutines.experimental.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class UserUseCase @Inject constructor(
        private val githubInteractor: GithubInteractor
) {
    fun CoroutineScope.load(login: String) = lce {
        githubInteractor.loadUserDetail(login)
    }

    fun openRepoDetail(id: RepoId) = NavigationSignal("repo", id)
}
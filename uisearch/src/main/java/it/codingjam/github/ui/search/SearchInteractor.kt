package it.codingjam.github.ui.search

import android.content.SharedPreferences
import com.nalulabs.prefs.string
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.ErrorSignal
import it.codingjam.github.util.NavigationSignal
import it.codingjam.github.util.execute
import it.codingjam.github.vo.Lce
import it.codingjam.github.vo.orElse
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchInteractor @Inject constructor(
        private val githubInteractor: GithubInteractor,
        prefs: SharedPreferences
) {

    var lastSearch by prefs.string("")
        private set

    fun setQuery(originalInput: String, state: SearchViewState) = execute<SearchViewState> {
        lastSearch = originalInput
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        if (!state.repos.map { it.searchInvoked }.orElse(false) || input != state.query) {
            send { state.copy(query = input) }
            sendAll(reloadData(state))
        }
    }

    fun loadNextPage(state: SearchViewState) = execute<SearchViewState> {
        state.repos.doOnData { (_, nextPage, _, loadingMore) ->
            val query = state.query
            if (!query.isEmpty() && nextPage != null && !loadingMore) {
                send { it.copyRepos { copy(loadingMore = true) } }
                try {
                    val (items, newNextPage) = githubInteractor.searchNextPage(query, nextPage)
                    send {
                        it.copyRepos {
                            copy(list = list + items, nextPage = newNextPage, loadingMore = false)
                        }
                    }
                } catch (t: Exception) {
                    send { it.copyRepos { copy(loadingMore = false) } }
                    sendSignal(ErrorSignal(t))
                }
            }
        }
    }

    private fun reloadData(state: SearchViewState) = execute<SearchViewState> {
        send { it.copy(repos = Lce.Loading) }
        try {
            val (items, nextPage) = githubInteractor.search(state.query)
            val success = Lce.Success(ReposViewState(items, nextPage, true))
            send {
                it.copy(repos = success)
            }
        } catch (e: Exception) {
            send { it.copy(repos = Lce.Error(e)) }
        }
    }

    fun refresh(state: SearchViewState) = execute<SearchViewState> {
        if (!state.query.isEmpty()) {
            sendAll(reloadData(state))
        }
    }

    fun openRepoDetail(id: RepoId) = NavigationSignal("repo", id)
}

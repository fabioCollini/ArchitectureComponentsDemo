package it.codingjam.github.ui.search

import android.content.SharedPreferences
import com.nalulabs.prefs.string
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.OpenForTesting
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.*
import it.codingjam.github.vo.lce
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class SearchUseCase @Inject constructor(
        private val githubInteractor: GithubInteractor,
        prefs: SharedPreferences
) {

    private var lastSearch by prefs.string("")

    fun initialState() = SearchViewState(lastSearch)

    fun setQuery(originalInput: String, state: SearchViewState) = produceActions {
        lastSearch = originalInput
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        if (state.repos.data?.searchInvoked != true || input != state.query) {
            sendAll(reloadData(input))
        }
    }

    fun loadNextPage(state: SearchViewState) = produceActions<ReposViewState> {
        state.repos.doOnData { (_, nextPage, _, loadingMore) ->
            val query = state.query
            if (!query.isEmpty() && nextPage != null && !loadingMore) {
                send { copy(loadingMore = true) }
                try {
                    val (items, newNextPage) = githubInteractor.searchNextPage(query, nextPage)
                    send {
                        copy(list = list + items, nextPage = newNextPage, loadingMore = false)
                    }
                } catch (t: Exception) {
                    send { copy(loadingMore = false) }
                    send(ErrorSignal(t))
                }
            }
        }
    }.map<SearchViewState> { action -> copy(repos = repos.map { action(it) }) }

    private suspend fun reloadData(query: String) = lce {
        val (items, nextPage) = githubInteractor.search(query)
        ReposViewState(items, nextPage, true)
    }.map<SearchViewState> { copy(repos = it(repos), query = query) }

    fun refresh(state: SearchViewState) = produceActions {
        if (!state.query.isEmpty()) {
            sendAll(reloadData(state.query))
        }
    }

    fun openRepoDetail(id: RepoId) = NavigationSignal("repo", id)
}
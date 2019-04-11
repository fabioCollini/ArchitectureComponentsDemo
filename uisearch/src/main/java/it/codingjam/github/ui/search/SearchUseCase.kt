package it.codingjam.github.ui.search

import android.content.SharedPreferences
import com.nalulabs.prefs.string
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.OpenForTesting
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.*
import it.codingjam.github.vo.lce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
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

    fun setQuery(originalInput: String, state: SearchViewState): Flow<Action<SearchViewState>> {
        lastSearch = originalInput
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        return if (state.repos.data?.searchInvoked != true || input != state.query) {
            reloadData(input)
        } else
            emptyFlow()
    }

    fun loadNextPage(state: SearchViewState): Flow<Action<SearchViewState>> = flow<Action<ReposViewState>> {
        state.repos.doOnData { (_, nextPage, _, loadingMore) ->
            val query = state.query
            if (!query.isEmpty() && nextPage != null && !loadingMore) {
                emitAction { copy(loadingMore = true) }
                try {
                    val (items, newNextPage) = githubInteractor.searchNextPage(query, nextPage)
                    emitAction {
                        copy(list = list + items, nextPage = newNextPage, loadingMore = false)
                    }
                } catch (t: Exception) {
                    emitAction { copy(loadingMore = false) }
                    emit(ErrorSignal(t))
                }
            }
        }
    }.mapActions { stateAction -> copy(repos = repos.map { stateAction(it) }) }

    private fun reloadData(query: String): Flow<Action<SearchViewState>> = lce {
        val (items, nextPage) = githubInteractor.search(query)
        ReposViewState(items, nextPage, true)
    }.mapActions { stateAction -> copy(repos = stateAction(repos), query = query) }

    fun refresh(state: SearchViewState): Flow<Action<SearchViewState>> {
        return if (!state.query.isEmpty()) {
            reloadData(state.query)
        } else
            emptyFlow()
    }

    fun openRepoDetail(id: RepoId) = NavigationSignal("repo", id)
}
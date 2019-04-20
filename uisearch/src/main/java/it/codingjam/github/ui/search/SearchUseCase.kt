package it.codingjam.github.ui.search

import android.content.SharedPreferences
import com.nalulabs.prefs.string
import it.codingjam.github.FeatureAppScope
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.OpenForTesting
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.*
import it.codingjam.github.vo.lce
import java.util.*
import javax.inject.Inject

@OpenForTesting
@FeatureAppScope
class SearchUseCase @Inject constructor(
        private val githubInteractor: GithubInteractor,
        prefs: SharedPreferences
) {

    private var lastSearch by prefs.string("")

    fun initialState() = SearchViewState(lastSearch)

    fun setQuery(originalInput: String, state: SearchViewState): ActionsFlow<SearchViewState> {
        lastSearch = originalInput
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        return if (state.repos.data?.searchInvoked != true || input != state.query) {
            reloadData(input)
        } else
            emptyActionsFlow()
    }

    fun loadNextPage(state: SearchViewState): ActionsFlow<SearchViewState> = actionsFlow<ReposViewState> {
        state.repos.doOnData { (_, nextPage, _, loadingMore) ->
            val query = state.query
            if (query.isNotEmpty() && nextPage != null && !loadingMore) {
                emit { copy(loadingMore = true) }
                try {
                    val (items, newNextPage) = githubInteractor.searchNextPage(query, nextPage)
                    emit {
                        copy(list = list + items, nextPage = newNextPage, loadingMore = false)
                    }
                } catch (t: Exception) {
                    emit { copy(loadingMore = false) }
                    emit(ErrorSignal(t))
                }
            }
        }
    }.mapActions { stateAction -> copy(repos = repos.map { stateAction(it) }) }

    private fun reloadData(query: String): ActionsFlow<SearchViewState> = lce {
        val (items, nextPage) = githubInteractor.search(query)
        ReposViewState(items, nextPage, true)
    }.mapActions { stateAction -> copy(repos = stateAction(repos), query = query) }

    fun refresh(state: SearchViewState): ActionsFlow<SearchViewState> {
        return if (state.query.isNotEmpty()) {
            reloadData(state.query)
        } else
            emptyActionsFlow()
    }

    fun openRepoDetail(id: RepoId) = NavigationSignal("repo", id)
}
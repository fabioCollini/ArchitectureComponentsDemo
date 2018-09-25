package it.codingjam.github.ui.search

import android.content.SharedPreferences
import com.nalulabs.prefs.string
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.OpenForTesting
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.*
import it.codingjam.github.vo.Lce
import kotlinx.coroutines.experimental.channels.*
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
            reloadData(input)
        }
    }

    fun loadNextPage(state: SearchViewState) = produceActions<SearchViewState> {
        state.repos.doOnData { (_, nextPage, _, loadingMore) ->
            val query = state.query
            if (!query.isEmpty() && nextPage != null && !loadingMore) {
                send { copyRepos { copy(loadingMore = true) } }
                try {
                    val (items, newNextPage) = githubInteractor.searchNextPage(query, nextPage)
                    send {
                        copyRepos {
                            copy(list = list + items, nextPage = newNextPage, loadingMore = false)
                        }
                    }
                } catch (t: Exception) {
                    send { copyRepos { copy(loadingMore = false) } }
                    send(ErrorSignal(t))
                }
            }
        }
    }

    fun loadNextPage2(state: SearchViewState): ReceiveChannel<Action<SearchViewState>> {
        return produceActions<ReposViewState> {
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
        }.map { action -> action.map<ReposViewState, SearchViewState> { copy(repos = Lce.Success(it(repos.data!!))) } }
    }

    private suspend fun ProducerScope<Action<SearchViewState>>.reloadData(query: String) {
//        exec({ lce -> copy(repos = lce, query = query) }) {
//            val (items, nextPage) = githubInteractor.search(query)
//            ReposViewState(items, nextPage, true)
//        }
//        sub<SearchViewState, Lce<ReposViewState>>({ copy(repos = it(repos)) }) {
//            exec2 {
//                val (items, nextPage) = githubInteractor.search(query)
//                ReposViewState(items, nextPage, true)
//            }
//        }
//        produceActions<Lce<ReposViewState>> {
//            exec2 {
//                val (items, nextPage) = githubInteractor.search(query)
//                ReposViewState(items, nextPage, true)
//            }
//        }.subb(this) { copy(repos = it(repos)) }

        lce {
            val (items, nextPage) = githubInteractor.search(query)
            ReposViewState(items, nextPage, true)
        }.map(this) { copy(repos = it(repos), query = query) }
    }

    fun refresh(state: SearchViewState) = produceActions {
        if (!state.query.isEmpty()) {
            reloadData(state.query)
        }
    }

    fun openRepoDetail(id: RepoId) = NavigationSignal("repo", id)
}

suspend inline fun <S, R> ProducerScope<Action<S>>.exec(crossinline copy: S.(Lce<R>) -> S, f: () -> R) {
    send { copy(this, Lce.Loading) }
    try {
        val result = f()
        send { copy(this, Lce.Success(result)) }
    } catch (e: Exception) {
        send { copy(this, Lce.Error(e)) }
    }
}

suspend inline fun <S> ProducerScope<Action<Lce<S>>>.exec2(f: () -> S) {
    send { Lce.Loading }
    try {
        val result = f()
        send { Lce.Success(result) }
    } catch (e: Exception) {
        send { Lce.Error(e) }
    }
}

inline fun <S> lce(crossinline f: suspend () -> S): ReceiveChannel<Action<Lce<S>>> {
    return produceActions {
        send { Lce.Loading }
        try {
            val result = f()
            send { Lce.Success(result) }
        } catch (e: Exception) {
            send { Lce.Error(e) }
        }
    }
}

fun <S, R> ProducerScope<Action<S>>.sub(copy: S.(StateAction<R>) -> S, f: suspend ProducerScope<Action<R>>.() -> Unit) {
    produce(block = f).map { originalAction ->
        send(if (originalAction is Signal) {
            originalAction
        } else {
            val stateAction = originalAction as StateAction<R>
            StateAction { copy(stateAction) }
        })
    }
}

suspend fun <S, R> ReceiveChannel<Action<R>>.map(scope: ProducerScope<Action<S>>, copy: S.(StateAction<R>) -> S) {
    consumeEach { originalAction ->
        scope.send(originalAction.map(copy))
    }
//    map { originalAction ->
//        scope.send(if (originalAction is Signal) {
//            originalAction
//        } else {
//            val stateAction = originalAction as StateAction<R>
//            StateAction { copy(stateAction) }
//        })
//    }
}

fun <R, S> Action<R>.map(copy: S.(StateAction<R>) -> S): Action<S> {
    return if (this is Signal) {
        this
    } else {
        val stateAction = this as StateAction<R>
        StateAction { copy(stateAction) }
    }
}
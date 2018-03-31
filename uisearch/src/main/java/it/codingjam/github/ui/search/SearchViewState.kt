package it.codingjam.github.ui.search

import it.codingjam.github.core.Repo
import it.codingjam.github.core.utils.deepCopy
import it.codingjam.github.vo.Lce
import it.codingjam.github.vo.orElse

data class ReposViewState(
        val list: List<Repo>,
        val nextPage: Int? = null,
        val searchInvoked: Boolean = false,
        val loadingMore: Boolean = false
) {
    val emptyStateVisible: Boolean = searchInvoked && list.isEmpty()
}

data class SearchViewState(
        val query: String = "",
        val repos: Lce<ReposViewState> = Lce.Success(ReposViewState(emptyList()))
) {

    val reposState = repos.orElse(null)

    inline fun copyRepos(c: ReposViewState.() -> ReposViewState): SearchViewState {
        return deepCopy(
                { (repos as Lce.Success).data }, { copy(repos = Lce.Success(it)) },
                { this.c() }
        )
    }
}

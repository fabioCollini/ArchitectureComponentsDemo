package it.codingjam.github.ui.search

import it.codingjam.github.core.Repo
import it.codingjam.github.vo.Lce
import it.codingjam.github.vo.orElse

data class PaginatedList<T>(
        val list: List<T>,
        val nextPage: Int? = null
)

data class SearchViewState(
        val query: String = "",
        val searchInvoked: Boolean = false,
        val repos: Lce<PaginatedList<Repo>> = Lce.Success(PaginatedList(emptyList())),
        val loadingMore: Boolean = false) {

    fun emptyStateVisible(): Boolean {
        return searchInvoked && repos is Lce.Success && repos.data.list.isEmpty()
    }

    inline val repoList get() = repos.map { it.list }.orElse(emptyList())
}

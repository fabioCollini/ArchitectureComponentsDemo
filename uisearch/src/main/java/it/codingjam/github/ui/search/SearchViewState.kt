package it.codingjam.github.ui.search

import it.codingjam.github.core.Repo
import it.codingjam.github.vo.Lce

data class SearchViewState(
        val query: String = "",
        val searchInvoked: Boolean = false,
        val repos: Lce<List<Repo>> = Lce.Success(emptyList()),
        val loadingMore: Boolean = false,
        val nextPage: Int? = null) {

    fun emptyStateVisible(): Boolean {
        return searchInvoked && repos is Lce.Success && repos.data.isEmpty()
    }
}

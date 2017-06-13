package it.codingjam.github.ui.search

import it.codingjam.github.vo.Repo
import it.codingjam.github.vo.Resource

data class SearchViewState(
        val query: String = "",
        val repos: Resource<List<Repo>> = Resource.Empty,
        val loadingMore: Boolean = false,
        val nextPage: Int? = null) {

    fun emptyStateVisible(): Boolean {
        return repos is Resource.Success && repos.data.isEmpty()
    }
}

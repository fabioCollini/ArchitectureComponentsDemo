package it.codingjam.github.core

interface GithubInteractor {

    suspend fun search(query: String): RepoSearchResponse

    suspend fun searchNextPage(query: String, nextPage: Int): RepoSearchResponse

    suspend fun loadRepo(owner: String, name: String): RepoDetail

    suspend fun loadUserDetail(login: String): UserDetail
}

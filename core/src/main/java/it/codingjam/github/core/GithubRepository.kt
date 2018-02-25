package it.codingjam.github.core

interface GithubRepository {
    suspend fun loadRepos(owner: String): List<Repo>
    suspend fun loadRepo(owner: String, name: String): RepoDetail

    suspend fun searchNextPage(query: String, nextPage: Int): RepoSearchResponse
    suspend fun search(query: String): RepoSearchResponse

    suspend fun loadUser(login: String): User
}

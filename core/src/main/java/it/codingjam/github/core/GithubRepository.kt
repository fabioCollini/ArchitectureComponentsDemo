package it.codingjam.github.core

interface GithubRepository {
    suspend fun loadRepos(owner: String): List<Repo>

    suspend fun searchNextPage(query: String, nextPage: Int): RepoSearchResponse
    suspend fun search(query: String): RepoSearchResponse

    suspend fun loadUser(login: String): User

    suspend fun getRepo(owner: String, name: String): Repo
    suspend fun getContributors(owner: String, name: String): List<Contributor>
}

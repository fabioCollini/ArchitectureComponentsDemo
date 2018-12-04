package it.codingjam.github.core

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class GithubInteractor @Inject constructor(private val githubRepository: GithubRepository) {

    suspend fun search(query: String) = githubRepository.search(query)

    suspend fun searchNextPage(query: String, nextPage: Int) = githubRepository.searchNextPage(query, nextPage)

    suspend fun loadRepo(owner: String, name: String): RepoDetail = coroutineScope {
        val repo = async { githubRepository.getRepo(owner, name) }
        val contributors = async { githubRepository.getContributors(owner, name) }

        RepoDetail(repo.await(), contributors.await())
    }

    suspend fun loadUserDetail(login: String): UserDetail = coroutineScope {
        val userDeferred = async { githubRepository.loadUser(login) }
        val reposDeferred = async { githubRepository.loadRepos(login) }
        UserDetail(userDeferred.await(), reposDeferred.await())
    }
}
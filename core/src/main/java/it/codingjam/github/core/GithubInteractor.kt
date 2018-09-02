package it.codingjam.github.core

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class GithubInteractor @Inject constructor(private val githubRepository: GithubRepository) {

    suspend fun search(query: String) = githubRepository.search(query)

    suspend fun searchNextPage(query: String, nextPage: Int) = githubRepository.searchNextPage(query, nextPage)

    suspend fun loadRepo(owner: String, name: String): RepoDetail {
        val repo = async(CommonPool) { githubRepository.getRepo(owner, name) }
        val contributors = async(CommonPool) { githubRepository.getContributors(owner, name) }

        return RepoDetail(repo.await(), contributors.await())
    }

    suspend fun loadUserDetail(login: String): UserDetail {
        val userDeferred = async(CommonPool) { githubRepository.loadUser(login) }
        val reposDeferred = async(CommonPool) { githubRepository.loadRepos(login) }
        return UserDetail(userDeferred.await(), reposDeferred.await())
    }
}
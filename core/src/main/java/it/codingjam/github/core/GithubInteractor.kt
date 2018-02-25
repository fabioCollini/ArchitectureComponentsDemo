package it.codingjam.github.core

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

class GithubInteractor(private val githubRepository: GithubRepository) {

    suspend fun search(query: String) = githubRepository.search(query)

    suspend fun searchNextPage(query: String, nextPage: Int) = githubRepository.searchNextPage(query, nextPage)

    suspend fun loadRepo(owner: String, name: String) = githubRepository.loadRepo(owner, name)

    suspend fun loadUserDetail(login: String): UserDetail {
        val userDeferred = async(CommonPool) { githubRepository.loadUser(login) }
        val reposDeferred = async(CommonPool) { githubRepository.loadRepos(login) }
        return UserDetail(userDeferred.await(), reposDeferred.await())
    }
}
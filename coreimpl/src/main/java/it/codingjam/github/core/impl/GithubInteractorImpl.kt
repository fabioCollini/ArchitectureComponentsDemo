package it.codingjam.github.core.impl

import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoDetail
import it.codingjam.github.core.UserDetail
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubInteractorImpl @Inject constructor(private val githubRepository: GithubRepository): GithubInteractor {

    override suspend fun search(query: String) = githubRepository.search(query)

    override suspend fun searchNextPage(query: String, nextPage: Int) = githubRepository.searchNextPage(query, nextPage)

    override suspend fun loadRepo(owner: String, name: String): RepoDetail {
        val repo = async(CommonPool) { githubRepository.getRepo(owner, name) }
        val contributors = async(CommonPool) { githubRepository.getContributors(owner, name) }

        return RepoDetail(repo.await(), contributors.await())
    }

    override suspend fun loadUserDetail(login: String): UserDetail {
        val userDeferred = async(CommonPool) { githubRepository.loadUser(login) }
        val reposDeferred = async(CommonPool) { githubRepository.loadRepos(login) }
        return UserDetail(userDeferred.await(), reposDeferred.await())
    }
}
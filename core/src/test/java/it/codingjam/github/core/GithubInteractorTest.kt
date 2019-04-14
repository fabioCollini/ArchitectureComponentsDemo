package it.codingjam.github.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GithubInteractorTest {

    val githubRepository: GithubRepository = mock()

    val githubInteractor = GithubInteractor(githubRepository)

    @Test fun load() = runBlocking {
        whenever(githubRepository.loadUser(LOGIN)).thenReturn(USER)
        whenever(githubRepository.loadRepos(LOGIN)).thenReturn(listOf(REPO_1, REPO_2))

        val detail = githubInteractor.loadUserDetail(LOGIN)
        assertThat(detail).isEqualTo(UserDetail(USER, listOf(REPO_1, REPO_2)))
    }

    companion object {
        private const val LOGIN = "login"
        private val OWNER = Owner("login", "url")
        private val USER = User("login", "avatar", "name", "company", "repos", "blog")
        private val REPO_1 = Repo(1, "name", "fullName", "desc", OWNER, 10000)
        private val REPO_2 = Repo(2, "name", "fullName", "desc", OWNER, 10000)
    }
}
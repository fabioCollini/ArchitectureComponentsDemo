package it.codingjam.github.ui.search


import assertk.assert
import assertk.assertions.*
import com.nalulabs.prefs.fake.FakeSharedPreferences
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.Repo
import it.codingjam.github.core.RepoSearchResponse
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.testdata.TestData.REPO_3
import it.codingjam.github.testdata.TestData.REPO_4
import it.codingjam.github.util.ErrorSignal
import it.codingjam.github.util.signals
import it.codingjam.github.util.states
import it.codingjam.github.vo.debug
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SearchUseCaseTest {
    val interactor: GithubInteractor = mock()
    val useCase = SearchUseCase(interactor, FakeSharedPreferences())

    @Test
    fun load() = runBlocking {
        whenever(interactor.search(QUERY)).thenReturn(RepoSearchResponse(listOf(REPO_1, REPO_2), 2))

        val states = states(SearchViewState()) { useCase.run { setQuery(QUERY, it) } }.map { it.repos }

        assert(states).hasSize(2)

        assert(states.map { it.debug }).containsExactly("L", "S")

        assert(states.map { it.data?.emptyStateVisible ?: false })
                .containsExactly(false, false)

        assert(states.last().data?.list).isNotNull {
            it.containsExactly(REPO_1, REPO_2)
        }
    }

    @Test
    fun emptyStateVisible() = runBlocking {
        whenever(interactor.search(QUERY)).thenReturn(RepoSearchResponse(emptyList(), null))

        val states = states(SearchViewState()) { useCase.run { setQuery(QUERY, it) } }.map { it.repos }

        assert(states.map { it.debug }).containsExactly("L", "S")

        assert(states.map { it.data?.emptyStateVisible ?: false })
                .containsExactly(false, true)

        assert(states.last().data?.list).isNotNull {
            it.isEmpty()
        }
    }

    private fun response(repo1: Repo, repo2: Repo, nextPage: Int): RepoSearchResponse {
        return RepoSearchResponse(listOf(repo1, repo2), nextPage)
    }

    @Test
    fun loadMore() = runBlocking {
        whenever(interactor.search(QUERY)).thenReturn(response(REPO_1, REPO_2, 2))
        whenever(interactor.searchNextPage(QUERY, 2)).thenReturn(response(REPO_3, REPO_4, 3))

        val lastState = states(SearchViewState()) { useCase.run { setQuery(QUERY, it) } }.last()

        val states = states(lastState) { useCase.loadNextPage(it) }

        assert(states.map { it.repos.debug }).containsExactly("S", "S")

        assert(states.map { it.repos.data?.loadingMore ?: false })
                .containsExactly(true, false)

        assert(states.last().repos.data!!.list)
                .isEqualTo(listOf(REPO_1, REPO_2, REPO_3, REPO_4))
    }

    @Test
    fun errorLoadingMore() = runBlocking {
        whenever(interactor.search(QUERY)).thenReturn(response(REPO_1, REPO_2, 2))
        whenever(interactor.searchNextPage(QUERY, 2)).thenThrow(RuntimeException(ERROR))

        val lastState = states(SearchViewState()) { useCase.run { setQuery(QUERY, it) } }.last()

        val states = states(lastState) { useCase.run { loadNextPage(it) } }

        assert(states.map { it.repos.debug }).containsExactly("S", "S")

        assert(states.map { it.repos.data?.loadingMore ?: false })
                .containsExactly(true, false)

        assert(states.last().repos.data?.list).isNotNull {
            it.containsExactly(REPO_1, REPO_2)
        }

        val signals = signals(lastState) { useCase.loadNextPage(it) }

        assert(signals.last()).isInstanceOf(ErrorSignal::class) {
            it.prop(ErrorSignal::message).isEqualTo(ERROR)
        }
    }

    companion object {
        private const val QUERY = "query"
        private const val ERROR = "error"
    }
}
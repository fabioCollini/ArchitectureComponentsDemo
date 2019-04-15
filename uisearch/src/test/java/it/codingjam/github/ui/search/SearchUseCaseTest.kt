package it.codingjam.github.ui.search


import assertk.all
import assertk.assertThat
import assertk.assertions.*
import com.nalulabs.prefs.fake.FakeSharedPreferences
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.Repo
import it.codingjam.github.core.RepoSearchResponse
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.testdata.TestData.REPO_3
import it.codingjam.github.testdata.TestData.REPO_4
import it.codingjam.github.testdata.containsLce
import it.codingjam.github.testdata.map
import it.codingjam.github.util.ErrorSignal
import it.codingjam.github.util.Signal
import it.codingjam.github.util.states
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SearchUseCaseTest {
    private val interactor: GithubInteractor = mock()
    private val useCase = SearchUseCase(interactor, FakeSharedPreferences())

    @Test
    fun load() = runBlocking {
        whenever(interactor.search(QUERY)) doReturn RepoSearchResponse(listOf(REPO_1, REPO_2), 2)

        val initialState = SearchViewState()
        val states = useCase.setQuery(QUERY, initialState)
                .states(initialState)
                .filterIsInstance<SearchViewState>().map { it.repos }

        assertThat(states).containsLce("LS")

        assertThat(states.map { it.data?.emptyStateVisible ?: false })
                .containsExactly(false, false)

        assertThat(states.last().data?.list)
                .isNotNull()
                .containsExactly(REPO_1, REPO_2)
    }

    @Test
    fun emptyStateVisible() = runBlocking {
        whenever(interactor.search(QUERY)) doReturn RepoSearchResponse(emptyList(), null)

        val initialState = SearchViewState()
        val states = useCase.setQuery(QUERY, initialState)
                .states(initialState)
                .filterIsInstance<SearchViewState>().map { it.repos }

        assertThat(states).all {
            containsLce("LS")

            map { it.data }.all {
                map { it?.emptyStateVisible ?: false }
                        .containsExactly(false, true)

                transform { it.last()?.list }
                        .isNotNull().isEmpty()
            }
        }
    }

    private fun response(repo1: Repo, repo2: Repo, nextPage: Int): RepoSearchResponse {
        return RepoSearchResponse(listOf(repo1, repo2), nextPage)
    }

    @Test
    fun loadMore() = runBlocking {
        whenever(interactor.search(QUERY)) doReturn response(REPO_1, REPO_2, 2)
        whenever(interactor.searchNextPage(QUERY, 2)) doReturn response(REPO_3, REPO_4, 3)

        val initialState = SearchViewState()
        val lastState = useCase.setQuery(QUERY, initialState)
                .states(initialState)
                .filterIsInstance<SearchViewState>().last()

        val states = useCase.loadNextPage(lastState)
                .states(lastState)
                .filterIsInstance<SearchViewState>()

        assertThat(states.map { it.repos }).all {
            containsLce("SS")

            map { it.data?.loadingMore ?: false }
                    .containsExactly(true, false)

            transform { it.last().data!!.list }
                    .isEqualTo(listOf(REPO_1, REPO_2, REPO_3, REPO_4))
        }

    }

    @Test
    fun errorLoadingMore() = runBlocking {
        whenever(interactor.search(QUERY)) doReturn response(REPO_1, REPO_2, 2)
        whenever(interactor.searchNextPage(QUERY, 2)).thenThrow(RuntimeException(ERROR))

        val initialState = SearchViewState()
        val lastState = useCase.setQuery(QUERY, initialState)
                .states(initialState)
                .filterIsInstance<SearchViewState>().last()

        val states = useCase.loadNextPage(lastState)
                .states(lastState)
                .filterIsInstance<SearchViewState>()

        assertThat(states.map { it.repos }).containsLce("SS")

        assertThat(states.map { it.repos.data?.loadingMore ?: false })
                .containsExactly(true, false)

        assertThat(states.last().repos.data?.list).isNotNull().containsExactly(REPO_1, REPO_2)

        val signals = useCase.loadNextPage(lastState)
                .states(lastState)
                .filterIsInstance<Signal>()

        assertThat(signals.last())
                .isInstanceOf(ErrorSignal::class)
                .prop(ErrorSignal::message)
                .isEqualTo(ERROR)
    }

    companion object {
        private const val QUERY = "query"
        private const val ERROR = "error"
    }
}
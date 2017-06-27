package it.codingjam.github.repository

import com.nhaarman.mockito_kotlin.mock
import it.codingjam.github.api.GithubService
import it.codingjam.github.util.TestData.REPO_1
import it.codingjam.github.util.TestData.REPO_2
import it.codingjam.github.util.willReturnJust
import okhttp3.Headers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnit
import retrofit2.Response

class RepoRepositoryTest {

    @get:Rule var mockitoRule = MockitoJUnit.rule()

    val githubService: GithubService = mock()

    @InjectMocks lateinit var repository: RepoRepository

    @Test fun search() {
        val header = "<https://api.github.com/search/repositories?q=foo&page=2>; rel=\"next\"," +
                " <https://api.github.com/search/repositories?q=foo&page=34>; rel=\"last\""
        val headers = mapOf("link" to header)

        githubService.searchRepos(QUERY) willReturnJust
                Response.success(listOf(REPO_1, REPO_2), Headers.of(headers))

        val (items, nextPage) = repository.search(QUERY).blockingGet()

        assertThat(items).containsExactly(REPO_1, REPO_2)
        assertThat(nextPage).isEqualTo(2)
    }

    companion object {
        private val QUERY = "abc"
    }
}
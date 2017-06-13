package it.codingjam.github.repository

import io.reactivex.Single
import it.codingjam.github.api.GithubService
import it.codingjam.github.util.TestData.Companion.REPO_1
import it.codingjam.github.util.TestData.Companion.REPO_2
import okhttp3.Headers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import retrofit2.Response
import java.util.*

class RepoRepositoryTest {

    @get:Rule var mockitoRule = MockitoJUnit.rule()

    @Mock lateinit var githubService: GithubService

    @InjectMocks lateinit var repository: RepoRepository

    @Test fun search() {
        val header = "<https://api.github.com/search/repositories?q=foo&page=2>; rel=\"next\"," +
                " <https://api.github.com/search/repositories?q=foo&page=34>; rel=\"last\""
        val headers = mapOf("link" to header)

        given(githubService.searchRepos(QUERY)).willReturn(
                Single.just(Response.success(Arrays.asList(REPO_1, REPO_2), Headers.of(headers))))

        val (items, nextPage) = repository.search(QUERY).blockingGet()

        assertThat(items).containsExactly(REPO_1, REPO_2)
        assertThat(nextPage).isEqualTo(2)
    }

    companion object {
        private val QUERY = "abc"
    }
}
/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.codingjam.github.ui.search


import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.v4.app.FragmentActivity
import io.reactivex.Single
import it.codingjam.github.NavigationController
import it.codingjam.github.api.RepoSearchResponse
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.util.ResourceTester
import it.codingjam.github.util.TestData.Companion.REPO_1
import it.codingjam.github.util.TestData.Companion.REPO_2
import it.codingjam.github.util.TestData.Companion.REPO_3
import it.codingjam.github.util.TestData.Companion.REPO_4
import it.codingjam.github.util.TestLiveDataObserver
import it.codingjam.github.util.TrampolineSchedulerRule
import it.codingjam.github.vo.Repo
import it.codingjam.github.vo.Resource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import java.io.IOException

class SearchViewModelTest {
    @get:Rule var mockitoRule = MockitoJUnit.rule()

    @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    @Mock lateinit var repository: RepoRepository
    @Mock lateinit var navigationController: NavigationController
    @Mock lateinit var activity: FragmentActivity
    @InjectMocks lateinit var viewModel: SearchViewModel

    private val observer = TestLiveDataObserver<SearchViewState>()

    @Before fun setUp() {
        viewModel.observeForever(activity, { observer.onChanged(it) })
    }

    @Test fun load() {
        given(repository.search(QUERY))
                .willReturn(response(REPO_1, REPO_2, 2))

        viewModel.setQuery(QUERY)

        ResourceTester(observer.getValues().map { it.repos })
                .empty().loading().success()

        assertThat((observer.getValues()[2].repos as Resource.Success).data)
                .containsExactly(REPO_1, REPO_2)
    }

    private fun response(repo1: Repo, repo2: Repo, nextPage: Int): Single<RepoSearchResponse> {
        return Single.just(RepoSearchResponse(listOf(repo1, repo2), nextPage))
    }

    @Test fun loadMore() {
        given(repository.search(QUERY))
                .willReturn(response(REPO_1, REPO_2, 2))

        given(repository.searchNextPage(QUERY, 2))
                .willReturn(response(REPO_3, REPO_4, 3))

        viewModel.setQuery(QUERY)
        viewModel.loadNextPage()

        ResourceTester(observer.getValues().map { it.repos })
                .empty().loading().success().success().success()

        assertThat(observer.getValues()).
                extracting { it.loadingMore }
                .containsExactly(false, false, false, true, false)

        assertThat((observer.getValues()[4].repos as Resource.Success).data)
                .isEqualTo(listOf(REPO_1, REPO_2, REPO_3, REPO_4))
    }

    @Test fun errorLoadingMore() {
        given(repository.search(QUERY))
                .willReturn(response(REPO_1, REPO_2, 2))

        given(repository.searchNextPage(QUERY, 2))
                .willReturn(Single.error(IOException(ERROR)))

        viewModel.setQuery(QUERY)
        viewModel.loadNextPage()

        ResourceTester(observer.getValues().map { it.repos })
                .empty().loading().success().success().success()

        assertThat(observer.getValues())
                .extracting { it.loadingMore }
                .containsExactly(false, false, false, true, false)

        assertThat((observer.getValues()[2].repos as Resource.Success).data)
                .containsExactly(REPO_1, REPO_2)

        verify(navigationController).showError(activity, ERROR)
    }

    companion object {
        private val QUERY = "query"
        private val ERROR = "error"
    }
}
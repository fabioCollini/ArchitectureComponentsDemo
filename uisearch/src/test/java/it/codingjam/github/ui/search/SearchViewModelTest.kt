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
import assertk.assert
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.nalulabs.prefs.fake.FakeSharedPreferences
import com.nhaarman.mockito_kotlin.mock
import it.codingjam.github.NavigationController
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.Repo
import it.codingjam.github.core.RepoSearchResponse
import it.codingjam.github.test.willReturn
import it.codingjam.github.test.willThrow
import it.codingjam.github.testdata.ResourceTester
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.testdata.TestData.REPO_3
import it.codingjam.github.testdata.TestData.REPO_4
import it.codingjam.github.util.TestCoroutines
import it.codingjam.github.vo.Lce
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify

class SearchViewModelTest {
    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    val interactor: GithubInteractor = mock()
    val navigationController: NavigationController = mock()
    val activity: FragmentActivity = mock()
    val viewModel by lazy { SearchViewModel(interactor, navigationController, TestCoroutines(), FakeSharedPreferences()) }

    val states = mutableListOf<SearchViewState>()

    @Before fun setUp() {
        viewModel.liveData.observeForever { states.add(it) }
        viewModel.uiActions.observeForever { it(activity) }
    }

    @Test fun load() = runBlocking {
        interactor.search(QUERY) willReturn RepoSearchResponse(listOf(REPO_1, REPO_2), 2)

        viewModel.setQuery(QUERY)

        ResourceTester(states.map { it.repos })
                .success().loading().success()

        assert(states.map { it.emptyStateVisible })
                .containsExactly(false, false, false)

        assert((states[2].repos as Lce.Success).data.list)
                .containsExactly(REPO_1, REPO_2)
    }

    @Test fun emptyStateVisible() = runBlocking {
        interactor.search(QUERY) willReturn RepoSearchResponse(emptyList(), null)

        viewModel.setQuery(QUERY)

        ResourceTester(states.map { it.repos })
                .success().loading().success()

        assert(states.map { it.emptyStateVisible })
                .containsExactly(false, false, true)

        assert((states[2].repos as Lce.Success).data.list).isEmpty()
    }

    private fun response(repo1: Repo, repo2: Repo, nextPage: Int): RepoSearchResponse {
        return RepoSearchResponse(listOf(repo1, repo2), nextPage)
    }

    @Test fun loadMore() = runBlocking {
        interactor.search(QUERY) willReturn response(REPO_1, REPO_2, 2)
        interactor.searchNextPage(QUERY, 2) willReturn response(REPO_3, REPO_4, 3)

        viewModel.setQuery(QUERY)
        viewModel.loadNextPage()

        ResourceTester(states.map { it.repos })
                .success().loading().success().success().success()

        assert(states.map { it.loadingMore })
                .containsExactly(false, false, false, true, false)

        assert((states[4].repos as Lce.Success).data.list)
                .isEqualTo(listOf(REPO_1, REPO_2, REPO_3, REPO_4))
    }

    @Test fun errorLoadingMore() = runBlocking {
        interactor.search(QUERY) willReturn response(REPO_1, REPO_2, 2)
        interactor.searchNextPage(QUERY, 2) willThrow RuntimeException(ERROR)

        viewModel.setQuery(QUERY)
        viewModel.loadNextPage()

        ResourceTester(states.map { it.repos })
                .success().loading().success().success().success()

        assert(states.map { it.loadingMore })
                .containsExactly(false, false, false, true, false)

        assert((states[2].repos as Lce.Success).data.list)
                .containsExactly(REPO_1, REPO_2)

        verify(navigationController).showError(activity, ERROR)
    }

    companion object {
        private const val QUERY = "query"
        private const val ERROR = "error"
    }
}
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
import android.support.v4.app.Fragment
import assertk.assert
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.nalulabs.prefs.fake.FakeSharedPreferences
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import it.codingjam.github.NavigationController
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.Repo
import it.codingjam.github.core.RepoSearchResponse
import it.codingjam.github.testdata.ResourceTester
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.testdata.TestData.REPO_3
import it.codingjam.github.testdata.TestData.REPO_4
import it.codingjam.github.util.TestCoroutines
import it.codingjam.github.vo.Lce
import it.codingjam.github.vo.orElse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {
    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    val interactor: GithubInteractor = mockk()
    val navigationController: NavigationController = mockk(relaxed = true)
    val fragment: Fragment = mockk()
    val viewModel by lazy { SearchViewModel(interactor, navigationController, TestCoroutines(), FakeSharedPreferences()) }

    val states = mutableListOf<SearchViewState>()

    @Before fun setUp() {
        viewModel.state.observeForever { states.add(it) }
        viewModel.uiActions.observeForever { it(fragment) }
    }

    @Test fun load() {
        coEvery { interactor.search(QUERY) } returns RepoSearchResponse(listOf(REPO_1, REPO_2), 2)

        viewModel.setQuery(QUERY)

        ResourceTester(states.map { it.repos })
                .success().success().loading().success()

        assert(states.map { it.repos.map { it.emptyStateVisible }.orElse(false) })
                .containsExactly(false, false, false, false)

        assert((states[3].repos as Lce.Success).data.list)
                .containsExactly(REPO_1, REPO_2)
    }

    @Test fun emptyStateVisible() {
        coEvery { interactor.search(QUERY) } returns RepoSearchResponse(emptyList(), null)

        viewModel.setQuery(QUERY)

        ResourceTester(states.map { it.repos })
                .success().success().loading().success()

        assert(states.map { it.repos.map { it.emptyStateVisible }.orElse(false) })
                .containsExactly(false, false, false, true)

        assert((states[3].repos as Lce.Success).data.list).isEmpty()
    }

    private fun response(repo1: Repo, repo2: Repo, nextPage: Int): RepoSearchResponse {
        return RepoSearchResponse(listOf(repo1, repo2), nextPage)
    }

    @Test fun loadMore() {
        coEvery { interactor.search(QUERY) } returns response(REPO_1, REPO_2, 2)
        coEvery { interactor.searchNextPage(QUERY, 2) } returns response(REPO_3, REPO_4, 3)

        viewModel.setQuery(QUERY)
        viewModel.loadNextPage()

        ResourceTester(states.map { it.repos })
                .success().success().loading().success().success().success()

        assert(states.map { it.repos.map { it.loadingMore }.orElse(false) })
                .containsExactly(false, false, false, false, true, false)

        assert((states[5].repos as Lce.Success).data.list)
                .isEqualTo(listOf(REPO_1, REPO_2, REPO_3, REPO_4))
    }

    @Test fun errorLoadingMore() {
        coEvery { fragment.requireActivity() } returns mockk()
        coEvery { interactor.search(QUERY) } returns response(REPO_1, REPO_2, 2)
        coEvery { interactor.searchNextPage(QUERY, 2) } throws RuntimeException(ERROR)

        viewModel.setQuery(QUERY)
        viewModel.loadNextPage()

        ResourceTester(states.map { it.repos })
                .success().success().loading().success().success().success()

        assert(states.map { it.repos.map { it.loadingMore }.orElse(false) })
                .containsExactly(false, false, false, false, true, false)

        assert((states[3].repos as Lce.Success).data.list)
                .containsExactly(REPO_1, REPO_2)

        verify { navigationController.showError(any(), eq(ERROR)) }
    }

    companion object {
        private const val QUERY = "query"
        private const val ERROR = "error"
    }
}
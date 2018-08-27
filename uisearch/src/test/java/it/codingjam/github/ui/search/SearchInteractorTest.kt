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


import assertk.assert
import assertk.assertions.containsExactly
import com.nalulabs.prefs.fake.FakeSharedPreferences
import com.nhaarman.mockito_kotlin.mock
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoSearchResponse
import it.codingjam.github.test.willReturn
import it.codingjam.github.testdata.ResourceTester
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.util.StateAction
import it.codingjam.github.util.UiSignal
import it.codingjam.github.vo.Lce
import it.codingjam.github.vo.orElse
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SearchInteractorTest {
    val interactor: GithubInteractor = mock()
    val searchInteractor = SearchInteractor(interactor, FakeSharedPreferences())

    @Test
    fun load() = runBlocking {
        interactor.search(QUERY) willReturn RepoSearchResponse(listOf(REPO_1, REPO_2), 2)

        val states = searchInteractor.setQuery(QUERY, SearchViewState()).toList()
                .map { (it as Pair<StateAction<SearchViewState>, UiSignal>).first(SearchViewState()) }

        ResourceTester(states.map { it.repos })
                .success().success().loading().success()

        assert(states.map { it.repos.map { it.emptyStateVisible }.orElse(false) })
                .containsExactly(false, false, false, false)

        assert((states[3].repos as Lce.Success).data.list)
                .containsExactly(REPO_1, REPO_2)
    }

//    @Test fun emptyStateVisible() = runBlocking {
//        interactor.search(QUERY) willReturn RepoSearchResponse(emptyList(), null)
//
//        viewModel.setQuery(QUERY)
//
//        ResourceTester(states.map { it.repos })
//                .success().success().loading().success()
//
//        assert(states.map { it.repos.map { it.emptyStateVisible }.orElse(false) })
//                .containsExactly(false, false, false, true)
//
//        assert((states[3].repos as Lce.Success).data.list).isEmpty()
//    }
//
//    private fun response(repo1: Repo, repo2: Repo, nextPage: Int): RepoSearchResponse {
//        return RepoSearchResponse(listOf(repo1, repo2), nextPage)
//    }
//
//    @Test fun loadMore() = runBlocking {
//        interactor.search(QUERY) willReturn response(REPO_1, REPO_2, 2)
//        interactor.searchNextPage(QUERY, 2) willReturn response(REPO_3, REPO_4, 3)
//
//        viewModel.setQuery(QUERY)
//        viewModel.loadNextPage()
//
//        ResourceTester(states.map { it.repos })
//                .success().success().loading().success().success().success()
//
//        assert(states.map { it.repos.map { it.loadingMore }.orElse(false) })
//                .containsExactly(false, false, false, false, true, false)
//
//        assert((states[5].repos as Lce.Success).data.list)
//                .isEqualTo(listOf(REPO_1, REPO_2, REPO_3, REPO_4))
//    }
//
//    @Test fun errorLoadingMore() = runBlocking {
//        fragment.requireActivity() willReturn mock()
//        interactor.search(QUERY) willReturn response(REPO_1, REPO_2, 2)
//        interactor.searchNextPage(QUERY, 2) willThrow RuntimeException(ERROR)
//
//        viewModel.setQuery(QUERY)
//        viewModel.loadNextPage()
//
//        ResourceTester(states.map { it.repos })
//                .success().success().loading().success().success().success()
//
//        assert(states.map { it.repos.map { it.loadingMore }.orElse(false) })
//                .containsExactly(false, false, false, false, true, false)
//
//        assert((states[3].repos as Lce.Success).data.list)
//                .containsExactly(REPO_1, REPO_2)
//
//        verify(navigationController).showError(any(), eq(ERROR))
//    }

    companion object {
        private const val QUERY = "query"
        private const val ERROR = "error"
    }
}
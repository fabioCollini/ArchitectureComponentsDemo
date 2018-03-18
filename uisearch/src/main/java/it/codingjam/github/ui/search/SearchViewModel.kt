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

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.nalulabs.prefs.string
import it.codingjam.github.NavigationController
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.Coroutines
import it.codingjam.github.util.LiveDataDelegate
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.vo.Lce
import it.codingjam.github.vo.orElse
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(
        private val githubInteractor: GithubInteractor,
        private val navigationController: NavigationController,
        private val coroutines: Coroutines,
        prefs: SharedPreferences
) : ViewModel() {

    private var lastSearch by prefs.string("")

    val liveData = LiveDataDelegate(SearchViewState(lastSearch))

    private var state by liveData

    val uiActions = UiActionsLiveData()

    fun setQuery(originalInput: String) = coroutines {
        lastSearch = originalInput
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        if (!state.repos.map { it.searchInvoked }.orElse(false) || input != state.query) {
            reloadData(input)
        }
    }

    private suspend fun reloadData(input: String) {
        state = state.copy(query = input, repos = Lce.Loading)
        state = try {
            val (items, nextPage) = githubInteractor.search(input)
            state.copy(repos = Lce.Success(ReposViewState(items, nextPage, true)))
        } catch (e: Exception) {
            state.copy(repos = Lce.Error(e))
        }
    }

    fun loadNextPage() = coroutines {
        (state.repos as? Lce.Success)
                ?.let { (data) ->
                    val query = state.query
                    val nextPage = data.nextPage
                    if (!query.isEmpty() && nextPage != null && !data.loadingMore) {
                        state = state.copy(repos = Lce.Success(data.copy(loadingMore = true)))
                        try {
                            val (items, newNextPage) = githubInteractor.searchNextPage(query, nextPage)
                            state = state.copy(repos = Lce.Success(data.copy(
                                    list = data.list + items,
                                    nextPage = newNextPage,
                                    loadingMore = false
                            )))
                        } catch (t: Exception) {
                            state = state.copy(repos = Lce.Success(data.copy(loadingMore = false)))
                            uiActions { navigationController.showError(it, t.message) }
                        }
                    }
                }
    }

    fun refresh() = coroutines {
        val query = state.query
        if (!query.isEmpty()) {
            reloadData(query)
        }
    }

    fun openRepoDetail(id: RepoId) =
            uiActions { navigationController.navigateToRepo(it, id) }

    override fun onCleared() {
        coroutines.cancel()
    }
}

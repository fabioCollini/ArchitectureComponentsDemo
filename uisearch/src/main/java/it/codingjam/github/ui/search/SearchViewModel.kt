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
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.util.ViewStateHolder
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

    val state = ViewStateHolder(coroutines, SearchViewState(lastSearch))

    val uiActions = UiActionsLiveData(coroutines)

    fun setQuery(originalInput: String) {
        lastSearch = originalInput
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        if (!state().repos.map { it.searchInvoked }.orElse(false) || input != state().query) {
            state.updateOnUi { it.copy(query = input) }
            reloadData()
        }
    }

    private fun reloadData() = coroutines {
        Lce.exec({ lce -> state.update { it.copy(repos = lce) } }) {
            val (items, nextPage) = githubInteractor.search(state().query)
            ReposViewState(items, nextPage, true)
        }
    }

    fun loadNextPage() = coroutines {
        state().repos.doOnData { (_, nextPage, _, loadingMore) ->
            val query = state().query
            if (!query.isEmpty() && nextPage != null && !loadingMore) {
                state.update { it.copyRepos { copy(loadingMore = true) } }
                try {
                    val (items, newNextPage) = githubInteractor.searchNextPage(query, nextPage)
                    state.update {
                        it.copyRepos {
                            copy(list = list + items, nextPage = newNextPage, loadingMore = false)
                        }
                    }
                } catch (t: Exception) {
                    state.update { it.copyRepos { copy(loadingMore = false) } }
                    uiActions { navigationController.showError(it, t.message) }
                }
            }
        }
    }

    fun refresh() {
        if (!state().query.isEmpty()) {
            reloadData()
        }
    }

    fun openRepoDetail(id: RepoId) =
            uiActions.executeOnUi { navigationController.navigateToRepo(it, id) }

    override fun onCleared() {
        coroutines.cancel()
    }
}

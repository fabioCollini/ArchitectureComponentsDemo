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
import it.codingjam.github.NavigationController
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.util.LiveDataDelegate
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource
import kotlinx.coroutines.experimental.Job
import java.util.*
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
        private val repoRepository: RepoRepository,
        private val navigationController: NavigationController
) : ViewModel() {

    val job = Job()

    val liveData = LiveDataDelegate(SearchViewState())

    private var state by liveData

    val uiActions = UiActionsLiveData()

    suspend fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        if (input != state.query) {
            reloadData(input)
        }
    }

    private suspend fun reloadData(input: String) {
        state = state.copy(input, Resource.Loading, false, null)
        state = try {
            val response = repoRepository.search(input)
            val items = response.items
            state.copy(repos = Resource.Success(items), nextPage = response.nextPage)
        } catch (e: Exception) {
            e.printStackTrace()
            state.copy(repos = Resource.Error(e))
        }
    }

    suspend fun loadNextPage() {
        val query = state.query
        val nextPage = state.nextPage
        if (!query.isEmpty() && nextPage != null && !state.loadingMore) {
            state = state.copy(loadingMore = true)
            try {
                val response = repoRepository.searchNextPage(query, nextPage)
                state = state.copy(repos = state.repos.map { v -> v + response.items }, nextPage = response.nextPage, loadingMore = false)
            } catch (t: Exception) {
                state = state.copy(loadingMore = false)
                uiActions { navigationController.showError(it, t.message) }
            }
        }
    }

    suspend fun refresh() {
        val query = state.query
        if (!query.isEmpty()) {
            reloadData(query)
        }
    }

    fun openRepoDetail(id: RepoId) =
            uiActions { navigationController.navigateToRepo(it, id) }

    override fun onCleared() {
        job.cancel()
    }
}

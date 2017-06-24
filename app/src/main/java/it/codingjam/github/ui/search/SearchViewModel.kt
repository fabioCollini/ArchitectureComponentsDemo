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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import it.codingjam.github.NavigationController
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.util.ViewStateLiveData
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource
import java.util.*
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
        private val repoRepository: RepoRepository,
        private val navigationController: NavigationController
) : ViewModel() {

    private val disposable = CompositeDisposable()

    val liveData = ViewStateLiveData(SearchViewState())

    private var state by liveData

    val uiActions = UiActionsLiveData()

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        if (input != state.query) {
            reloadData(input)
        }
    }

    private fun reloadData(input: String) {
        state = state.copy(input, Resource.Loading, false, null)
        disposable += repoRepository.search(input)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { state = state.copy(repos = Resource.Success(it.items), nextPage = it.nextPage) },
                        { state = state.copy(repos = Resource.Error(it)) }
                )
    }

    fun loadNextPage() {
        val query = state.query
        val nextPage = state.nextPage
        if (!query.isEmpty() && nextPage != null && !state.loadingMore) {
            state = state.copy(loadingMore = true)
            disposable += repoRepository.searchNextPage(query, nextPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { state = state.copy(repos = state.repos.map { v -> v + it.items }, nextPage = it.nextPage, loadingMore = false) },
                            { t ->
                                state = state.copy(loadingMore = false)
                                uiActions { navigationController.showError(it, t.message) }
                            }
                    )
        }
    }

    fun refresh() {
        val query = state.query
        if (!query.isEmpty()) {
            reloadData(query)
        }
    }

    fun openRepoDetail(id: RepoId) =
            uiActions { navigationController.navigateToRepo(it, id) }

    override fun onCleared() = disposable.clear()
}

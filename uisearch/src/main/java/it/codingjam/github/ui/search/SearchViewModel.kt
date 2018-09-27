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
import it.codingjam.github.core.OpenForTesting
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.ViewStateStore
import javax.inject.Inject

@OpenForTesting
class SearchViewModel @Inject constructor(
        private val searchUseCase: SearchUseCase
) : ViewModel() {

    val state = ViewStateStore(searchUseCase.initialState())

    fun setQuery(originalInput: String) = state.dispatchActions(searchUseCase.setQuery(originalInput, state()))

    fun loadNextPage() = state.dispatchActions(searchUseCase.loadNextPage(state()))

    fun refresh() = state.dispatchActions(searchUseCase.refresh(state()))

    fun openRepoDetail(id: RepoId) = state.dispatchSignal(searchUseCase.openRepoDetail(id))

    override fun onCleared() = state.cancel()
}

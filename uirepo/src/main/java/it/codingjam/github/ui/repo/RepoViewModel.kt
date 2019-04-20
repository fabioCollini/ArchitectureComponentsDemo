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

package it.codingjam.github.ui.repo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.codingjam.github.FeatureFragmentScope
import it.codingjam.github.core.OpenForTesting
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.ViewStateStoreFactory
import it.codingjam.github.vo.Lce
import javax.inject.Inject

@OpenForTesting
@FeatureFragmentScope
class RepoViewModel @Inject constructor(
        private val useCase: RepoUseCase,
        private val repoId: RepoId,
        factory: ViewStateStoreFactory
) : ViewModel() {

    val state = factory<RepoViewState>(Lce.Loading, viewModelScope)

    fun reload() = state.dispatchActions(useCase.reload(repoId))

    fun openUserDetail(login: String) = state.dispatchSignal(useCase.openUserDetail(login))
}

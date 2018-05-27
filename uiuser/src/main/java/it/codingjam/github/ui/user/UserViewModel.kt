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

package it.codingjam.github.ui.user

import android.arch.lifecycle.ViewModel
import it.codingjam.github.NavigationController
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.Coroutines
import it.codingjam.github.util.LiveDataDelegate
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.vo.Lce
import javax.inject.Inject

class UserViewModel @Inject constructor(
        private val githubInteractor: GithubInteractor,
        private val navigationController: NavigationController,
        private val coroutines: Coroutines,
        private val login: String
) : ViewModel() {

    val state = LiveDataDelegate<UserViewState>(coroutines, Lce.Loading)

    val uiActions = UiActionsLiveData(coroutines)

    fun load() = coroutines {
        Lce.exec({ lce -> state.update { lce } }) {
            githubInteractor.loadUserDetail(login)
        }
    }

    fun retry() = load()

    fun openRepoDetail(id: RepoId) =
            uiActions.executeOnUi { navigationController.navigateToRepo(it, id) }

    override fun onCleared() = coroutines.cancel()
}

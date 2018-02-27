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

import android.arch.lifecycle.ViewModel
import it.codingjam.github.NavigationController
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoId
import it.codingjam.github.util.Coroutines
import it.codingjam.github.util.LiveDataDelegate
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.vo.Resource
import javax.inject.Inject

class RepoViewModel @Inject constructor(
        private val navigationController: NavigationController,
        private val githubInteractor: GithubInteractor,
        private val coroutines: Coroutines
) : ViewModel() {

    private lateinit var repoId: RepoId

    val liveData = LiveDataDelegate(RepoViewState(Resource.Empty))

    private var state by liveData

    val uiActions = UiActionsLiveData()

    fun retry() = coroutines { reload() }

    fun init(repoId: RepoId) = coroutines {
        this.repoId = repoId
        reload()
    }

    private suspend fun reload() {
        state = state.copy(repoDetail = Resource.Loading)

        state = try {
            val repo = githubInteractor.loadRepo(repoId.owner, repoId.name)
            state.copy(repoDetail = Resource.Success(repo))
        } catch (e: Exception) {
            state.copy(repoDetail = Resource.Error(e))
        }
    }

    fun openUserDetail(login: String) =
            uiActions { navigationController.navigateToUser(it, login) }

    override fun onCleared() {
        coroutines.cancel()
    }
}

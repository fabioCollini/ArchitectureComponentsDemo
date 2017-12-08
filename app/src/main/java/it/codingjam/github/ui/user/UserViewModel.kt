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
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.repository.UserRepository
import it.codingjam.github.util.LiveDataDelegate
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.util.async
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import javax.inject.Inject

class UserViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val repoRepository: RepoRepository,
        private val navigationController: NavigationController
) : ViewModel() {

    val job = Job()

    private lateinit var login: String

    val liveData = LiveDataDelegate(UserViewState(Resource.Empty))

    private var state by liveData

    val uiActions = UiActionsLiveData()

    suspend fun load(login: String) {
        this.login = login
        state = state.copy(Resource.Loading)

        state = try {
            val userDeferred = async { userRepository.loadUser(login) }
            val reposDeferred = async { repoRepository.loadRepos(login) }
            val detail = UserDetail(userDeferred.await(), reposDeferred.await())
            state.copy(userDetail = Resource.Success(detail))
        } catch (e: Exception) {
            state.copy(userDetail = Resource.Error(e))
        }
    }

    fun loadAsync(login: String) = job.async { load(login) }

    suspend fun retry() = load(login)

    fun openRepoDetail(id: RepoId) =
            uiActions { navigationController.navigateToRepo(it, id) }

    override fun onCleared() {
        job.cancel()
    }
}

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
import it.codingjam.github.util.UiScheduler
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import ru.gildor.coroutines.retrofit.Result
import javax.inject.Inject

class UserViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val repoRepository: RepoRepository,
        private val navigationController: NavigationController,
        private val ui: UiScheduler
) : ViewModel() {

    private lateinit var login: String

    val liveData = LiveDataDelegate(UserViewState(Resource.Empty))

    private var state by liveData

    val uiActions = UiActionsLiveData()

    fun load(login: String) = ui {
        this.login = login
        state = state.copy(userDetail = Resource.Loading)

        val userDeferred = async { userRepository.loadUser(login) }
        val reposDeferred = async { repoRepository.loadRepos(login) }
        val result = zip(userDeferred, reposDeferred, ::UserDetail)

        state = state.copy(userDetail = Resource.create(result))
    }

    fun retry() = ui { load(login) }

    fun openRepoDetail(id: RepoId) =
            uiActions { navigationController.navigateToRepo(it, id) }

    override fun onCleared() = ui.cancel()
}

suspend fun <T1 : Any, T2 : Any, R : Any> zip(d1: Deferred<Result<T1>>, d2: Deferred<Result<T2>>, zipper: (T1, T2) -> R): Result<R> {
    val r1 = d1.await()
    return when (r1) {
        is Result.Ok -> {
            val r2 = d2.await()
            when (r2) {
                is Result.Ok -> Result.Ok(zipper(r1.value, r2.value), r2.response)
                is Result.Exception -> r2
                is Result.Error -> r2
            }
        }
        is Result.Exception -> r1
        is Result.Error -> r1
    }
}
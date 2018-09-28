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
import it.codingjam.github.core.OpenForTesting
import it.codingjam.github.core.RepoId
import it.codingjam.github.core.UserDetail
import it.codingjam.github.util.ViewStateStore
import it.codingjam.github.vo.Lce
import kotlinx.coroutines.experimental.CoroutineDispatcher
import javax.inject.Inject

@OpenForTesting
class UserViewModel @Inject constructor(
        private val userUseCase: UserUseCase,
        private val login: String,
        dispatcher: CoroutineDispatcher
) : ViewModel() {

    val state = ViewStateStore<Lce<UserDetail>>(Lce.Loading, dispatcher)

    fun load() = state.dispatchActions(userUseCase.load(login))

    fun retry() = load()

    fun openRepoDetail(id: RepoId) = state.dispatchSignal(userUseCase.openRepoDetail(id))

    override fun onCleared() = state.cancel()
}

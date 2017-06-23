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

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import it.codingjam.github.NavigationController
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.repository.UserRepository
import it.codingjam.github.ui.common.RxViewModel
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource
import javax.inject.Inject

class UserViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val repoRepository: RepoRepository,
        private val navigationController: NavigationController
) : RxViewModel<UserViewState>(UserViewState(Resource.Empty)) {

    private lateinit var login: String

    fun load(login: String) {
        this.login = login
        state = state.copy(Resource.Loading)
        Singles.zip(
                userRepository.loadUser(login).subscribeOn(Schedulers.io()),
                repoRepository.loadRepos(login).subscribeOn(Schedulers.io()),
                ::UserDetail
        )
                .takeUntil(cleared)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { state = state.copy(Resource.Success(it)) },
                        { state = state.copy(Resource.Error(it)) }
                )
    }

    fun retry() {
        load(login)
    }

    fun openRepoDetail(id: RepoId) {
        uiActions.execute { navigationController.navigateToRepo(it, id) }
    }
}

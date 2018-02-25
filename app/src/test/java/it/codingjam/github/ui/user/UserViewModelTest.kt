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

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.v4.app.FragmentActivity
import assertk.assert
import assertk.assertions.containsExactly
import com.nhaarman.mockito_kotlin.mock
import it.codingjam.github.NavigationController
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoId
import it.codingjam.github.core.UserDetail
import it.codingjam.github.test.willReturn
import it.codingjam.github.test.willThrow
import it.codingjam.github.util.TestCoroutines
import it.codingjam.github.util.TestData.REPO_1
import it.codingjam.github.util.TestData.REPO_2
import it.codingjam.github.util.TestData.USER
import it.codingjam.github.vo.Resource
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify

class UserViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val githubInteractor: GithubInteractor = mock()
    val navigationController: NavigationController = mock()
    val activity: FragmentActivity = mock()
    val userViewModel by lazy { UserViewModel(githubInteractor, navigationController, TestCoroutines()) }

    val states = mutableListOf<UserViewState>()

    @Before
    fun setUp() {
        userViewModel.liveData.observeForever({ states.add(it) })
        userViewModel.uiActions.observeForever({ it(activity) })
    }

    @Test
    fun load() {
        runBlocking {
            githubInteractor.loadUserDetail(LOGIN) willReturn UserDetail(USER, listOf(REPO_1, REPO_2))
        }

        userViewModel.load(LOGIN)

        assert(states.map { it.userDetail })
                .containsExactly(
                        Resource.Empty,
                        Resource.Loading,
                        Resource.Success(UserDetail(USER, listOf(REPO_1, REPO_2)))
                )
    }

    @Test
    fun retry() {
        runBlocking {
            githubInteractor.loadUserDetail(LOGIN)
                    .willThrow(RuntimeException(ERROR))
                    .willReturn(UserDetail(USER, listOf(REPO_1, REPO_2)))
        }

        userViewModel.load(LOGIN)
        userViewModel.retry()

        assert(states.map { it.userDetail })
                .containsExactly(
                        Resource.Empty,
                        Resource.Loading,
                        Resource.Error(ERROR),
                        Resource.Loading,
                        Resource.Success(UserDetail(USER, listOf(REPO_1, REPO_2)))
                )
    }

    @Test
    fun openRepoDetail() {
        userViewModel.openRepoDetail(REPO_ID)

        verify(navigationController).navigateToRepo(activity, REPO_ID)
    }

    companion object {
        private val LOGIN = "login"
        private val ERROR = "error"
        private val REPO_ID = RepoId("owner", "name")
    }
}
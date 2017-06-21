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
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Single
import it.codingjam.github.NavigationController
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.repository.UserRepository
import it.codingjam.github.util.TestData.Companion.REPO_1
import it.codingjam.github.util.TestData.Companion.REPO_2
import it.codingjam.github.util.TestData.Companion.USER
import it.codingjam.github.util.TrampolineSchedulerRule
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import java.io.IOException

class UserViewModelTest {
    @get:Rule var mockitoRule = MockitoJUnit.rule()

    @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    val userRepository: UserRepository = mock()
    val repoRepository: RepoRepository = mock()
    val navigationController: NavigationController = mock()
    val activity: FragmentActivity = mock()
    @InjectMocks lateinit var userViewModel: UserViewModel

    val states = mutableListOf<UserViewState>()

    @Before fun setUp() {
        userViewModel.observeForever({ it(activity) }, { states.add(it) })
    }

    @Test fun load() {
        given(userRepository.loadUser(LOGIN))
                .willReturn(Single.just(USER))
        given(repoRepository.loadRepos(LOGIN))
                .willReturn(Single.just(listOf(REPO_1, REPO_2)))

        userViewModel.load(LOGIN)

        assertThat(states)
                .extracting { it.userDetail }
                .containsExactly(
                        Resource.Empty,
                        Resource.Loading,
                        Resource.Success(UserDetail(USER, listOf(REPO_1, REPO_2)))
                )
    }

    @Test fun retry() {
        given(userRepository.loadUser(LOGIN))
                .willReturn(Single.error(IOException(ERROR)))
                .willReturn(Single.just(USER))
        given(repoRepository.loadRepos(LOGIN))
                .willReturn(Single.just(listOf(REPO_1, REPO_2)))

        userViewModel.load(LOGIN)
        userViewModel.retry()

        assertThat(states)
                .extracting { it.userDetail }
                .containsExactly(
                        Resource.Empty,
                        Resource.Loading,
                        Resource.Error(ERROR),
                        Resource.Loading,
                        Resource.Success(UserDetail(USER, listOf(REPO_1, REPO_2)))
                )
    }

    @Test fun openRepoDetail() {
        userViewModel.openRepoDetail(REPO_ID)

        verify(navigationController).navigateToRepo(activity, REPO_ID)
    }

    companion object {
        private val LOGIN = "login"
        private val ERROR = "error"
        private val REPO_ID = RepoId("owner", "name")
    }
}
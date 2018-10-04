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

import assertk.assert
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoId
import it.codingjam.github.core.UserDetail
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.testdata.TestData.USER
import it.codingjam.github.util.states
import it.codingjam.github.vo.Lce
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class UserUseCaseTest {

    val githubInteractor: GithubInteractor = mock()
    val userUseCase = UserUseCase(githubInteractor)

    @Test
    fun load() = runBlocking {
        whenever(githubInteractor.loadUserDetail(LOGIN)).thenReturn(UserDetail(USER, listOf(REPO_1, REPO_2)))

        val states = userUseCase.run { load(LOGIN).states(Lce.Loading) }

        assert(states)
                .containsExactly(
                        Lce.Loading,
                        Lce.Success(UserDetail(USER, listOf(REPO_1, REPO_2)))
                )
    }

    @Test
    fun retry() = runBlocking {
        whenever(githubInteractor.loadUserDetail(LOGIN))
                .thenThrow(RuntimeException(ERROR))
                .thenReturn(UserDetail(USER, listOf(REPO_1, REPO_2)))

        val states = userUseCase.run { load(LOGIN).states(Lce.Loading) } +
                userUseCase.run { load(LOGIN).states(Lce.Loading) }

        assert(states)
                .containsExactly(
                        Lce.Loading,
                        Lce.Error(ERROR),
                        Lce.Loading,
                        Lce.Success(UserDetail(USER, listOf(REPO_1, REPO_2)))
                )
    }

    @Test
    fun openRepoDetail() {
        val (_, params) = userUseCase.openRepoDetail(REPO_ID)
        assert(params).isEqualTo(REPO_ID)

//        verify(navigationController).navigateToRepo(fragment, REPO_ID)
    }

    companion object {
        private val LOGIN = "login"
        private val ERROR = "error"
        private val REPO_ID = RepoId("owner", "name")
    }
}
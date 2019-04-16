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

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoId
import it.codingjam.github.core.UserDetail
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.testdata.TestData.USER
import it.codingjam.github.testdata.on
import it.codingjam.github.testdata.states
import it.codingjam.github.vo.Lce
import org.junit.Test

class UserUseCaseTest {

    private val githubInteractor: GithubInteractor = mock()
    private val userUseCase = UserUseCase(githubInteractor)

    @Test
    fun load() {
        on { githubInteractor.loadUserDetail(LOGIN) } doReturn UserDetail(USER, listOf(REPO_1, REPO_2))

        val states = userUseCase.load(LOGIN).states(Lce.Loading)

        assertThat(states)
                .containsExactly(
                        Lce.Loading,
                        Lce.Success(UserDetail(USER, listOf(REPO_1, REPO_2)))
                )
    }

    @Test
    fun retry() {
        on { githubInteractor.loadUserDetail(LOGIN) }
                .thenThrow(RuntimeException(ERROR))
                .thenReturn(UserDetail(USER, listOf(REPO_1, REPO_2)))

        val states = userUseCase.load(LOGIN).states(Lce.Loading) +
                userUseCase.load(LOGIN).states(Lce.Loading)

        assertThat(states)
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
        assertThat(params).isEqualTo(REPO_ID)
    }

    companion object {
        private const val LOGIN = "login"
        private const val ERROR = "error"
        private val REPO_ID = RepoId("owner", "name")
    }
}
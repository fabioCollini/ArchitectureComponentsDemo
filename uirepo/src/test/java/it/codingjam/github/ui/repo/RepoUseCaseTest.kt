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

import assertk.assertThat
import assertk.assertions.containsExactly
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoId
import it.codingjam.github.testdata.TestData
import it.codingjam.github.util.states
import it.codingjam.github.vo.Lce
import it.codingjam.github.vo.debug
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RepoUseCaseTest {

    val interactor: GithubInteractor = mock()

    val useCase = RepoUseCase(interactor)

    @Test
    fun fetchData() = runBlocking {
        whenever(interactor.loadRepo("a", "b")) doReturn TestData.REPO_DETAIL

        val states = states<RepoViewState>(Lce.Loading) { useCase.reload(RepoId("a", "b")) }

        assertThat(states.map { it.debug }).containsExactly("L", "S")
    }

    @Test
    fun errorFetchingData() = runBlocking {
        whenever(interactor.loadRepo("a", "b")) doThrow RuntimeException()

        val states = states<RepoViewState>(Lce.Loading) { useCase.reload(RepoId("a", "b")) }

        assertThat(states.map { it.debug }).containsExactly("L", "E")
    }

    @Test
    fun retry() = runBlocking {
        whenever(interactor.loadRepo("a", "b"))
                .thenThrow(RuntimeException())
                .thenReturn(TestData.REPO_DETAIL)

        val states = states<RepoViewState>(Lce.Loading) { useCase.reload(RepoId("a", "b")) } +
                states<RepoViewState>(Lce.Loading) { useCase.reload(RepoId("a", "b")) }

        assertThat(states.map { it.debug }).containsExactly("L", "E", "L", "S")
    }
}
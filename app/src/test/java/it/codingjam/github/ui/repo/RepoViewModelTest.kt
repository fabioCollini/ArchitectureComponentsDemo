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

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.v4.app.FragmentActivity
import io.reactivex.Single
import it.codingjam.github.NavigationController
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.util.ResourceTester
import it.codingjam.github.util.TestData
import it.codingjam.github.util.TestLiveDataObserver
import it.codingjam.github.util.TrampolineSchedulerRule
import it.codingjam.github.vo.RepoId
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import java.io.IOException

class RepoViewModelTest {

    @get:Rule var mockitoRule = MockitoJUnit.rule()

    @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    @Mock lateinit var repository: RepoRepository

    @Mock lateinit var navigationController: NavigationController

    @Mock lateinit var activity: FragmentActivity

    @InjectMocks lateinit var repoViewModel: RepoViewModel

    private val observer = TestLiveDataObserver<RepoViewState>()

    @Before fun setUp() {
        repoViewModel.observeForever(activity, { observer.onChanged(it) })
    }

    @Test fun fetchData() {
        given(repository.loadRepo("a", "b"))
                .willReturn(Single.just(TestData.REPO_DETAIL))

        repoViewModel.init(RepoId("a", "b"))

        ResourceTester(observer.getValues().map { it.repoDetail })
                .empty().loading().success()
    }

    @Test fun errorFetchingData() {
        given(repository.loadRepo("a", "b"))
                .willReturn(Single.error(IOException()))

        repoViewModel.init(RepoId("a", "b"))

        ResourceTester(observer.getValues().map { it.repoDetail })
                .empty().loading().error()
    }

    @Test
    fun retry() {
        given(repository.loadRepo("a", "b"))
                .willReturn(Single.error(IOException()))
                .willReturn(Single.just(TestData.REPO_DETAIL))

        repoViewModel.init(RepoId("a", "b"))

        repoViewModel.retry()

        ResourceTester(observer.getValues().map { it.repoDetail })
                .empty().loading().error().loading().success()
    }
}
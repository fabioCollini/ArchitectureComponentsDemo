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

import android.app.Application
import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import it.codingjam.github.core.RepoDetail
import it.codingjam.github.core.RepoId
import it.codingjam.github.espresso.TestApplication
import it.codingjam.github.espresso.rule
import it.codingjam.github.testdata.TEST_DISPATCHER
import it.codingjam.github.testdata.TestData.CONTRIBUTOR1
import it.codingjam.github.testdata.TestData.CONTRIBUTOR2
import it.codingjam.github.testdata.TestData.OWNER
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.util.ViewModelFactory
import it.codingjam.github.util.ViewStateStore
import it.codingjam.github.vo.Lce
import it.cosenonjaviste.daggermock.DaggerMock
import it.cosenonjaviste.daggermock.override
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test


class RepoFragmentTest {

    @get:Rule
    val fragmentRule = RepoFragment.rule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    val factory: ViewModelFactory = ViewModelFactory { viewModel }

    val viewModel by lazy {
        mock<RepoViewModel> {
            on(it.state) doReturn ViewStateStore<RepoViewState>(Lce.Loading, CoroutineScope(Dispatchers.Main), TEST_DISPATCHER)
        }
    }

    init {
        val app = ApplicationProvider.getApplicationContext<TestApplication>()
        app.init { c, componentFactory ->
            DaggerMock.override(c, componentFactory, this)
        }
    }

    @Test
    fun testLoading() {
        fragmentRule.launchFragment(RepoId("a", "b"))

        viewModel.state.dispatchState(Lce.Loading)

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testValueWhileLoading() {
        fragmentRule.launchFragment(RepoId("a", "b"))

        viewModel.state.dispatchState(Lce.Loading)
        viewModel.state.dispatchState(Lce.Success(RepoDetail(REPO_1, listOf(CONTRIBUTOR1, CONTRIBUTOR2))))

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.name)).check(matches(
                withText(getString(R.string.repo_full_name, OWNER.login, REPO_1.name))))
        onView(withId(R.id.description)).check(matches(withText(REPO_1.description)))
    }

    private fun getString(@StringRes id: Int, vararg args: Any) = ApplicationProvider.getApplicationContext<Application>().getString(id, *args)
}
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
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.StringRes
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import com.nhaarman.mockito_kotlin.mock
import it.codingjam.github.NavigationController
import it.codingjam.github.R
import it.codingjam.github.ui.common.create
import it.codingjam.github.util.*
import it.codingjam.github.util.TestData.CONTRIBUTOR1
import it.codingjam.github.util.TestData.CONTRIBUTOR2
import it.codingjam.github.util.TestData.OWNER
import it.codingjam.github.util.TestData.REPO_1
import it.codingjam.github.vo.RepoDetail
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RepoFragmentTest {

    @get:Rule var fragmentRule = FragmentTestRule()

    @get:Rule var daggerMockRule = gitHubDaggerMockRule()

    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    val liveData = MutableLiveData<RepoViewState>()

    val viewModel: RepoViewModel = mock()

    val navigationController: NavigationController = mock()

    @Before fun setUp() {
        viewModel.liveData willReturn LiveDataDelegate(RepoViewState(Resource.Empty), liveData)
        viewModel.uiActions willReturn UiActionsLiveData()
    }

    @Test fun testLoading() {
        fragmentRule.launchFragment(RepoFragment.create(RepoId("a", "b")))

        liveData.postValue(RepoViewState(Resource.Empty))
        liveData.postValue(RepoViewState(Resource.Loading))

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())))
    }

    @Test fun testValueWhileLoading() {
        fragmentRule.launchFragment(RepoFragment.create(RepoId("a", "b")))

        liveData.postValue(RepoViewState(Resource.Loading))
        liveData.postValue(RepoViewState(Resource.Success(RepoDetail(REPO_1, listOf(CONTRIBUTOR1, CONTRIBUTOR2)))))

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.name)).check(matches(
                withText(getString(R.string.repo_full_name, OWNER.login, REPO_1.name))))
        onView(withId(R.id.description)).check(matches(withText(REPO_1.description)))
    }

    private fun getString(@StringRes id: Int, vararg args: Any) = InstrumentationRegistry.getTargetContext().getString(id, *args)
}
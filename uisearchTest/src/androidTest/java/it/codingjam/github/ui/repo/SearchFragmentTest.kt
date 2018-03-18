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
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.nhaarman.mockito_kotlin.mock
import it.codingjam.github.R
import it.codingjam.github.ViewLibModule
import it.codingjam.github.espresso.FragmentTestRule
import it.codingjam.github.espresso.espressoDaggerMockRule
import it.codingjam.github.test.willReturn
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.ui.search.SearchFragment
import it.codingjam.github.ui.search.SearchViewModel
import it.codingjam.github.ui.search.SearchViewState
import it.codingjam.github.util.LiveDataDelegate
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.util.ViewModelFactory
import it.codingjam.github.vo.Lce
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchFragmentTest {

    @get:Rule var fragmentRule = FragmentTestRule()

    @get:Rule var daggerMockRule = espressoDaggerMockRule<SearchTestComponent>(ViewLibModule())

    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    val liveData = MutableLiveData<SearchViewState>()

    val factory = ViewModelFactory { viewModel }

    val viewModel by lazy { mock<SearchViewModel>() }

    @Before fun setUp() {
        viewModel.liveData willReturn LiveDataDelegate(SearchViewState(repos = Lce.Loading), liveData)
        viewModel.uiActions willReturn UiActionsLiveData()
    }

    @Test fun testLoading() {
        fragmentRule.launchFragment(SearchFragment())

        liveData.postValue(SearchViewState(repos = Lce.Loading))

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())))
    }

    @Test fun testValueWhileLoading() {
        fragmentRule.launchFragment(SearchFragment())

        liveData.postValue(SearchViewState(repos = Lce.Loading))
        liveData.postValue(SearchViewState(repos = Lce.Success(listOf(REPO_1, REPO_2))))

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }
}
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
import android.arch.lifecycle.MutableLiveData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import com.nhaarman.mockito_kotlin.mock
import it.codingjam.github.ViewLibModule
import it.codingjam.github.core.UserDetail
import it.codingjam.github.espresso.FragmentTestRule
import it.codingjam.github.espresso.espressoDaggerMockRule
import it.codingjam.github.test.willReturn
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.testdata.TestData.USER
import it.codingjam.github.ui.common.create
import it.codingjam.github.util.LiveDataDelegate
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.util.ViewModelFactory
import it.codingjam.github.vo.Lce
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserFragmentTest {

    @get:Rule var fragmentRule = FragmentTestRule()

    @get:Rule var daggerMockRule = espressoDaggerMockRule<UserTestComponent>(ViewLibModule())

    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    val liveData = MutableLiveData<UserViewState>()

    val factory = ViewModelFactory { viewModel }

    val viewModel by lazy { mock<UserViewModel>() }

    @Before fun setUp() {
        viewModel.liveData willReturn LiveDataDelegate(Lce.Loading, liveData)
        viewModel.uiActions willReturn UiActionsLiveData()
    }

    @Test fun testLoading() {
        fragmentRule.launchFragment(UserFragment.create("user"))

        liveData.postValue(Lce.Loading)

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())))
    }

    @Test fun testValueWhileLoading() {
        fragmentRule.launchFragment(UserFragment.create("user"))

        liveData.postValue(Lce.Loading)
        liveData.postValue(Lce.Success(UserDetail(USER, listOf(REPO_1, REPO_2))))

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.user_name)).check(matches(withText(USER.name)))
    }
}
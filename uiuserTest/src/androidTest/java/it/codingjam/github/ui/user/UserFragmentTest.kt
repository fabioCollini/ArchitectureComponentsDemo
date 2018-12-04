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

import android.os.Debug
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.nhaarman.mockitokotlin2.mock
import it.codingjam.github.ViewLibModule
import it.codingjam.github.core.UserDetail
import it.codingjam.github.espresso.espressoDaggerMockRule
import it.codingjam.github.espresso.rule
import it.codingjam.github.testdata.TestData.REPO_1
import it.codingjam.github.testdata.TestData.REPO_2
import it.codingjam.github.testdata.TestData.USER
import it.codingjam.github.util.ViewModelFactory
import it.codingjam.github.util.ViewStateStore
import it.codingjam.github.vo.Lce
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test

class UserFragmentTest {

    @get:Rule val fragmentRule = UserFragment.rule()

    @get:Rule val daggerMockRule = espressoDaggerMockRule<UserTestComponent>(ViewLibModule())

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    val factory = ViewModelFactory { viewModel }

    val viewModel by lazy {
        mock<UserViewModel> {
            on(it.state).thenReturn(ViewStateStore.test(Lce.Loading))
        }
    }

    @Test fun testLoading() {
        Debug.startMethodTracing()
        fragmentRule.launchFragment("user")

        viewModel.state.dispatchState(Lce.Loading)

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())))
        Debug.stopMethodTracing()
    }

    @Test fun testValueWhileLoading() {
        fragmentRule.launchFragment("user")

        viewModel.state.dispatchState(Lce.Loading)
        viewModel.state.dispatchState(Lce.Success(UserDetail(USER, listOf(REPO_1, REPO_2))))

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.user_name)).check(matches(withText(USER.name)))
    }
}
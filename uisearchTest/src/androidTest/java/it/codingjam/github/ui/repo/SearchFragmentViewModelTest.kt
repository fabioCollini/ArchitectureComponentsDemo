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
import android.content.SharedPreferences
import android.os.Bundle
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.pressKey
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.view.KeyEvent
import com.nalulabs.prefs.fake.FakeSharedPreferences
import com.nhaarman.mockito_kotlin.mock
import it.codingjam.github.R
import it.codingjam.github.ViewLibModule
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoSearchResponse
import it.codingjam.github.espresso.FragmentTestRule
import it.codingjam.github.espresso.espressoDaggerMockRule
import it.codingjam.github.test.willReturn
import it.codingjam.github.testdata.TestAppModule
import it.codingjam.github.testdata.TestData.REPO_1
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test

class SearchFragmentViewModelTest {

    @get:Rule var fragmentRule = FragmentTestRule<Unit>(R.navigation.search_nav_graph, R.id.search) { Bundle() }

    @get:Rule
    var daggerMockRule = espressoDaggerMockRule<SearchTestComponent>(TestAppModule(), ViewLibModule())

    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    val prefs: SharedPreferences = FakeSharedPreferences()

    val githubInteractor = mock<GithubInteractor> {
        runBlocking {
            it.search("abc") willReturn RepoSearchResponse(listOf(REPO_1), 1)
        }
    }

    @Test fun testLoad() {
        fragmentRule.launchFragment(Unit)

        onView(withId(R.id.input)).perform(
                typeText("abc"), pressKey(KeyEvent.KEYCODE_ENTER))

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())))
        onView(withText(REPO_1.fullName)).check(matches(isDisplayed()))
    }
}
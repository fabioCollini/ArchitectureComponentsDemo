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

import android.support.annotation.StringRes
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.view.View
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import it.codingjam.github.NavigationController
import it.codingjam.github.R
import it.codingjam.github.util.FragmentTestRule
import it.codingjam.github.util.GitHubDaggerMockRule
import it.codingjam.github.util.TestData.CONTRIBUTOR1
import it.codingjam.github.util.TestData.CONTRIBUTOR2
import it.codingjam.github.util.TestData.OWNER
import it.codingjam.github.util.TestData.REPO_1
import it.codingjam.github.vo.RepoDetail
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.willAnswer
import org.mockito.Mock

class RepoFragmentTest {

    @get:Rule var fragmentRule = FragmentTestRule()

    @get:Rule var daggerMockRule = GitHubDaggerMockRule()

    @Mock lateinit var viewModel: RepoViewModel

    @Mock lateinit var navigationController: NavigationController

    @Test fun testLoading() {
        val fragment = RepoFragment.create(RepoId("a", "b"))
        setState(
                fragment as RepoFragment,
                RepoViewState(Resource.Empty),
                RepoViewState(Resource.Loading)
        )

        fragmentRule.launchFragment(fragment)

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.retry)).check(matches(not<View>(isDisplayed())))
    }

    @Test fun testValueWhileLoading() {
        val fragment = RepoFragment.create(RepoId("a", "b"))
        setState(fragment as RepoFragment,
                RepoViewState(Resource.Empty),
                RepoViewState(Resource.Loading),
                RepoViewState(Resource.Success(RepoDetail(REPO_1, listOf(CONTRIBUTOR1, CONTRIBUTOR2))))
        )

        fragmentRule.launchFragment(fragment)

        onView(withId(R.id.progress_bar)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.name)).check(matches(
                withText(getString(R.string.repo_full_name, OWNER.login, REPO_1.name))))
        onView(withId(R.id.description)).check(matches(withText(REPO_1.description)))
    }

    private fun setState(fragment: RepoFragment, vararg states: RepoViewState) =
            willAnswer { invocation ->
                val observer = invocation.getArgument<(RepoViewState) -> Unit>(1)
                for (viewState in states) {
                    observer(viewState)
                }
                null
            }.given(viewModel).observe(eq(fragment), any())

    private fun getString(@StringRes id: Int, vararg args: Any): String {
        return InstrumentationRegistry.getTargetContext().getString(id, *args)
    }
}
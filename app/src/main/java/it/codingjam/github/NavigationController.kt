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

package it.codingjam.github

import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import it.codingjam.github.ui.common.create
import it.codingjam.github.ui.repo.RepoFragment
import it.codingjam.github.ui.search.SearchFragment
import it.codingjam.github.ui.user.UserFragment
import it.codingjam.github.vo.RepoId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class NavigationController @Inject constructor() {

    fun navigateToSearch(activity: FragmentActivity) {
        val searchFragment = SearchFragment()
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, searchFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToRepo(activity: FragmentActivity, repoId: RepoId) {
        val fragment = RepoFragment.create(repoId)
        val tag = "repo/${repoId.owner}/${repoId.name}"
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToUser(activity: FragmentActivity, login: String) {
        val tag = "user/$login"
        val userFragment = UserFragment.create(login)
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, userFragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun showError(activity: FragmentActivity, error: String?) {
        Snackbar.make(activity.findViewById(android.R.id.content), error ?: "Error", Snackbar.LENGTH_LONG).show()
    }
}

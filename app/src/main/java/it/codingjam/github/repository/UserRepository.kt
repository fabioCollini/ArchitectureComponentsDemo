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

package it.codingjam.github.repository

import io.reactivex.Single
import it.codingjam.github.api.GithubService
import it.codingjam.github.vo.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */
@Singleton
class UserRepository @Inject constructor(private val githubService: GithubService) {

    fun loadUser(login: String): Single<User> {
        return githubService.getUser(login)
    }
}

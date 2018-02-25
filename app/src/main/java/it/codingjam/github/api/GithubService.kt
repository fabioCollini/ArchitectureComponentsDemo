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

package it.codingjam.github.api

import it.codingjam.github.core.Contributor
import it.codingjam.github.core.Repo
import it.codingjam.github.core.User
import it.codingjam.github.util.EnvelopePayload
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("users/{login}")
    fun getUser(@Path("login") login: String): Deferred<User>

    @GET("users/{login}/repos")
    fun getRepos(@Path("login") login: String): Deferred<List<Repo>>

    @GET("repos/{owner}/{name}")
    fun getRepo(@Path("owner") owner: String, @Path("name") name: String): Deferred<Repo>

    @GET("repos/{owner}/{name}/contributors")
    fun getContributors(@Path("owner") owner: String, @Path("name") name: String): Deferred<List<Contributor>>

    @EnvelopePayload("items")
    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String): Deferred<Response<List<Repo>>>

    @EnvelopePayload("items")
    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String, @Query("page") page: Int): Deferred<Response<List<Repo>>>
}

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

import it.codingjam.github.core.GithubRepository
import it.codingjam.github.core.Repo
import it.codingjam.github.core.RepoSearchResponse
import it.codingjam.github.core.User
import retrofit2.HttpException
import retrofit2.Response
import java.util.regex.Pattern

class GithubRepositoryImpl(private val githubService: GithubService) : GithubRepository {

    override suspend fun getRepo(owner: String, name: String) = githubService.getRepo(owner, name).await()

    override suspend fun getContributors(owner: String, name: String) = githubService.getContributors(owner, name).await()

    override suspend fun loadRepos(owner: String): List<Repo> = githubService.getRepos(owner).await()

    override suspend fun searchNextPage(query: String, nextPage: Int): RepoSearchResponse =
            githubService.searchRepos(query, nextPage).await().let { toRepoSearchResponse(it) }

    override suspend fun search(query: String): RepoSearchResponse =
            githubService.searchRepos(query).await().let { toRepoSearchResponse(it) }

    private fun toRepoSearchResponse(response: Response<List<Repo>>): RepoSearchResponse {
        if (response.isSuccessful) {
            return RepoSearchResponse(response.body()
                    ?: emptyList(), extractNextPage(response))
        } else {
            throw HttpException(response)
        }
    }

    private fun extractNextPage(response: Response<List<Repo>>): Int? {
        response.headers().get("link")?.let {
            val matcher = LINK_PATTERN.matcher(it)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    if (matcher.group(2) == NEXT_LINK) {
                        val next = matcher.group(1)
                        val pageMatcher = PAGE_PATTERN.matcher(next)
                        if (!pageMatcher.find() || pageMatcher.groupCount() != 1) {
                            return null
                        }
                        return try {
                            Integer.parseInt(pageMatcher.group(1))
                        } catch (ex: NumberFormatException) {
                            null
                        }

                    }
                }
            }
        }
        return null
    }

    override suspend fun loadUser(login: String): User = githubService.getUser(login).await()

    companion object {

        private val LINK_PATTERN = Pattern
                .compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("page=(\\d)+")
        private const val NEXT_LINK = "next"
    }
}

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
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers
import it.codingjam.github.api.GithubService
import it.codingjam.github.api.RepoSearchResponse
import it.codingjam.github.vo.Repo
import it.codingjam.github.vo.RepoDetail
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class RepoRepository
@Inject constructor(private val githubService: GithubService) {

    fun loadRepos(owner: String): Single<List<Repo>> =
            githubService.getRepos(owner)

    fun loadRepo(owner: String, name: String): Single<RepoDetail> =
            Singles.zip(
                    githubService.getRepo(owner, name).subscribeOn(Schedulers.io()),
                    githubService.getContributors(owner, name).subscribeOn(Schedulers.io()),
                    { repo, contributors -> RepoDetail(repo, contributors) }
            )

    fun searchNextPage(query: String, nextPage: Int): Single<RepoSearchResponse> =
            githubService.searchRepos(query, nextPage).map { toRepoSearchResponse(it) }

    fun search(query: String): Single<RepoSearchResponse> =
            githubService.searchRepos(query).map { toRepoSearchResponse(it) }

    private fun toRepoSearchResponse(response: Response<List<Repo>>): RepoSearchResponse {
        if (response.isSuccessful) {
            return RepoSearchResponse(response.body() ?: emptyList(), extractNextPage(response))
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
                        try {
                            return Integer.parseInt(pageMatcher.group(1))
                        } catch (ex: NumberFormatException) {
                            Timber.w("cannot parse next page from %s", next)
                            return null
                        }

                    }
                }
            }
        }
        return null
    }

    companion object {

        private val LINK_PATTERN = Pattern
                .compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("page=(\\d)+")
        private val NEXT_LINK = "next"
    }
}

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

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import it.codingjam.github.di.AppModule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.nio.charset.StandardCharsets

class GithubServiceTest {

    lateinit var service: GithubService

    val mockWebServer = MockWebServer()

    @Before fun createService() {
        service = AppModule.createService(false, mockWebServer.url("/"))
    }

    @After fun stopService() {
        mockWebServer.shutdown()
    }

    @Test fun getUser() {
        enqueueResponse("user-yigit.json")
        val yigit = service.getUser("yigit").blockingGet()

        val request = mockWebServer.takeRequest()
        assert(request.path).isEqualTo("/users/yigit")

        assert(yigit).isNotNull()
        assert(yigit.avatarUrl).isEqualTo("https://avatars3.githubusercontent.com/u/89202?v=3")
        assert(yigit.company).isEqualTo("Google")
        assert(yigit.blog).isEqualTo("birbit.com")
    }

    @Test fun repos() {
        enqueueResponse("repos-yigit.json")
        val repos = service.getRepos("yigit").blockingGet()

        val request = mockWebServer.takeRequest()
        assert(request.path).isEqualTo("/users/yigit/repos")

        assert(repos.size).isEqualTo(2)

        val (_, _, fullName, _, owner) = repos[0]
        assert(fullName).isEqualTo("yigit/AckMate")

        assert(owner).isNotNull()
        assert(owner.login).isEqualTo("yigit")
        assert(owner.url).isEqualTo("https://api.github.com/users/yigit")

        assert(repos[1].fullName).isEqualTo("yigit/android-architecture")
    }

    @Test fun getContributors() {
        enqueueResponse("contributors.json")
        val contributors = service.getContributors("foo", "bar").blockingGet()
        assert(contributors.size).isEqualTo(3)
        val (login, contributions, avatarUrl) = contributors[0]
        assert(login).isEqualTo("yigit")
        assert(avatarUrl).isEqualTo("https://avatars3.githubusercontent.com/u/89202?v=3")
        assert(contributions).isEqualTo(291)
        assert(contributors[1].login).isEqualTo("guavabot")
        assert(contributors[2].login).isEqualTo("coltin")
    }

    @Test fun search() {
        val header = "<https://api.github.com/search/repositories?q=foo&page=2>; rel=\"next\"," + " <https://api.github.com/search/repositories?q=foo&page=34>; rel=\"last\""
        enqueueResponse("search.json", mapOf("link" to header))
        val response = service.searchRepos("foo").blockingGet()

        assert(response).isNotNull()
        assert(response.body()?.size).isEqualTo(30)
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/" + fileName)
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        headers.forEach { key, value -> mockResponse.addHeader(key, value) }
        mockWebServer.enqueue(mockResponse
                .setBody(source.readString(StandardCharsets.UTF_8)))
    }
}

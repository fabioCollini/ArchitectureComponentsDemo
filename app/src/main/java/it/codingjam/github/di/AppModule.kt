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

package it.codingjam.github.di

import android.app.Application
import android.preference.PreferenceManager
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import it.codingjam.github.BuildConfig
import it.codingjam.github.NavigationController
import it.codingjam.github.api.GithubService
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.ui.repo.RepoViewModel
import it.codingjam.github.util.DenvelopingConverter
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module class AppModule(private val application: Application) {
    @Singleton @Provides fun provideGithubService() =
            createService(BuildConfig.DEBUG, HttpUrl.parse("https://api.github.com/")!!)

    @Provides fun provideNavigationController() = NavigationController()

    @Provides fun providePrefs() = PreferenceManager.getDefaultSharedPreferences(application)

    @Provides fun provideRepoViewModel(navigationController: NavigationController, repository: RepoRepository) =
            RepoViewModel(navigationController, repository)

    companion object {

        fun createService(debug: Boolean, baseUrl: HttpUrl): GithubService {
            val httpClient = OkHttpClient.Builder()

            if (debug) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                httpClient.addInterceptor(logging)
            }

            val gson = GsonBuilder().create()

            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(DenvelopingConverter(gson))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build()
                    .create(GithubService::class.java)
        }
    }
}

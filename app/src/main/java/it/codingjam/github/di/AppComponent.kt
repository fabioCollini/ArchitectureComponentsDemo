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
import dagger.BindsInstance
import dagger.Component
import it.codingjam.github.NavigationController
import it.codingjam.github.api.ApiModule
import it.codingjam.github.ui.repo.RepoViewModel
import it.codingjam.github.ui.search.SearchViewModel
import it.codingjam.github.ui.user.UserViewModel
import javax.inject.Singleton



@Singleton
@Component(modules = [AppModule::class, ApiModule::class])
interface AppComponent {

    val searchViewModel: SearchViewModel

    val repoViewModel: RepoViewModel

    val userViewModel: UserViewModel

    val navigationController: NavigationController

    @Component.Builder interface Builder {
        @BindsInstance fun application(application: Application): Builder

        fun build(): AppComponent
    }
}

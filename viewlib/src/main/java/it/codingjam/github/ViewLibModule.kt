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

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import it.codingjam.github.core.OpenForTesting
import it.codingjam.github.core.utils.ComponentHolder
import it.codingjam.github.core.utils.get
import it.codingjam.github.core.utils.getOrCreate
import it.codingjam.github.util.ViewStateStoreFactory
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@OpenForTesting
@Module
internal class ViewLibModule {
    @Provides
    @Singleton
    fun providePrefs(application: Application): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    @Provides
    @Singleton
    fun viewStateStoreFactory() = ViewStateStoreFactory(Dispatchers.IO)
}

interface ViewLibComponent {
    val prefs: SharedPreferences

    val viewStateStoreFactory: ViewStateStoreFactory

    val navigationController: NavigationController
}

@Component(
        modules = [ViewLibModule::class],
        dependencies = [ViewLibDependencies::class]
)
@Singleton
internal interface ViewLibComponentImpl : ViewLibComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application, dependencies: ViewLibDependencies): ViewLibComponent
    }
}

interface ViewLibDependencies {
    val navigationController: NavigationController
}

val Application.viewLibComponent
    get() = (this as ComponentHolder).getOrCreate {
        DaggerViewLibComponentImpl.factory().create(
                this,
                this.get()
        )
    }
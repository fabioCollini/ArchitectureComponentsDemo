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
import dagger.Module
import dagger.Provides
import it.codingjam.github.util.AndroidCoroutines
import it.codingjam.github.util.AndroidViewModelFactory
import it.codingjam.github.util.Coroutines
import it.codingjam.github.util.ViewModelFactory
import javax.inject.Singleton

@Module class ViewLibModule {
    @Provides @Singleton fun providePrefs(application: Application): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    @Provides fun coroutines(): Coroutines = AndroidCoroutines()

    @Provides fun viewModelFactory(): ViewModelFactory = AndroidViewModelFactory()
}

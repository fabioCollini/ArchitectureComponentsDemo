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

package it.codingjam.github.vo

import ru.gildor.coroutines.retrofit.Result

sealed class Resource<out T> {

    abstract fun <R> map(f: (T) -> R): Resource<R>

    data class Success<out T>(val data: T) : Resource<T>() {
        override fun <R> map(f: (T) -> R): Resource<R> = Success(f(data))
    }

    data class Error(val message: String) : Resource<Nothing>() {
        constructor(t: Throwable) : this(t.message ?: "")

        override fun <R> map(f: (Nothing) -> R): Resource<R> = this
    }

    object Loading : Resource<Nothing>() {
        override fun <R> map(f: (Nothing) -> R): Resource<R> = this
    }

    object Empty : Resource<Nothing>() {
        override fun <R> map(f: (Nothing) -> R): Resource<R> = this
    }

    companion object {
        fun <T : Any> create(res: Result<T>): Resource<T> {
            return when (res) {
                is Result.Ok -> Resource.Success(res.value)
                is Result.Error -> Resource.Error(res.exception)
                is Result.Exception -> Resource.Error(res.exception)
            }
        }
    }
}

fun <T> Resource<T>.orElse(defaultValue: T): T = (this as? Resource.Success)?.data ?: defaultValue


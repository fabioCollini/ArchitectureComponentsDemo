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

import it.codingjam.github.util.ActionsFlow
import it.codingjam.github.util.actionsFlow

sealed class Lce<out T> {

    open val data: T? = null

    abstract fun <R> map(f: (T) -> R): Lce<R>

    inline fun doOnData(f: (T) -> Unit) {
        if (this is Success) {
            f(data)
        }
    }

    data class Success<out T>(override val data: T) : Lce<T>() {
        override fun <R> map(f: (T) -> R): Lce<R> = Success(f(data))
    }

    data class Error(val message: String) : Lce<Nothing>() {
        constructor(t: Throwable) : this(t.message ?: "")

        override fun <R> map(f: (Nothing) -> R): Lce<R> = this
    }

    object Loading : Lce<Nothing>() {
        override fun <R> map(f: (Nothing) -> R): Lce<R> = this
    }
}

inline fun <S> lce(crossinline f: suspend () -> S): ActionsFlow<Lce<S>> {
    return actionsFlow {
        emit { Lce.Loading }
        try {
            val result = f()
            emit { Lce.Success(result) }
        } catch (e: Exception) {
            emit { Lce.Error(e) }
        }
    }
}
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

interface BaseLce

sealed class Lce<out T> : BaseLce {

    abstract fun <R> map(f: (T) -> R): Lce<R>

    data class Success<out T>(val data: T) : Lce<T>() {
        override fun <R> map(f: (T) -> R): Lce<R> = Success(f(data))
    }

    data class Error(val message: String) : Lce<Nothing>() {
        constructor(t: Throwable) : this(t.message ?: "")

        override fun <R> map(f: (Nothing) -> R): Lce<R> = this
    }

    object Loading : Lce<Nothing>() {
        override fun <R> map(f: (Nothing) -> R): Lce<R> = this
    }

    companion object {
        inline fun <T> exec(copy: (Lce<T>) -> Unit, f: () -> T) {
            copy(Lce.Loading)
            try {
                copy(Lce.Success(f()))
            } catch (e: Exception) {
                copy(Lce.Error(e))
            }
        }
    }
}

fun <T> Lce<T>.orElse(defaultValue: T): T = (this as? Lce.Success)?.data ?: defaultValue


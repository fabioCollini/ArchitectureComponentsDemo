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

package it.codingjam.github.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding

abstract class DataBoundViewHolder<T : Any, out V : ViewDataBinding>
private constructor(val binding: V) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    constructor(parent: ViewGroup, factory: (LayoutInflater, ViewGroup, Boolean) -> V) :
            this(factory(LayoutInflater.from(parent.context), parent, false))

    lateinit var item: T

    abstract fun bind(t: T)
}

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

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

class DataBoundListAdapter<T : Any, V : ViewDataBinding>(
        private var factory: (ViewGroup) -> DataBoundViewHolder<T, V>
) : RecyclerView.Adapter<DataBoundViewHolder<T, V>>() {

    private var items: List<T> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<T, V> = factory(parent)

    override fun onBindViewHolder(holder: DataBoundViewHolder<T, V>, position: Int) {
        val item = items[position]
        holder.item = item
        holder.bind(item)
        holder.binding.executePendingBindings()
    }

    fun replace(update: List<T>) {
        this.items = update
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}

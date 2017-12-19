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

package it.codingjam.github.ui.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.codingjam.github.component
import it.codingjam.github.databinding.SearchFragmentBinding
import it.codingjam.github.ui.common.DataBoundListAdapter
import it.codingjam.github.util.viewModelProvider
import it.codingjam.github.vo.orElse

class SearchFragment : Fragment() {

    private val viewModel by viewModelProvider { component.searchViewModel }

    lateinit var binding: SearchFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = DataBoundListAdapter { RepoViewHolder(it, viewModel) }
        binding.repoList.adapter = adapter

        binding.repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager
                        .findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    viewModel.loadNextPage()
                }
            }
        })

        viewModel.liveData.observe(this) {
            binding.state = it
            adapter.replace(it.repos.orElse(emptyList()))
            binding.executePendingBindings()
        }
        viewModel.uiActions.observe(this) { it(activity!!) }

        binding.viewModel = viewModel
    }
}

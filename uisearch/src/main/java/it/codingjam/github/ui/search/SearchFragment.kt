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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.codingjam.github.core.RepoId
import it.codingjam.github.ui.common.DataBoundListAdapter
import it.codingjam.github.ui.search.databinding.SearchFragmentBinding
import it.codingjam.github.util.ErrorSignal
import it.codingjam.github.util.NavigationSignal
import it.codingjam.github.util.viewModel
import it.codingjam.github.viewLibComponent

class SearchFragment : androidx.fragment.app.Fragment() {

    private val navigationController by lazy {
        requireActivity().application.viewLibComponent.navigationController
    }

    private val viewModel by viewModel {
        searchFragmentComponent.viewModel
    }

    private lateinit var binding: SearchFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = DataBoundListAdapter { RepoViewHolder(it, viewModel) }
        binding.results.repoList.adapter = adapter

        binding.results.repoList.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
                val lastPosition = layoutManager
                        .findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    viewModel.loadNextPage()
                }
            }
        })

        binding.lce.setUpdateListener {
            adapter.replace((it as ReposViewState).list)
        }

        viewModel.state.observe(this) {
            binding.state = it
            binding.executePendingBindings()
        }
        viewModel.state.observeSignals(this) {
            when (it) {
                is ErrorSignal -> navigationController.showError(requireActivity(), it.message)
                is NavigationSignal<*> -> navigationController.navigateToRepo(this, it.params as RepoId)
            }
        }

        binding.viewModel = viewModel
    }
}

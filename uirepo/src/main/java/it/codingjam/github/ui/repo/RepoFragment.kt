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

package it.codingjam.github.ui.repo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import it.codingjam.github.core.RepoDetail
import it.codingjam.github.core.RepoId
import it.codingjam.github.ui.common.DataBoundListAdapter
import it.codingjam.github.ui.common.FragmentCreator
import it.codingjam.github.ui.repo.databinding.RepoFragmentBinding
import it.codingjam.github.util.LceContainer
import it.codingjam.github.util.ViewModelFactory
import javax.inject.Inject
import javax.inject.Provider

class RepoFragment : Fragment() {

    lateinit var lceContainer: LceContainer<RepoDetail>

    @Inject lateinit var viewModelProvider: Provider<RepoViewModel>

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        viewModelFactory(this, viewModelProvider) { it.reload() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.observe(this) {
            lceContainer.lce = it
        }
        viewModel.uiActions.observe(this) { it(this) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        lceContainer = LceContainer(requireContext()) {
            viewModel.reload()
        }

        val adapter = DataBoundListAdapter { ContributorViewHolder(it, viewModel) }

        val binding = RepoFragmentBinding.inflate(inflater, lceContainer, true)

        binding.contributorList.adapter = adapter

        lceContainer.setUpdateListener {
            binding.repo = it.repo
            adapter.replace(it.contributors)
            binding.executePendingBindings()
        }

        return lceContainer
    }

    companion object : FragmentCreator<RepoId>(R.navigation.repo_nav_graph, R.id.repo)
}

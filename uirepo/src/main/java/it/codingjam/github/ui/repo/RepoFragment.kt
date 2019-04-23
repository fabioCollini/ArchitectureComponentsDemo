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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.codingjam.github.core.RepoDetail
import it.codingjam.github.core.RepoId
import it.codingjam.github.ui.common.DataBoundListAdapter
import it.codingjam.github.ui.common.FragmentCreator
import it.codingjam.github.ui.repo.databinding.RepoFragmentBinding
import it.codingjam.github.util.ErrorSignal
import it.codingjam.github.util.LceContainer
import it.codingjam.github.util.NavigationSignal
import it.codingjam.github.util.viewModel
import it.codingjam.github.viewLibComponent

class RepoFragment : Fragment() {

    lateinit var lceContainer: LceContainer<RepoDetail>

    private val navigationController by lazy {
        requireActivity().application.viewLibComponent.navigationController
    }

    private val viewModel: RepoViewModel by viewModel {
        repoFragmentComponent.viewModel.apply { reload() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.observe(this) {
            lceContainer.lce = it
        }
        viewModel.state.observeSignals(this) {
            when (it) {
                is ErrorSignal -> navigationController.showError(requireActivity(), it.message)
                is NavigationSignal<*> -> navigationController.navigateToUser(this, it.params as String)
            }
        }
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

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

package it.codingjam.github.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.codingjam.github.NavigationController
import it.codingjam.github.core.RepoId
import it.codingjam.github.core.UserDetail
import it.codingjam.github.ui.common.DataBoundListAdapter
import it.codingjam.github.ui.common.FragmentCreator
import it.codingjam.github.ui.user.databinding.UserFragmentBinding
import it.codingjam.github.util.ErrorSignal
import it.codingjam.github.util.LceContainer
import it.codingjam.github.util.NavigationSignal
import it.codingjam.github.util.ViewModelFactory
import javax.inject.Inject
import javax.inject.Provider

class UserFragment : androidx.fragment.app.Fragment() {

    @Inject lateinit var viewModelProvider: Provider<UserViewModel>

    @Inject lateinit var viewModelFactory: ViewModelFactory

    @Inject lateinit var navigationController: NavigationController

    private val viewModel by lazy {
        viewModelFactory(this, viewModelProvider) { it.load() }
    }

    private lateinit var lceContainer: LceContainer<UserDetail>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        lceContainer = LceContainer(requireContext()) {
            viewModel.load()
        }

        val adapter = DataBoundListAdapter { UserRepoViewHolder(it, viewModel) }

        val binding = UserFragmentBinding.inflate(inflater, lceContainer, true)

        binding.repoList.adapter = adapter

        lceContainer.setUpdateListener {
            binding.user = it.user
            adapter.replace(it.repos)
            binding.executePendingBindings()
        }

        return lceContainer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().application
                .userComponent
                .fragmentComponent()
                .create(param)
                .inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.observe(this) {
            lceContainer.lce = it
        }
        viewModel.state.observeSignals(this) {
            when (it) {
                is ErrorSignal -> navigationController.showError(requireActivity(), it.message)
                is NavigationSignal<*> -> navigationController.navigateToRepo(this, it.params as RepoId)
            }
        }

    }

    companion object : FragmentCreator<String>(R.navigation.user_nav_graph, R.id.user)
}

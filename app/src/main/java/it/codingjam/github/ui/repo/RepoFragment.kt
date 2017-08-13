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

import android.arch.lifecycle.LifecycleFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.codingjam.github.component
import it.codingjam.github.databinding.RepoFragmentBinding
import it.codingjam.github.ui.common.DataBoundListAdapter
import it.codingjam.github.ui.common.FragmentCreator
import it.codingjam.github.ui.common.getParam
import it.codingjam.github.util.viewModelProvider
import it.codingjam.github.vo.RepoId

class RepoFragment : LifecycleFragment() {

    lateinit var binding: RepoFragmentBinding

    private val viewModel by viewModelProvider {
        component.repoViewModel().also { it.init(getParam(this)) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = DataBoundListAdapter { ContributorViewHolder(it, viewModel) }

        binding.contributorList.adapter = adapter
        binding.viewModel = viewModel

        viewModel.liveData.observe(this) {
            binding.state = it
            adapter.replace(it.contributors())
            binding.executePendingBindings()
        }
        viewModel.uiActions.observe(this) { it(activity) }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = RepoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object : FragmentCreator<RepoId>(::RepoFragment)
}

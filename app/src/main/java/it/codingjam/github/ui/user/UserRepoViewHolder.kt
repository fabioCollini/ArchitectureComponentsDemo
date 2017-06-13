package it.codingjam.github.ui.user


import android.view.ViewGroup
import it.codingjam.github.databinding.RepoItemBinding
import it.codingjam.github.ui.common.DataBoundViewHolder
import it.codingjam.github.vo.Repo

class UserRepoViewHolder(parent: ViewGroup, viewModel: UserViewModel) :
        DataBoundViewHolder<Repo, RepoItemBinding>(parent, RepoItemBinding::inflate) {
    init {
        binding.showFullName = false
        binding.root.setOnClickListener {
            viewModel.openRepoDetail(item.repoId())
        }
    }

    override fun bind(t: Repo) {
        binding.repo = t
    }
}


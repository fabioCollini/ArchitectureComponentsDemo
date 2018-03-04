package it.codingjam.github.ui.search


import android.view.ViewGroup
import it.codingjam.github.core.Repo
import it.codingjam.github.ui.common.DataBoundViewHolder
import it.codingjam.github.viewlib.databinding.RepoItemBinding

class RepoViewHolder(parent: ViewGroup, viewModel: SearchViewModel) :
        DataBoundViewHolder<Repo, RepoItemBinding>(parent, RepoItemBinding::inflate) {
    init {
        binding.showFullName = true
        binding.root.setOnClickListener {
            viewModel.openRepoDetail(item.repoId)
        }
    }

    override fun bind(t: Repo) {
        binding.repo = t
    }
}

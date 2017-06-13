package it.codingjam.github.ui.repo


import android.view.ViewGroup
import it.codingjam.github.databinding.ContributorItemBinding
import it.codingjam.github.ui.common.DataBoundViewHolder
import it.codingjam.github.vo.Contributor

class ContributorViewHolder(parent: ViewGroup, private val viewModel: RepoViewModel) :
        DataBoundViewHolder<Contributor, ContributorItemBinding>(parent, ContributorItemBinding::inflate) {

    init {
        binding.viewHolder = this
    }

    override fun bind(t: Contributor) {
        binding.contributor = t
    }

    fun openUserDetail() = viewModel.openUserDetail(item.login)
}
